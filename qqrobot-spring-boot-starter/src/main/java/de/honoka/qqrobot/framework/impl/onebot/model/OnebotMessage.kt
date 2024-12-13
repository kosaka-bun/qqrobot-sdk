package de.honoka.qqrobot.framework.impl.onebot.model

import cn.hutool.json.JSONArray
import cn.hutool.json.JSONObject
import de.honoka.sdk.util.file.FileUtils
import java.io.File

@Suppress("MemberVisibilityCanBePrivate")
class OnebotMessage(messageJson: JSONArray? = null) : AutoCloseable {

    data class Part(var type: String? = null, var data: JSONObject? = null)
    
    val parts: MutableList<Part> = messageJson?.run { toList(Part::class.java) } ?: arrayListOf()
    
    val first: Part?
        get() = parts.firstOrNull()
    
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
    
    fun addImagePart(path: String, toUri: Boolean) {
        val part = Part("image", JSONObject().also {
            it["file"] = if(toUri) {
                FileUtils.toUriPath(path).apply {
                    externalResources.add(path)
                }
            } else {
                path
            }
        })
        parts.add(part)
    }
    
    fun addFilePart(path: String, fileName: String, toUri: Boolean) {
        val part = Part("file", JSONObject().also {
            it["file"] = if(toUri) {
                FileUtils.toUriPath(path).apply {
                    externalResources.add(path)
                }
            } else {
                path
            }
            it["name"] = fileName
        })
        parts.add(part)
    }
    
    fun toJson(): JSONArray = JSONArray(parts)
    
    fun toRawString(): String = buildString {
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
    }
    
    override fun toString(): String = toJson().toString()
    
    override fun close() {
        externalResources.forEach {
            val file = File(it)
            runCatching {
                if(file.exists()) file.delete()
            }
        }
    }
}
