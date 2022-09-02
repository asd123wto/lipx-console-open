package xyz.labmem.lipx.server.console

import xyz.labmem.lipx.server.console.enums.DisplayEnum
import xyz.labmem.lipx.server.console.enums.DisplayEnum.*
import xyz.labmem.lipx.server.core.AppContext
import xyz.labmem.lipx.server.core.AppContext.Companion.cacheChange
import xyz.labmem.lipx.server.core.AppContext.Companion.cacheData
import xyz.labmem.lipx.server.core.ConfigData
import xyz.labmem.lipx.server.core.ConfigData.Companion.refreshData
import xyz.labmem.lipx.server.shutdown
import java.util.*

/**
 * @description: do something
 * @author: liutianyu
 * @date: 2022/8/9 11:40
 */
class Control {

    companion object {

        fun parse(input: String, de: DisplayEnum): Boolean {
            try {
                val inputList = input.trim().split(" ")
                val key = inputList[0]
                return if (de.keys().contains(key)) {
                    when (de) {
                        HOME -> {
                            if (key == "config") {
                                refreshData()
                                Display.render(CONFIG)
                            } else if (key == "status") {
                                Display.render(STATUS)
                            } else if (key == "log") {
                                Display.render(LOG)
                            } else if (key == "exit") {
                                shutdown()
                            }
                        }

                        LOG -> {
                            if (key == "r") {
                                Display.render(de)
                            } else if (key == "back" || key == "./") {
                                Display.render(HOME)
                            }
                        }

                        STATUS -> {
                            if (key == "r") {
                                Display.render(de)
                            } else if (key == "back" || key == "./") {
                                Display.render(HOME)
                            } else if (key == "start") {
                                refreshData()
                                if (AppContext.run()) {
                                    if (enquire("服务已经启动，是否重启？")) {
                                        AppContext.server.restart(cacheData!!)
                                        println("LIPX_Server 启动中。。")
                                    }
                                } else {
                                    AppContext.server.start(cacheData!!)
                                    println("LIPX_Server 启动中。。")
                                }
                                Display.render(de)
                            } else if (key == "close") {
                                if (!AppContext.run()) {
                                    println("LIPX_Server 未启动。。")
                                } else
                                    AppContext.server.close()
                                Display.render(de)
                            }
                        }

                        CONFIG -> {
                            if (key == "edit") {
                                val k = inputList[1]
                                val v = inputList[2]
                                if (k == "port") {
                                    try {
                                        cacheData!!.port = v.toInt()
                                        cacheChange = true
                                    } catch (e: Exception) {
                                        println("服务端口错误")
                                    }
                                } else if (k == "pwd") {
                                    cacheData!!.password = if (v == "null") "" else v
                                    cacheChange = true
                                } else if (k == "wls") {
                                    cacheData!!.wls.clear()
                                    cacheData!!.wls.addAll(v.split(","))
                                    cacheChange = true
                                }
                                Display.render(de)
                            } else if (key == "save") {
                                cacheData?.let {
                                    ConfigData.write(it)
                                    println("保存成功！")
                                    if (AppContext.run()) {
                                        AppContext.server.restart(it)
                                        println("并重启了服务！")
                                    }
                                }
                                cacheChange = false
                                Display.render(de)
                            } else if (key == "back" || key == "./") {
                                if (cacheChange) {
                                    if (enquire("是否放弃修改内容？")) {
                                        refreshData()
                                        cacheChange = false
                                        Display.render(HOME)
                                    } else
                                        Display.render(de)
                                } else {
                                    Display.render(HOME)
                                }
                            }
                        }
                    }
                    true
                } else false
            } catch (e: IndexOutOfBoundsException) {
                return false
            }
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