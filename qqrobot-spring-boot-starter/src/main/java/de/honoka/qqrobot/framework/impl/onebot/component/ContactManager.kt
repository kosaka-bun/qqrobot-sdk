package de.honoka.qqrobot.framework.impl.onebot.component

import cn.hutool.http.HttpUtil
import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil
import de.honoka.qqrobot.framework.config.property.OnebotProperties
import de.honoka.qqrobot.framework.impl.onebot.OnebotFramework
import de.honoka.qqrobot.starter.RobotStarter
import de.honoka.sdk.util.kotlin.code.log
import jakarta.annotation.PostConstruct
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class ContactManager(private val onebotProperties: OnebotProperties) {
    
    data class Contact(val name: String)
    
    data class Group(val name: String, val memberList: ConcurrentHashMap<Long, Contact>)
    
    @Volatile
    var friendCache = ConcurrentHashMap<Long, Contact>()
    
    @Volatile
    var groupCache = ConcurrentHashMap<Long, Group>()
    
    //QQ号到其所属的其中一个群号的映射，用于快速寻找可向某个用户发起临时会话的群
    @Volatile
    var memberToGroupCache = ConcurrentHashMap<Long, Long>()
    
    @PostConstruct
    private fun init() {
        RobotStarter.globalThreadPool.submit {
            flush()
            log.info("读取了${friendCache.size}个好友信息")
            log.info("读取了${groupCache.size}个群信息")
        }
    }
    
    @Scheduled(cron = "0 0/10 * * * ?")
    @Synchronized
    fun flush() {
        readFriends()
        readGroups()
    }
    
    private fun readFriends() {
        val url = "${onebotProperties.urlPrefix}/get_friend_list"
        val res = HttpUtil.post(url, "{}", OnebotFramework.HTTP_REQUEST_TIMEOUT).let {
            JSONUtil.parseObj(it)
        }
        val friends = HashMap<Long, Contact>()
        res.getJSONArray("data").forEach {
            it as JSONObject
            val qq = it.getLong("user_id")
            val contact = Contact(it.getStr("nickname"))
            friends[qq] = contact
        }
        friendCache = ConcurrentHashMap(friends)
    }
    
    private fun readGroups() {
        val url = "${onebotProperties.urlPrefix}/get_group_list"
        val res = HttpUtil.post(url, "{}", OnebotFramework.HTTP_REQUEST_TIMEOUT).let {
            JSONUtil.parseObj(it)
        }
        val groupsJson = res.getJSONArray("data")
        val groups = HashMap<Long, Group>()
        val memberToGroupMap = HashMap<Long, Long>()
        groupsJson.forEach {
            it as JSONObject
            val groupId = it.getLong("group_id")
            val group = Group(it.getStr("group_name"), readGroupMemberList(groupId))
            group.memberList.forEach { m ->
                if(!memberToGroupMap.containsKey(m.key)) {
                    memberToGroupMap[m.key] = groupId
                }
            }
            groups[groupId] = group
        }
        groupCache = ConcurrentHashMap(groups)
        memberToGroupCache = ConcurrentHashMap(memberToGroupMap)
    }
    
    private fun readGroupMemberList(group: Long): ConcurrentHashMap<Long, Contact> {
        val url = "${onebotProperties.urlPrefix}/get_group_member_list"
        val map = ConcurrentHashMap<Long, Contact>()
        runCatching {
            val res = HttpUtil.post(
                url,
                JSONObject().let {
                    it["group_id"] = group
                    it.toString()
                },
                OnebotFramework.HTTP_REQUEST_TIMEOUT + 30 * 1000
            ).let { JSONUtil.parseObj(it) }
            val memberList = HashMap<Long, Contact>()
            res.getJSONArray("data").forEach {
                it as JSONObject
                var name = it.getStr("card")
                if(name?.isNotBlank() != true) name = it.getStr("nickname")
                val qq = it.getLong("user_id")
                val contact = Contact(name)
                memberList[qq] = contact
            }
            map.putAll(memberList)
        }.onFailure { e ->
            log.error("未成功读取群${group}的群成员列表", e)
        }
        return map
    }
    
    fun searchContact(qq: Long): List<Long?>? {
        if(friendCache.containsKey(qq)) return listOf(null, qq)
        if(memberToGroupCache.containsKey(qq)) return listOf(memberToGroupCache[qq], qq)
        return null
    }
    
    fun containsGroup(group: Long): Boolean = groupCache.containsKey(group)
}