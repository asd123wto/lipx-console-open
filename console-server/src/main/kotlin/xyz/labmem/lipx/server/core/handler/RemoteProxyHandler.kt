package xyz.labmem.lipx.server.core.handler

import io.netty.channel.ChannelHandlerContext
import xyz.labmem.lipx.core.tool.LogInfo
import xyz.labmem.lipx.netty.core.handler.LabCommonHandler
import xyz.labmem.lipx.netty.core.protocol.LabMessage
import xyz.labmem.lipx.netty.core.protocol.LabMessageType
import java.net.InetSocketAddress

/**
 * @description: do something
 * @author: liutianyu
 * @date: 2022/5/30 16:56
 */
class RemoteProxyHandler(
    private val proxyHandler: LabCommonHandler,
    private val port: Int
) : LabCommonHandler() {

    @Throws(Exception::class)
    override fun channelActive(ctx: ChannelHandlerContext) {
        val accessIp = (ctx.channel().remoteAddress() as InetSocketAddress).address.hostAddress
        LogInfo.appendLog("[$accessIp] 请求连接代理端口: $port ")
        val metaData = HashMap<String, Any>()
        metaData["channelId"] = ctx.channel().id().asLongText()
        LabMessage().apply {
            type = LabMessageType.CONNECTED
            this.metaData = metaData
            proxyHandler.ctx?.writeAndFlush(this)
        }
    }

    @Throws(Exception::class)
    override fun channelInactive(ctx: ChannelHandlerContext) {
        val metaData = HashMap<String, Any>()
        metaData["channelId"] = ctx.channel().id().asLongText()
        LabMessage().apply {
            type = LabMessageType.DISCONNECTED
            this.metaData = metaData
            proxyHandler.ctx?.writeAndFlush(this)
        }
    }

    @Throws(Exception::class)
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        val data = msg as ByteArray?
        val metaData = HashMap<String, Any>()
        metaData["channelId"] = ctx.channel().id().asLongText()
        LabMessage().apply {
            type = LabMessageType.DATA
            this.data = data
            this.metaData = metaData
            proxyHandler.ctx?.writeAndFlush(this)
        }
//        data?.let { LipxStaticObj.addInputStreamByte(it.size) }
    }

}