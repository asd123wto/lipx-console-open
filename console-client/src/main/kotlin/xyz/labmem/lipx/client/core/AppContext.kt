package xyz.labmem.lipx.client.core

import xyz.labmem.lipx.core.PortConfig

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

        val connectList = HashMap<String, LabSSHPenetrationClient>()

    }

}