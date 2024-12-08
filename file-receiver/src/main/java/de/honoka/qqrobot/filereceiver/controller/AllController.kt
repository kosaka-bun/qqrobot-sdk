package de.honoka.qqrobot.filereceiver.controller

import cn.hutool.core.io.FileUtil
import cn.hutool.core.io.IoUtil
import cn.hutool.core.util.IdUtil
import de.honoka.qqrobot.filereceiver.config.MainProperties
import de.honoka.sdk.util.web.ApiResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.InputStream
import kotlin.io.path.Path

@RestController
class AllController(private val mainProperties: MainProperties) {
    
    @PostMapping("/uploadImage")
    fun uploadImage(@RequestParam file: MultipartFile): ApiResponse<*> = run {
        ApiResponse.success(writeToFile("image", "png", file.inputStream))
    }
    
    @PostMapping("/uploadFile")
    fun uploadFile(@RequestParam file: MultipartFile): ApiResponse<*> = run {
        ApiResponse.success(writeToFile("file", "bin", file.inputStream))
    }
    
    private fun writeToFile(subDir: String, fileExt: String, `in`: InputStream): String {
        val path = Path(
            mainProperties.filePathPrefix,
            subDir,
            "${IdUtil.getSnowflakeNextId()}.$fileExt"
        ).toString()
        File(path).run {
            FileUtil.touch(this)
            outputStream().use {
                IoUtil.copy(`in`, it)
            }
            toURI().toString().run {
                if(startsWith("file:///")) return this
                return replaceFirst("file:/", "file:///")
            }
        }
    }
}