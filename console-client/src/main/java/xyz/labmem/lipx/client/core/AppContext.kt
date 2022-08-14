package xyz.labmem.lipx.client.core

import xyz.labmem.lipx.client.core.pojo.PortConfig

/**
 * @description: do something
 * @author: liutianyu
 * @date: 2022/8/9 17:33
 */
class AppContext {

    companion object {

        val cacheData = HashMap<String, PortConfig>()

        var infoCache: PortConfig? = null

        var cacheChange = false

    }

}