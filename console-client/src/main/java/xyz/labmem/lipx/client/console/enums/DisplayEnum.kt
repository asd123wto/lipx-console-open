package xyz.labmem.lipx.client.console.enums

import xyz.labmem.lipx.client.console.Display
import xyz.labmem.lipx.client.labVersion

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
                    [name]连接名称 : 
                    [sip]服务器IP : 
                    [spt]服务器端口 : 
                    [pip]代理IP : 
                    [ppt]代理端口 : 
                    [tpt]转发端口 : 
                    [pwd]密码 : 
                    [wls]访问白名单 : 
                """.trimIndent()
            }

            STATUS -> ""
            CONNECT_LIST -> {
                val title = " 序号     连接名称     服务器IP : 服务器端口[ 代理ip : 代理端口 -> 转发端口 ]     状态 \n"
                var list = ""
                Display.getList().forEachIndexed { i, t ->
                    list += "| $i     ${t.remark}     ${t.serverHost} : ${t.serverPort} [${t.proxyHost}${t.proxyPort} -> ${t.targetPort}]     ${t.status.getCN()} \n"
                }
                return title + list
            }

            LOG -> {
                return ""
            }
        }
    }

    /**
     * 操作提示
     */
    fun hint(): String {
        return "tips: " + when (this) {
            HOME -> "连接列表[list] 状态[status] 退出客户端[exit]"
            CONNECT_INFO -> "编辑[edit 'key' 'val'] 保存[save] 返回[back]"
            STATUS -> "刷新[r] 返回[back]"
            CONNECT_LIST -> """
                新增连接[new '连接名称'#'服务器IP':'服务器端口'@'密码'@[`'代理ip':'代理端口'->'转发端口'`,..]] 连接详情[info '序号'] 删除连接[del '序号'] 
                启动连接[start '序号(, all)'] 断开连接[cut '序号(... all)'] 刷新[r] 返回上级[back]
            """.trimIndent()

            LOG -> "刷新[r] 返回[back]"
        }
    }

    fun keys(): List<String> {
        return when (this) {
            HOME -> listOf("list", "status", "exit")
            CONNECT_LIST -> listOf("new", "info", "del", "start", "cut", "back")
            CONNECT_INFO -> listOf("edit", "save", "back")
            LOG -> listOf("r", "back")
            STATUS -> listOf("r", "back")
        }

    }

}