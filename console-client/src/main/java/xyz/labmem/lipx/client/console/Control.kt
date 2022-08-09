package xyz.labmem.lipx.client.console

import xyz.labmem.lipx.client.console.enums.DisplayEnum
import xyz.labmem.lipx.client.console.enums.DisplayEnum.*
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
                            Display.render(CONNECT_INFO)
                        } else if (key == "del") {
                            //TODO
                            Display.render(de)
                        } else if (key == "start") {
                            //TODO
                            Display.render(de)
                        } else if (key == "cut") {
                            //TODO
                            Display.render(de)
                        } else if (key == "r") {
                            Display.render(de)
                        } else if (key == "back") {
                            Display.render(HOME)
                        }
                    }

                    CONNECT_INFO -> {
                        if (key == "edit") {
                            //TODO
                            Display.render(de)
                        } else if (key == "save") {
                            //TODO
                            Display.render(CONNECT_LIST)
                        } else if (key == "back") {
                            //TODO 判断是否修改
                            if (enquire("是否放弃修改内容？"))
                                Display.render(CONNECT_LIST)
                            else
                                Display.render(de)
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

        fun enquire(q: String): Boolean {
            println("$q 【y/n】")
            val console = Scanner(System.`in`)
            if (console.nextLine().trim() == "y")
                return true
            return false
        }


    }
}