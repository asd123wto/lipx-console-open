package xyz.labmem.lipx.client.console

import cn.hutool.core.convert.Convert
import cn.hutool.core.util.RandomUtil
import xyz.labmem.lipx.client.console.enums.DisplayEnum
import xyz.labmem.lipx.client.console.enums.DisplayEnum.*
import xyz.labmem.lipx.client.core.AppContext.Companion.cacheChange
import xyz.labmem.lipx.client.core.AppContext.Companion.cacheData
import xyz.labmem.lipx.client.core.AppContext.Companion.connectList
import xyz.labmem.lipx.client.core.AppContext.Companion.infoCache
import xyz.labmem.lipx.client.core.ConfigData
import xyz.labmem.lipx.client.core.LabSSHPenetrationClient
import xyz.labmem.lipx.core.PortConfig
import xyz.labmem.lipx.client.shutdown
import java.util.*
import kotlin.collections.ArrayList

/**
 * @description: do something
 * @author: liutianyu
 * @date: 2022/8/9 11:40
 */
class Control {

    companion object {

        fun parse(input: String, de: DisplayEnum): Boolean {
            val inputList = input.trim().split(" ")
            val key = inputList[0]
            return if (de.keys().contains(key)) {
                when (de) {
                    HOME -> {
                        if (key == "list") {
                            Display.render(CONNECT_LIST)
                        } else if (key == "status") {
                            ConfigData.refreshList()
                            Display.render(STATUS)
                        } else if (key == "log") {
                            Display.render(LOG)
                        } else if (key == "exit") {
                            shutdown()
                        }
                    }

                    CONNECT_LIST -> {
                        if (key == "new") {
                            try {
                                val addList = ArrayList<PortConfig>()
                                var ot = inputList[1].split(":")
                                val serverHost = ot[0]
                                ot = ot[1].split("&")
                                val serverPort = ot[0]
                                ot = ot[1].split("@")
                                val password = ot[0]
                                ot[1].replace("[", "").replace("]", "").split(",").forEach {
                                    PortConfig().apply {
                                        this.id = RandomUtil.randomString(6)
                                        this.serverHost = serverHost
                                        try {
                                            this.serverPort = serverPort.toInt()
                                        } catch (e: Exception) {
                                            throw Exception("【服务器端口不正确】")
                                        }
                                        this.password = password
                                        var ou = it.split("#")
                                        this.remark = ou[0]
                                        ou = ou[1].split("%")
                                        this.proxyHost = ou[0]
                                        ou = ou[1].split("->")
                                        try {
                                            this.proxyPort = ou[0].toInt()
                                        } catch (e: Exception) {
                                            throw Exception("【代理端口不正确】")
                                        }
                                        try {
                                            this.targetPort = ou[1].toInt()
                                        } catch (e: Exception) {
                                            throw Exception("【转发端口不正确】")
                                        }
                                        addList.add(this)
                                    }
                                }
                                ConfigData.write(addList)
                                println("保存成功！")
                            } catch (e: Exception) {
                                println("保存错误！ " + e.message)
                            }
                            Display.render(de)
                        } else if (key == "info") {
                            if (cacheData.containsKey(inputList[1])) {
                                infoCache = cacheData[inputList[1]]
                                cacheChange = false
                                Display.render(CONNECT_INFO)
                            } else {
                                println(
                                    """
                                    【 ID不正确，请重新输入 】
                                    """.trimIndent()
                                )
                                Display.render(de)
                            }
                        } else if (key == "del") {
                            if (cacheData.containsKey(inputList[1])) {
                                val data = cacheData[inputList[1]]
                                if (enquire("是否删除'${data!!.remark}'?")) {
//                                    cacheData.remove(inputList[1])
                                    ConfigData.delById(inputList[1])
                                    println("删除成功！")
                                }
                            } else {
                                println(
                                    """
                                    【 ID不正确，请重新输入 】
                                    """.trimIndent()
                                )
                            }
                            Display.render(de)
                        } else if (key == "start") {
                            if (inputList[1] == "all") {
                                cacheData.forEach {
                                    connectList[it.key] = LabSSHPenetrationClient(it.value).apply {
                                        connect()
                                    }
                                }
                            } else {
                                inputList[1].split(",").forEach {
                                    if (cacheData.containsKey(it)) {
                                        connectList[it] = LabSSHPenetrationClient(cacheData[it]!!).apply {
                                            connect()
                                        }
                                    } else
                                        println("id【$it】错误！")

                                }
                            }
                            Display.render(de)
                        } else if (key == "cut") {
                            if (inputList[1] == "all") {
                                connectList.forEach {
                                    it.value.close()
                                }
                            } else {
                                inputList[1].split(",").forEach {
                                    if (connectList.containsKey(it)) {
                                        connectList[it]?.close()
                                    } else
                                        println("id【$it】错误！")
                                }
                            }
                            Display.render(de)
                        } else if (key == "r") {
                            Display.render(de)
                        } else if (key == "back") {
                            Display.render(HOME)
                        }
                    }

                    CONNECT_INFO -> {
                        if (key == "edit") {
                            val value = inputList[2]
                            when (inputList[1]) {
                                "name" -> {
                                    infoCache?.remark = value
                                    cacheChange = true
                                    Display.render(de)
                                }

                                "sip" -> {
                                    infoCache?.serverHost = value
                                    cacheChange = true
                                    Display.render(de)
                                }

                                "spt" -> {
                                    try {
                                        val port = Convert.toInt(value)
                                        if (port < 0 || port > 65535) {
                                            println("---端口必须为[0-65535]!---")
                                        } else {
                                            infoCache?.serverPort = port
                                            cacheChange = true
                                        }
                                    } catch (e: Exception) {
                                        println("---端口必须为[0-65535]!---")
                                    }
                                    Display.render(de)
                                }

                                "pip" -> {
                                    infoCache?.proxyHost = value
                                    cacheChange = true
                                    Display.render(de)
                                }

                                "ppt" -> {
                                    try {
                                        val port = Convert.toInt(value)
                                        if (port < 0 || port > 65535) {
                                            println("---端口必须为[0-65535]!---")
                                        } else {
                                            infoCache?.proxyPort = port
                                            cacheChange = true
                                        }
                                    } catch (e: Exception) {
                                        println("---端口必须为[0-65535]!---")
                                    }
                                    Display.render(de)
                                }

                                "tpt" -> {
                                    try {
                                        val port = Convert.toInt(value)
                                        if (port < 0 || port > 65535) {
                                            println("---端口必须为[0-65535]!---")
                                        } else {
                                            infoCache?.targetPort = port
                                            cacheChange = true
                                        }
                                    } catch (e: Exception) {
                                        println("---端口必须为[0-65535]!---")
                                    }
                                    Display.render(de)
                                }

                                "pwd" -> {
                                    infoCache?.password = value
                                    cacheChange = true
                                    Display.render(de)
                                }

                                "wls" -> {
                                    try {
                                        infoCache?.let {
                                            it.wls.clear()
                                            it.wls.addAll(Convert.toSet(String::class.java, value))
                                        }
                                        cacheChange = true
                                    } catch (e: Exception) {
                                        println("---白名单输入有误！例：exit wls [192.168.1.33,192.168.3.3]---")
                                    }
                                    Display.render(de)
                                }

                                else -> return false
                            }
                        } else if (key == "save") {
                            infoCache?.let { ConfigData.write(it) }
                            println("保存成功！")
                            Display.render(CONNECT_LIST)
                        } else if (key == "back") {
                            if (cacheChange) {
                                if (enquire("是否放弃修改内容？")) {
                                    Display.render(CONNECT_LIST)
                                    infoCache = null
                                } else
                                    Display.render(de)
                            } else {
                                Display.render(CONNECT_LIST)
                                infoCache = null
                            }
                        }

                    }

                    LOG -> {
                        if (key == "r") {
                            Display.render(de)
                        } else if (key == "back") {
                            Display.render(HOME)
                        }
                    }

                    STATUS -> {
                        if (key == "r") {
                            Display.render(de)
                        } else if (key == "back") {
                            Display.render(HOME)
                        } else if (key == "start") {
                            cacheData.forEach {
                                connectList[it.key] = LabSSHPenetrationClient(it.value).apply {
                                    connect()
                                }
                            }
                            Display.render(de)
                        } else if (key == "close") {
                            connectList.forEach {
                                it.value.close()
                            }
                            Display.render(de)
                        }
                    }
                }
                true
            } else false

        }

        private fun enquire(q: String): Boolean {
            println("$q 【y/n】")
            val console = Scanner(System.`in`)
            if (console.nextLine().trim() == "y")
                return true
            return false
        }


    }
}