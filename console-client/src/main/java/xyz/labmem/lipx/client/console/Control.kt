package xyz.labmem.lipx.client.console

import cn.hutool.core.convert.Convert
import xyz.labmem.lipx.client.console.enums.DisplayEnum
import xyz.labmem.lipx.client.console.enums.DisplayEnum.*
import xyz.labmem.lipx.client.core.AppContext.Companion.cacheChange
import xyz.labmem.lipx.client.core.AppContext.Companion.cacheData
import xyz.labmem.lipx.client.core.AppContext.Companion.infoCache
import xyz.labmem.lipx.client.shutdown
import java.util.*

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
                            Display.render(STATUS)
                        } else if (key == "exit") {
                            shutdown()
                        }
                    }

                    CONNECT_LIST -> {
                        if (key == "new") {
                            //TODO
                            Display.render(de)
                        } else if (key == "info") {
                            if (cacheData.containsKey(inputList[1])) {
                                infoCache = cacheData[inputList[1]]
                                Display.render(CONNECT_INFO)
                                cacheChange = false
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
                                    cacheData.remove(inputList[1])
                                    //TODO 保存到文件
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
                            //TODO 连接
                            Display.render(de)
                        } else if (key == "cut") {
                            //TODO 断开
                            Display.render(de)
                        } else if (key == "r") {
                            Display.render(de)
                        } else if (key == "back") {
                            Display.render(HOME)
                        }
                    }

                    CONNECT_INFO -> {
                        if (key == "edit") {
                            val key = inputList[1]
                            val value = inputList[2]
                            when (key) {
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
                            //TODO 保存到文件
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