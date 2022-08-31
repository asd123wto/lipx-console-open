package xyz.labmem.lipx.server


import xyz.labmem.lipx.server.console.Display
import xyz.labmem.lipx.server.console.enums.DisplayEnum
import xyz.labmem.lipx.server.core.AppContext
import xyz.labmem.lipx.server.core.ConfigData
import xyz.labmem.lipx.server.core.LabNettyPenetrationServer
import kotlin.system.exitProcess


/**
 * @description: do something
 * @author: liutianyu
 * @date: 2022/8/9 11:27
 */
const val labVersion = "1.0"
fun main() {
    var process = 0

    Thread {
        process = 1
        ConfigData.init()
        process = 2
        Thread.sleep(500)
        process = 3
    }.start()

    while (process < 4) {
        val p = if (process == 0) {
            "##############################"
        } else if (process == 1) {
            "############################################################"
        } else if (process == 2) {
            "########################################################################################################################"
        } else if (process == 3) {
            process = 4
            "/-----------------------------------------------------------------------------------------------------------------------/"
        } else {
            ""
        }
        print("$p \r")
    }
    Display.render(DisplayEnum.HOME)
}

fun shutdown() {
    println("😢 goodbye!")
    exitProcess(0)
}