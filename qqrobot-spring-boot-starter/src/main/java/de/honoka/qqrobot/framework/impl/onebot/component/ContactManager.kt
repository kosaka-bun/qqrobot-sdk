package de.honoka.qqrobot.framework.impl.onebot.component

import cn.hutool.http.HttpUtil
import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil
import de.honoka.qqrobot.framework.impl.onebot.OnebotFramework
import de.honoka.qqrobot.framework.impl.onebot.config.OnebotProperties
import de.honoka.qqrobot.framework.impl.onebot.config.urlPrefix
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class ContactManager(private val onebotProperties: OnebotProperties) {
    
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
    }
    
    private fun readGroups() {
        val url = "${onebotProperties.urlPrefix}/get_group_list"
        val res = HttpUtil.post(url, "{}", OnebotFramework.HTTP_REQUEST_TIMEOUT).let {
            JSONUtil.parseObj(it)
        }
        val groups = HashMap<Long, Group>()
        res.getJSONArray("data").forEach {
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
                toString()
            },
            OnebotFramework.HTTP_REQUEST_TIMEOUT
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