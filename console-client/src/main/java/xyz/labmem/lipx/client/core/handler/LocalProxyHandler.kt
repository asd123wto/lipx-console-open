package xyz.labmem.lipx.client.core.handler

import io.netty.channel.ChannelHandlerContext
import xyz.labmem.lipx.netty.core.handler.LabCommonHandler
import xyz.labmem.lipx.netty.core.protocol.LabMessage
import xyz.labmem.lipx.netty.core.protocol.LabMessageType

/**
 * @description: do something
 * @author: liutianyu
 * @date: 2022/5/30 15:51
 */
class LocalProxyHandler(
    private val proxyHandler: LabCommonHandler,
    private val remoteChannelId: String
) : LabCommonHandler() {

    @Throws(Exception::class)
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        val data = msg as ByteArray?
        val metaData = HashMap<String, Any>()
        metaData["channelId"] = remoteChannelId
        LabMessage().apply {
            type = LabMessageType.DATA
            this.data = data
            this.metaData = metaData
            proxyHandler.ctx?.writeAndFlush(this)
        }
    }

    @Throws(Exception::class)
    override fun channelInactive(ctx: ChannelHandlerContext) {
        val metaData = HashMap<String, Any>()
        metaData["channelId"] = remoteChannelId
        LabMessage().apply {
            type = LabMessageType.DISCONNECTED
            this.metaData = metaData
            proxyHandler.ctx?.writeAndFlush(this)
        }
    }

}