package xyz.labmem.lipx.server.core

import xyz.labmem.lipx.core.ServerConfig
import xyz.labmem.lipx.server.core.net.ServerStatus

/**
 * @description: do something
 * @author: liutianyu
 * @date: 2022/8/9 17:33
 */
class AppContext {

    companion object {

        val server = LabNettyPenetrationServer()

        var cacheData: ServerConfig? = null

        var clientList = HashMap<String, ClientData>()

        var cacheChange = false

        fun run() = server.getStatus() == ServerStatus.RUN

        var startDate: String = "-"

    }

}