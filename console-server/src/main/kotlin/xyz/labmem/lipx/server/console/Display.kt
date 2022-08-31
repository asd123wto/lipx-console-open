package xyz.labmem.lipx.server.console

import xyz.labmem.lipx.server.console.enums.DisplayEnum
import java.util.*

/**
 * @description: do something
 * @author: liutianyu
 * @date: 2022/8/9 11:40
 */
class Display {

    companion object {

        fun render(de: DisplayEnum) {
            println(de.content())
            println(de.hint())
            print("lipx:~$ ")
            val console = Scanner(System.`in`)
            if (!Control.parse(console.nextLine(), de)) {
                println(
                    """
                    【 未知命令，请检查后再试！】
                """.trimIndent()
                )
                render(de)
            }
        }

    }

}