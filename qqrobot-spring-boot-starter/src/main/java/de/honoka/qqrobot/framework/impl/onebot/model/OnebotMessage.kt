package de.honoka.qqrobot.framework.impl.onebot.model

import cn.hutool.json.JSONArray
import cn.hutool.json.JSONObject
import java.io.File

@Suppress("MemberVisibilityCanBePrivate")
class OnebotMessage(messageJson: JSONArray? = null) : AutoCloseable {

    data class Part(
        
        var type: String? = null,
        
        var data: JSONObject? = null
    )
    
    val parts: MutableList<Part> = run {
        messageJson ?: return@run ArrayList<Part>()
        messageJson.toList(Part::class.java)
    }
    
    val first: Part?
        get() = if(parts.isEmpty()) null else parts[0]
    
    private val externalResources: MutableList<String> = ArrayList()
    
    fun addTextPart(str: String) {
        val part = Part("text", JSONObject().also {
            it["text"] = str
        })
        parts.add(part)
    }
    
    fun addAtPart(qq: Long) {
        val part = Part("at", JSONObject().also {
            it["qq"] = qq
        })
        parts.add(part)
    }
    
    fun addImagePart(path: String) {
        val part = Part("image", JSONObject().also {
            it["file"] = File(path).toURI().toASCIIString().run {
                if(!contains("file:///")) {
                    return@run replace("file:/", "file:///")
                }
                this
            }
        })
        parts.add(part)
        externalResources.add(path)
    }
    
    fun addFilePart(path: String, fileName: String) {
        val part = Part("file", JSONObject().also {
            it["file"] = File(path).toURI().toASCIIString()
            it["name"] = fileName
        })
        parts.add(part)
        externalResources.add(path)
    }
    
    fun toJson(): JSONArray = JSONArray(parts)
    
    fun toRawString(): String = StringBuilder().run {
        parts.forEach {
            val str = when(it.type) {
                "text" -> it.data?.getStr("text")
                "at" -> "@${it.data?.getLong("qq")}"
                "image" -> "[图片]"
                "file" -> "[文件]"
                else -> null
            }
            str?.let { append(str) }
        }
        toString()
    }
    
    override fun toString(): String = toJson().toString()
    
    override fun close() = externalResources.forEach {
        val file = File(it)
        runCatching {
            if(file.exists()) file.delete()
        }
    }
}
