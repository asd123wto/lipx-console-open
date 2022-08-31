package xyz.labmem.lipx.server.console.enums


import xyz.labmem.lipx.core.tool.LogInfo.Companion.logs
import xyz.labmem.lipx.server.core.AppContext
import xyz.labmem.lipx.server.core.AppContext.Companion.clientList
import xyz.labmem.lipx.server.labVersion

/**
 * @description: do something
 * @author: liutianyu
 * @date: 2022/8/9 11:41
 */
enum class DisplayEnum {


    HOME,//首页
    CONFIG,//配置
    LOG,//日志
    STATUS;//服务端端状态

    /**
     * 展示内容
     */
    fun content(): String {
        return when (this) {
            HOME -> """
                 _      _____ _______   __     _____                          
                | |    |_   _|  __ \ \ / /    / ____|                         
                | |      | | | |__) \ V /____| (___   ___ _ ____   _____ _ __ 
                | |      | | |  ___/ > <______\___ \ / _ \ '__\ \ / / _ \ '__|
                | |____ _| |_| |    / . \     ____) |  __/ |   \ V /  __/ |   
                |______|_____|_|   /_/ \_\   |_____/ \___|_|    \_/ \___|_|   
                labVersion:$labVersion
            """.trimIndent()

            STATUS -> {
                val title = """
                当前服务状态：${if (AppContext.run) "前进四中" else "节省能源中"}
                已代理列表(size:${clientList.size})：
                _____________________________________________________
                |连接时间       连接IP        连接名称        使用端口    |
                """.trimIndent()
                var content = ""
                clientList.forEach {
                    content += "|${it.value.connectTime}       ${it.value.ip}        ${it.value.name}      ${it.value.port}   |\n"
                }
                return title + content + """
                _____________________________________________________
                """.trimIndent()
            }

            LOG -> {
                var list = ""
                logs.forEach { list += " > $it" }
                return list
            }

            CONFIG -> """
                [port] 服务端口: ${AppContext.cacheData?.port ?: ""}
                [pwd] 密码: ${AppContext.cacheData?.password ?: ""}
                [wls] 白名单IP: ${AppContext.cacheData?.wls.toString()}
            """.trimIndent()
        }
    }

    /**
     * 操作提示
     */
    fun hint(): String {
        return "tips: " + when (this) {
            HOME -> "配置[config] 状态[status] 日志[log] 退出服务端[exit]"
            CONFIG -> "编辑[edit 'key' 'val'] 保存[save] 返回[back]"
            STATUS -> "连接全部[start] 断开全部连接[close] 刷新[r] 返回[back]"
            LOG -> "刷新[r] 返回[back]"
        }
    }

    fun keys(): List<String> {
        return when (this) {
            HOME -> listOf("config", "status", "log", "exit")
            CONFIG -> listOf("edit", "save", "back", "r", "./")
            LOG -> listOf("r", "back", "./")
            STATUS -> listOf("start", "close", "r", "back", "./")
        }
    }
}