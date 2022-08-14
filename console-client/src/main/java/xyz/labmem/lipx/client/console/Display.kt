package xyz.labmem.lipx.client.console

import xyz.labmem.lipx.client.console.enums.DisplayEnum
import xyz.labmem.lipx.client.core.AppContext.Companion.cacheData
import xyz.labmem.lipx.client.core.ConfigData
import xyz.labmem.lipx.client.core.pojo.PortConfig
import java.util.*
import kotlin.collections.HashMap

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

        fun getList(): HashMap<String,PortConfig> {
            ConfigData.refreshList()
            return cacheData
        }

    }

}