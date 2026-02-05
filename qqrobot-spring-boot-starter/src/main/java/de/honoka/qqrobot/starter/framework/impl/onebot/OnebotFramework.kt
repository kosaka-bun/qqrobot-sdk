package de.honoka.qqrobot.starter.framework.impl.onebot

import cn.hutool.core.exceptions.ExceptionUtil
import cn.hutool.core.io.FileUtil
import cn.hutool.core.io.IoUtil
import cn.hutool.core.util.IdUtil
import cn.hutool.http.HttpUtil
import cn.hutool.json.JSONArray
import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil
import de.honoka.qqrobot.framework.api.message.RobotMessage
import de.honoka.qqrobot.framework.api.message.RobotMessageTypes
import de.honoka.qqrobot.framework.api.message.RobotMultipartMessage
import de.honoka.qqrobot.starter.common.NoContactException
import de.honoka.qqrobot.starter.common.RobotMutedException
import de.honoka.qqrobot.starter.framework.TypedRobotFramework
import de.honoka.qqrobot.starter.framework.config.OnebotProperties
import de.honoka.qqrobot.starter.framework.impl.onebot.component.ContactManager
import de.honoka.qqrobot.starter.framework.impl.onebot.model.OnebotMessage
import de.honoka.qqrobot.starter.util.GlobalThreadPools
import de.honoka.sdk.util.kotlin.text.toJsonWrapper
import de.honoka.sdk.util.kotlin.various.log
import jakarta.annotation.PreDestroy
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.socket.*
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import java.io.File
import java.io.InputStream
import java.net.SocketTimeoutException
import java.util.concurrent.ConcurrentHashMap
import kotlin.io.path.Path

/**
 * 基于OneBot 11标准进行实现<br>
 *
 * starter仅对一个版本的OneBot标准进行实现
 */
@Component
class OnebotFramework(
    private val onebotProperties: OnebotProperties,
    private val contactManager: ContactManager
) : TypedRobotFramework<OnebotMessage>() {

    companion object {

        const val TIME_TO_WAIT_ONLINE = 5000L
    }
    
    inner class WebSocketHandlerImpl : WebSocketHandler {
        
        override fun afterConnectionEstablished(session: WebSocketSession) {
            log.info("WebSocket连接已建立")
            /*
             * 若不采用异步，则在这个方法返回之前，webSocketClient.execute().get()语句
             * 都不会返回。
             * 这条语句在synchronized方法中，checkIsOnline也是一个synchronized方法，不采用
             * 异步将会导致循环等待。
             */
            GlobalThreadPools.pool.submit {
                checkIsOnline()
                if(online) frameworkCallback.onStartup()
            }
        }
        
        override fun handleMessage(session: WebSocketSession, message: WebSocketMessage<*>) {
            if(message !is TextMessage) return
            try {
                val json = JSONUtil.parseObj(message.payload)
                when(json.getStr("post_type")) {
                    "message" -> handleBotMessageEvent(json)
                    "notice" -> handleNoticeEvent(json)
                }
            } catch(t: Throwable) {
                log.error("", t)
            }
        }
        
        private fun handleBotMessageEvent(json: JSONObject) {
            //在线时长不足，忽略传入的消息，避免响应用户在上线前很久时所发的命令
            if(!online || System.currentTimeMillis() - onlineTimePoint < TIME_TO_WAIT_ONLINE) return
            val group = json.getLong("group_id")
            val qq = json.getLong("user_id")
            val robotMessage = json["message"].let {
                when(it) {
                    is String -> RobotMultipartMessage.of(it)
                    is JSONArray -> typedTransform(OnebotMessage(it))
                    else -> throw Exception("Unknown message type")
                }
            }
            when(json.getStr("message_type")) {
                "private" -> {
                    if(group != null && !contactManager.memberToGroupCache.containsKey(qq)) {
                        contactManager.memberToGroupCache[qq] = group
                    }
                    frameworkCallback.onPrivateMsg(qq, robotMessage)
                }
                "group" -> {
                    if(!contactManager.groupCache.containsKey(group)) {
                        //临时添加一个群信息
                        contactManager.groupCache[group] = ContactManager.Group(
                            "【未知】", ConcurrentHashMap()
                        )
                    }
                    frameworkCallback.onGroupMsg(group, qq, robotMessage)
                }
            }
        }
        
        private fun handleNoticeEvent(json: JSONObject) {
            val noticeType = json.getStr("notice_type")
            val subType = json.getStr("sub_type")
            when(noticeType) {
                "group_ban" -> {
                    if(json.getLong("user_id") != json.getLong("self_id")) return
                    val groupId = json.getLong("group_id")
                    when(subType) {
                        "ban" -> groupToMuteEndTimePointMap[groupId] = System.currentTimeMillis() +
                            json.getLong("duration") * 1000L
                        "lift_ban" -> groupToMuteEndTimePointMap[groupId] = 0L
                    }
                }
            }
        }
        
        override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
            log.info("WebSocket连接已断开")
            GlobalThreadPools.pool.submit {
                frameworkCallback.onShutdown()
                if(started) {
                    //不直接使用reboot，以确保不会和自动重连任务相冲突，导致执行两次重启
                    checkIsActive()
                }
            }
        }
        
        override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
            log.error("", exception)
        }
        
        override fun supportsPartialMessages(): Boolean = false
    }
    
    private val webSocketClient = StandardWebSocketClient()
    
    private val webSocketHandler = WebSocketHandlerImpl()
    
    private var webSocketSession: WebSocketSession? = null
    
    /**
     * 是否已手动启动。<br>
     *
     * 若尚未手动启动，或已手动关闭，则不应触发自动重连。
     */
    @Volatile
    private var started = false

    @Volatile
    private var online = false
    
    @Volatile
    private var onlineTimePoint = 0L
    
    /**
     * 在群中被禁言结束时的时间点
     */
    private val groupToMuteEndTimePointMap = ConcurrentHashMap<Long, Long>()

    @Synchronized
    override fun boot() {
        started = true
        if(webSocketSession?.isOpen == true) return
        val url = "${onebotProperties.websocketUrlPrefix}/event"
        webSocketSession = webSocketClient.execute(webSocketHandler, url).get()
    }

    @PreDestroy
    @Synchronized
    override fun stop() {
        started = false
        if(webSocketSession?.isOpen == true) {
            webSocketSession?.close()
        }
        webSocketSession = null
    }

    @Synchronized
    override fun reboot() {
        stop()
        boot()
    }

    @Scheduled(cron = "0/10 * * * * ?")
    @Synchronized
    fun checkIsActive() {
        if(!started || webSocketSession?.isOpen == true) return
        reboot()
    }

    @Scheduled(cron = "5/10 * * * * ?")
    @Synchronized
    fun checkIsOnline() {
        val onlineBeforeFlush = online
        if(!started || webSocketSession?.isOpen != true) {
            online = false
            if(onlineBeforeFlush) {
                //此前在线，现在不在线
                log.info("QQ已离线")
            }
            return
        }
        val url = "${onebotProperties.urlPrefix}/get_status"
        online = try {
            HttpUtil.post(url, "{}", 3000).toJsonWrapper().run {
                getBool("data.online")
            }
        } catch(_: Throwable) {
            false
        }
        if(online != onlineBeforeFlush) {
            if(online) {
                //此前不在线，现在在线，更新上线时间点
                log.info("QQ已在线，${TIME_TO_WAIT_ONLINE / 1000L}秒后开始处理消息")
                onlineTimePoint = System.currentTimeMillis()
                GlobalThreadPools.instantPool.submit {
                    Thread.sleep(TIME_TO_WAIT_ONLINE)
                    log.info("已开始处理消息")
                }
            } else {
                //此前在线，现在不在线
                log.info("QQ已离线")
            }
        }
    }
    
    //group与qq参数均未使用
    override fun typedTransform(group: Long?, qq: Long, message: RobotMultipartMessage): OnebotMessage {
        val onebotMessage = OnebotMessage()
        val remoteMode = onebotProperties.fileReceiverPort != null
        message.messageList.forEach {
            when(val content = it.content) {
                is String -> {
                    if(content.isNotEmpty()) onebotMessage.addTextPart(content)
                }
                is RobotMessageTypes.At -> onebotMessage.addAtPart(content.qq)
                is RobotMessageTypes.Image -> {
                    val fileName = "${IdUtil.getSnowflakeNextId()}.png"
                    val path = if(remoteMode) {
                        uploadFileToReceiver(content.content, "uploadImage", fileName)
                    } else {
                        Path(onebotProperties.imagePath, fileName).toString().run {
                            writeResouceToFile(content.content, this)
                            this
                        }
                    }
                    onebotMessage.addImagePart(path, !remoteMode)
                }
                is RobotMessageTypes.File -> {
                    val fileName = "${IdUtil.getSnowflakeNextId()}.bin"
                    val displayFileName = it.others["fileName"] as String? ?: fileName
                    val path = if(remoteMode) {
                        uploadFileToReceiver(content.content, "uploadFile", displayFileName)
                    } else {
                        Path(onebotProperties.fileToUploadPath, fileName).toString().run {
                            writeResouceToFile(content.content, this)
                            this
                        }
                    }
                    onebotMessage.addFilePart(path, displayFileName, !remoteMode)
                }
                else -> {}
            }
        }
        return onebotMessage
    }
    
    private fun typedTransform(message: RobotMultipartMessage): OnebotMessage = run {
        typedTransform(null, 0, message)
    }

    override fun typedTransform(onebotMessage: OnebotMessage): RobotMultipartMessage {
        val message = RobotMultipartMessage()
        onebotMessage.parts.forEach {
            when(it.type) {
                "text" -> message.add(it.data?.getStr("text"))
                "at" -> message.add(RobotMessage.at(it.data!!.getLong("qq")))
            }
        }
        return message
    }
    
    private fun writeResouceToFile(`in`: InputStream, filePath: String) {
        val file = File(filePath)
        try {
            if(file.exists()) file.delete()
            FileUtil.touch(file)
            file.outputStream().use {
                IoUtil.copy(`in`, it)
            }
        } catch(t: Throwable) {
            if(file.exists()) file.delete()
            throw t
        }
    }
    
    private fun uploadFileToReceiver(`in`: InputStream, urlPath: String, fileName: String): String {
        val url = "${onebotProperties.fileReceiverUrlPrefix}/$urlPath"
        val content = IoUtil.readBytes(`in`)
        HttpUtil.createPost(url).run {
            form("file", content, fileName)
            timeout(10 * 1000)
            val res = JSONUtil.parseObj(execute().body())
            if(res.getBool("status") != true) {
                throw Exception("File receiver returns failed status: ${res.getStr("msg")}")
            }
            return res.getStr("data")
        }
    }
    
    override fun sendPrivateMsg(qq: Long, message: RobotMultipartMessage) {
        val contact = contactManager.searchContact(qq) ?: throw NoContactException(qq)
        sendMessage(contact[0], qq, typedTransform(message))
    }
    
    override fun sendGroupMsg(group: Long, message: RobotMultipartMessage) {
        if(!contactManager.containsGroup(group)) throw NoContactException(group = group)
        if(isMuted(group)) throw RobotMutedException(group)
        sendMessage(group, null, typedTransform(message))
    }
    
    private fun sendMessage(group: Long?, qq: Long?, message: OnebotMessage) {
        val apiName = if(qq == null) "send_group_msg" else "send_private_msg"
        val url = "${onebotProperties.urlPrefix}/$apiName"
        message.use { m ->
            var throwable: Throwable? = null
            for(i in 1..3) {
                try {
                    val res = HttpUtil.post(
                        url,
                        JSONObject().let {
                            it["group_id"] = group
                            it["user_id"] = qq
                            it["message"] = m.parts
                            it.toString()
                        },
                        5000
                    ).let { JSONUtil.parseObj(it) }
                    val retcode = res.getInt("retcode")
                    val errMsg = res.getStr("message")
                    if(retcode != 0) error("retcode = $retcode，errMsg = $errMsg")
                } catch(t: Throwable) {
                    when(ExceptionUtil.getRootCause(t)) {
                        is SocketTimeoutException -> {
                            log.warn("\n消息可能未发送成功\n要发送的内容：\n${m.toRawString()}", t)
                        }
                        else -> {
                            throwable = t
                            log.error("\n消息发送失败！已尝试次数：$i\n要发送的内容：\n${m.toRawString()}", t)
                            if(!basicProperties.resendOnSendFailed) break
                            continue
                        }
                    }
                }
                throwable = null
                if(i > 1) log.info("\n消息重发成功：\n${m.toRawString()}")
                break
            }
            throwable?.let { throw it }
        }
    }

    override fun getGroupName(group: Long): String = contactManager.groupCache[group]?.name ?: "【未知】"

    override fun getNickOrCard(group: Long, qq: Long): String {
        val groupObj = contactManager.groupCache[group]
        val nickname = contactManager.friendCache[qq]?.name ?: "【未知】"
        return groupObj?.memberList?.get(qq)?.name ?: nickname
    }

    override fun isMuted(group: Long): Boolean {
        if(!groupToMuteEndTimePointMap.containsKey(group)) return false
        return System.currentTimeMillis() > (groupToMuteEndTimePointMap[group] ?: 0)
    }
}
