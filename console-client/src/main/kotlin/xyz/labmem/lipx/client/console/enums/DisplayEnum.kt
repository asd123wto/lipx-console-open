package xyz.labmem.lipx.client.console.enums

import xyz.labmem.lipx.client.console.Display
import xyz.labmem.lipx.client.core.AppContext
import xyz.labmem.lipx.client.core.AppContext.Companion.cacheData
import xyz.labmem.lipx.client.core.AppContext.Companion.connectList
import xyz.labmem.lipx.client.labVersion
import xyz.labmem.lipx.core.tool.LogInfo.Companion.logs

/**
 * @description: do something
 * @author: liutianyu
 * @date: 2022/8/9 11:41
 */
enum class DisplayEnum {


    HOME,//首页
    CONNECT_LIST,//连接列表
    CONNECT_INFO,//连接详情
    LOG,//日志
    STATUS;//客户端状态

    /**
     * 展示内容
     */
    fun content(): String {
        return when (this) {
            HOME -> """
                 _      _____ _______   __      _____ _ _            _   
                | |    |_   _|  __ \ \ / /     / ____| (_)          | |  
                | |      | | | |__) \ V /_____| |    | |_  ___ _ __ | |_ 
                | |      | | |  ___/ > <______| |    | | |/ _ \ '_ \| __|
                | |____ _| |_| |    / . \     | |____| | |  __/ | | | |_ 
                |______|_____|_|   /_/ \_\     \_____|_|_|\___|_| |_|\__|
                labVersion:$labVersion
            """.trimIndent()

            CONNECT_INFO -> {
                return """
                    [name]连接名称 : ${AppContext.infoCache?.remark} 
                    [sip]服务器HOST : ${AppContext.infoCache?.serverHost} 
                    [spt]服务器端口 : ${AppContext.infoCache?.serverPort} 
                    [pip]代理HOST : ${AppContext.infoCache?.proxyHost} 
                    [ppt]代理端口 : ${AppContext.infoCache?.proxyPort}
                    [tpt]转发端口 : ${AppContext.infoCache?.targetPort}
                    [pwd]密码 : ${AppContext.infoCache?.password}
                    [wls]访问白名单 : ${AppContext.infoCache?.wls.toString()}
                """.trimIndent()
            }

            STATUS -> """
                总配置数量:${cacheData.size}         当前通信数量：${connectList.size}
            """.trimIndent()

            CONNECT_LIST -> {
                val title = """
                    ____________________________________________________________________________________
                    |id     连接名称     服务器HOST : 服务器端口 [ 代理HOST : 代理端口 -> 转发端口 ]     状态| 

                """.trimIndent()
                var list = ""
                Display.getList().forEach { (i, t) ->
                    list += "| $i     ${t.remark}     ${t.serverHost} : ${t.serverPort} [${t.proxyHost} : ${t.proxyPort} -> ${t.targetPort}]     ${t.status.getCN()} |\n"
                }
                return title + list + """
                    ____________________________________________________________________________________
                    
                    
                """.trimIndent()
            }

            LOG -> {
                var list = ""
                logs.forEach { list += " > $it" }
                return list
            }
        }
    }

    /**
     * 操作提示
     */
    fun hint(): String {
        return "tips: " + when (this) {
            HOME -> "连接列表[list] 状态[status] 日志[log] 退出客户端[exit]"
            CONNECT_INFO -> "编辑[edit 'key' 'val'] 保存[save] 返回[back]"
            STATUS -> "连接全部[start] 断开全部连接[close] 刷新[r] 返回[back]"
            CONNECT_LIST -> """
                        新增连接[new '服务器HOST':'服务器端口'&'密码'@['连接名称'#'代理HOST'%'代理端口'->'转发端口',..]] 
                        连接详情[info 'id'] 
                        删除连接[del 'id'] 
                        启动连接[start 'id(..., all)'] 
                        断开连接[cut 'id(..., all)'] 
                        刷新[r]   返回上级[back]
            """.trimIndent()

            LOG -> "刷新[r] 返回[back]"
        }
    }

    fun keys(): List<String> {
        return when (this) {
            HOME -> listOf("list", "status", "log", "exit")
            CONNECT_LIST -> listOf("new", "info", "del", "start", "cut", "back", "r", "./")
            CONNECT_INFO -> listOf("edit", "save", "back", "./")
            LOG -> listOf("r", "back", "./")
            STATUS -> listOf("start", "close", "r", "back", "./")
        }

    }

}