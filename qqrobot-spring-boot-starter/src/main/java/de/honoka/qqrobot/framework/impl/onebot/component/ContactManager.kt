package de.honoka.qqrobot.framework.impl.onebot.component

import cn.hutool.http.HttpUtil
import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil
import de.honoka.qqrobot.framework.impl.onebot.OnebotFramework
import de.honoka.qqrobot.framework.impl.onebot.config.OnebotProperties
import de.honoka.qqrobot.framework.impl.onebot.config.urlPrefix
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class ContactManager(private val onebotProperties: OnebotProperties) {
    
    companion object {
        
        private val log = LoggerFactory.getLogger(ContactManager::class.java)
    }
    
    data class Contact(val name: String)
    
    data class Group(val name: String, val memberList: Map<Long, Contact>)
    
    @Volatile
    final lateinit var friends: Map<Long, Contact>
        private set
    
    @Volatile
    final lateinit var groups: Map<Long, Group>
        private set
    
    @PostConstruct
    private fun init() {
        flush()
    }
    
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
        this.friends = friends
        log.info("读取了${friends.size}个好友信息")
    }
    
    private fun readGroups() {
        val url = "${onebotProperties.urlPrefix}/get_group_list"
        val res = HttpUtil.post(url, "{}", OnebotFramework.HTTP_REQUEST_TIMEOUT).let {
            JSONUtil.parseObj(it)
        }
        val groups = HashMap<Long, Group>()
        val groupsJson = res.getJSONArray("data")
        log.info("读取了${groupsJson.size}个群信息")
        groupsJson.forEach {
            it as JSONObject
            val groupId = it.getLong("group_id")
            val group = Group(it.getStr("group_name"), readGroupMemberList(groupId))
            groups[groupId] = group
        }
        this.groups = groups
    }
    
    private fun readGroupMemberList(groupId: Long): Map<Long, Contact> {
        val url = "${onebotProperties.urlPrefix}/get_group_member_list"
        val res = HttpUtil.post(
            url,
            JSONObject().let {
                it["group_id"] = groupId
                it.toString()
            },
            OnebotFramework.HTTP_REQUEST_TIMEOUT + 3 * 1000
        ).let { JSONUtil.parseObj(it) }
        val memberList = HashMap<Long, Contact>()
        res.getJSONArray("data").forEach {
            it as JSONObject
            var name = it.getStr("card")
            if(name?.isNotBlank() == true) name = it.getStr("nickname")
            val qq = it.getLong("user_id")
            val contact = Contact(name)
            memberList[qq] = contact
        }
        log.info("群${groupId}中有${memberList.size}位成员")
        return memberList
    }
    
    fun searchContact(qq: Long): List<Long?>? {
        if(friends.contains(qq)) return listOf(null, qq)
        groups.forEach {
            if(it.value.memberList.contains(qq)) return listOf(it.key, qq)
        }
        return null
    }
}