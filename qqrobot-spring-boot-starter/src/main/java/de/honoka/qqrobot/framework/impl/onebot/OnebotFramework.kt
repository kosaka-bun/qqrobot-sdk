package de.honoka.qqrobot.framework.impl.onebot

import cn.hutool.core.io.FileUtil
import cn.hutool.core.io.IoUtil
import cn.hutool.core.util.IdUtil
import cn.hutool.http.HttpUtil
import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil
import de.honoka.qqrobot.framework.Framework
import de.honoka.qqrobot.framework.impl.onebot.component.ContactManager
import de.honoka.qqrobot.framework.impl.onebot.config.*
import de.honoka.qqrobot.framework.impl.onebot.model.OnebotMessage
import de.honoka.qqrobot.framework.model.RobotMessageType.*
import de.honoka.qqrobot.framework.model.RobotMultipartMessage
import de.honoka.qqrobot.starter.RobotBasicProperties
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.socket.*
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import java.io.File
import java.io.InputStream
import java.util.concurrent.ConcurrentHashMap
import javax.annotation.PreDestroy
import kotlin.io.path.Path

/**
 * 基于OneBot 11标准进行实现<br>
 *
 * starter仅对一个版本的OneBot标准进行实现
 */
@Component
class OnebotFramework(
    private val basicProperties: RobotBasicProperties,
    private val onebotProperties: OnebotProperties,
    private val contactManager: ContactManager
) : Framework<OnebotMessage>() {

    companion object {

        private val log = LoggerFactory.getLogger(OnebotFramework::class.java)
        
        const val TIME_TO_WAIT_ONLINE = 5000L
        
        const val HTTP_REQUEST_TIMEOUT = 3000
    }
    
    inner class WebSocketHandlerImpl : WebSocketHandler {
        
        override fun afterConnectionEstablished(session: WebSocketSession) {
            checkIsOnline()
            if(online) frameworkCallback.onStartup()
        }
        
        override fun handleMessage(session: WebSocketSession, message: WebSocketMessage<*>) {
            //在线时长不足5秒，忽略传入的消息，避免响应用户在上线前很久时所发的命令
            val shouldIgnore = message !is TextMessage || !online ||
                System.currentTimeMillis() - onlineTimePoint < TIME_TO_WAIT_ONLINE
            if(shouldIgnore) return
            runCatching {
                val json = JSONUtil.parseObj(message.payload)
                when(json.getStr("post_type")) {
                    "message" -> handleMessageEvent(json)
                    "notice" -> handleNoticeEvent(json)
                }
            }
        }
        
        private fun handleMessageEvent(json: JSONObject) {
            val qq = json.getLong("user_id")
            val robotMessage = transform(OnebotMessage(json.getJSONArray("message")))
            when(json.getStr("message_type")) {
                "private" -> frameworkCallback.onPrivateMsg(qq, robotMessage)
                "group" -> {
                    val group = json.getLong("group_id")
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
            frameworkCallback.onShutdown()
            if(started) {
                //不直接使用reboot，以确保不会和自动重连任务相冲突，导致执行两次重启
                checkIsActive()
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
    
    private val groupToMuteEndTimePointMap = ConcurrentHashMap<Long, Long>()

    @Synchronized
    override fun boot() {
        started = true
        if(webSocketSession?.isOpen == true) return
        val url = "${onebotProperties.websocketUrlPrefix}/event"
        webSocketSession = webSocketClient.doHandshake(webSocketHandler, url).get()
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
        if(!started || webSocketSession?.isOpen != true) {
            online = false
            return
        }
        val url = "${onebotProperties.urlPrefix}/get_status"
        online = try {
            val res = HttpUtil.post(url, "{}", HTTP_REQUEST_TIMEOUT).let { JSONUtil.parseObj(it) }
            val online = res.getByPath("data.online") as Boolean
            //此前不在线，现在在线，更新上线时间点
            if(online && !this.online) onlineTimePoint = System.currentTimeMillis()
            online
        } catch(t: Throwable) {
            false
        }
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

    override fun transform(group: Long?, qq: Long, message: RobotMultipartMessage): OnebotMessage {
        val onebotMessage = OnebotMessage()
        message.messageList.forEach {
            when(it.type) {
                TEXT -> {
                    val str = it.content as String?
                    if(str?.isNotEmpty() == true) onebotMessage.addTextPart(str)
                }
                AT -> onebotMessage.addAtPart(it.content as Long)
                IMAGE -> {
                    val path = Path(
                        onebotProperties.imagePath,
                        "${IdUtil.getSnowflakeNextId()}.png"
                    ).toString()
                    writeResouceToFile(it.content as InputStream, path)
                    onebotMessage.addImagePart(path)
                }
                FILE -> {
                    val fileName = "${IdUtil.getSnowflakeNextId()}.bin"
                    val displayFileName = it.others["fileName"] as String? ?: fileName
                    val path = Path(onebotProperties.fileToUploadPath, fileName).toString()
                    writeResouceToFile(it.content as InputStream, path)
                    onebotMessage.addFilePart(path, displayFileName)
                }
                else -> {}
            }
        }
        return onebotMessage
    }

    override fun transform(onebotMessage: OnebotMessage): RobotMultipartMessage {
        val message = RobotMultipartMessage()
        onebotMessage.parts.forEach {
            when(it.type) {
                "text" -> message.add(TEXT, it.data?.getStr("text"))
                "at" -> message.add(AT, it.data?.getLong("qq"))
            }
        }
        return message
    }
    
    override fun sendPrivateMsg(qq: Long, message: RobotMultipartMessage) {
        val contact = contactManager.searchContact(qq) ?: return
        sendMessage(contact[0], qq, transform(contact[0], qq, message))
    }

    override fun sendGroupMsg(group: Long, message: RobotMultipartMessage) {
        if(group !in contactManager.groups || isMuted(group)) return
        sendMessage(group, null, transform(group, 0, message))
    }
    
    private fun sendMessage(group: Long?, qq: Long?, message: OnebotMessage) {
        val apiName = if(group != null) "send_group_msg" else "send_private_msg"
        val url = "${onebotProperties.urlPrefix}/$apiName"
        for(i in 1..3) {
            try {
                val res = HttpUtil.post(
                    url,
                    JSONObject().let {
                        if(group != null) {
                            it["group_id"] = group
                        } else {
                            it["user_id"] = qq
                        }
                        it["message"] = message.parts
                        toString()
                    },
                    HTTP_REQUEST_TIMEOUT
                ).let { JSONUtil.parseObj(it) }
                val retcode = res.getInt("retcode")
                if(retcode != 0) throw Exception("retcode = $retcode")
            } catch(t: Throwable) {
                log.error("\n消息发送失败！已尝试次数：${i + 1}\n要发送的内容：\n${message.toRawString()}", t)
                if(!basicProperties.resendOnSendFailed) break
                continue
            }
            if(i > 1) log.info("\n消息重发成功：\n${message.toRawString()}")
            break
        }
        message.close()
    }

    override fun getGroupName(group: Long): String = contactManager.groups[group]?.name ?: "【未知】"

    override fun getNickOrCard(group: Long, qq: Long): String {
        val groupObj = contactManager.groups[group]
        val nickname = contactManager.friends[qq]?.name ?: "【未知】"
        return groupObj?.memberList?.get(qq)?.name ?: nickname
    }

    override fun isMuted(group: Long): Boolean {
        if(!groupToMuteEndTimePointMap.containsKey(group)) return false
        return System.currentTimeMillis() > (groupToMuteEndTimePointMap[group] ?: 0)
    }
}