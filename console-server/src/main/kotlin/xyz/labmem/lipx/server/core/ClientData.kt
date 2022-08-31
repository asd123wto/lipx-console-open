package xyz.labmem.lipx.server.core

import xyz.labmem.lipx.netty.core.handler.LabCommonHandler

/**
 * @description: do something
 * @author: liutianyu
 * @date: 2022/8/29 13:21
 */
class ClientData {

    var name: String? = null

    var port: Int? = null

    var ip: String? = null

    var connectTime: String? = null

    var connectChannel: LabCommonHandler? = null

}