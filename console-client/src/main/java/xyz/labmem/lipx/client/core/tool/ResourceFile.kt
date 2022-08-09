package xyz.labmem.lipx.client.core.tool

import cn.hutool.core.io.resource.ClassPathResource
import java.io.File
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import kotlin.reflect.KClass

fun getResourceFile(resourcePath: String?): ClassPathResource {
    return ClassPathResource(resourcePath)
}

fun KClass<*>.getJarFile(): File {
    //关键是这行...
    var path = this.java.protectionDomain.codeSource
        .location.file
    path = URLDecoder.decode(path, StandardCharsets.UTF_8) //转换处理中文及空格
    return File(path)
}

fun KClass<*>.getJarParentPath(): String {
    return File(this.getJarFile().absolutePath).parent
}