package xyz.labmem.lipx.client.core.handler

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInitializer
import io.netty.channel.group.DefaultChannelGroup
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.bytes.ByteArrayDecoder
import io.netty.handler.codec.bytes.ByteArrayEncoder
import io.netty.util.concurrent.GlobalEventExecutor
import xyz.labmem.lipx.client.core.net.TcpConnection
import xyz.labmem.lipx.core.PortConfig
import xyz.labmem.lipx.core.tool.LogInfo
import xyz.labmem.lipx.netty.core.handler.LabCommonHandler
import xyz.labmem.lipx.netty.core.protocol.LabMessage
import xyz.labmem.lipx.netty.core.protocol.LabMessageType
import java.util.concurrent.ConcurrentHashMap

/**
 * @description: do something
 * @author: liutianyu
 * @date: 2022/5/30 15:58
 */
class LabNettyClientHandler(
    private val config: PortConfig,
    private val registerBack: ((r: Boolean, reject: Boolean) -> Unit)
) : LabCommonHandler() {

    private var register = false
    private val channelHandlerMap = ConcurrentHashMap<String, LabCommonHandler>()
    private val channelGroup = DefaultChannelGroup(GlobalEventExecutor.INSTANCE)

    @Throws(Exception::class)
    override fun channelActive(ctx: ChannelHandlerContext) {
        val metaData = HashMap<String, Any>()
        metaData["port"] = config.targetPort!!
        metaData["password"] = config.password!!
        metaData["wls"] = config.wls
        metaData["name"] = config.remark ?: "NoName"
        // register client information
        LabMessage().apply {
            type = LabMessageType.REGISTER
            this.metaData = metaData
            ctx.writeAndFlush(this)
            super.channelActive(ctx)
        }
    }

    @Throws(Exception::class)
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        val message = msg as LabMessage
        if (message.type === LabMessageType.REGISTER_RESULT) {
            processRegisterResult(message)
        } else if (message.type === LabMessageType.CONNECTED) {
            processConnected(message)
        } else if (message.type === LabMessageType.DISCONNECTED) {
            processDisconnected(message)
        } else if (message.type === LabMessageType.DATA) {
            processData(message)
        } else if (message.type === LabMessageType.KEEPALIVE) {
            // ?????????, ?????????
        } else {
            throw Exception("Unknown type: ${message.type}")
        }
    }

    @Throws(Exception::class)
    override fun channelInactive(ctx: ChannelHandlerContext) {
        channelGroup.close()
        LogInfo.appendLog("???lipx????????????????????????!", config)
    }

    private fun processRegisterResult(message: LabMessage) {
        var reject = false
        if (message.metaData!!["success"] as Boolean) {
            register = true
            LogInfo.appendLog("??????????????????????????????", config)
        } else {
            register = false
            if (message.metaData!!["reason"].toString().contains("????????????"))
                reject = true
            LogInfo.appendLogError("????????????????????????${message.metaData!!["reason"]}", config)
            ctx!!.close()
        }
        registerBack(register, reject)
    }


    @Throws(Exception::class)
    private fun processConnected(message: LabMessage) {
        try {
            val thisHandler = this
            val localConnection = TcpConnection()
            localConnection.connect(
                config.proxyHost!!,
                config.proxyPort!!,
                object : ChannelInitializer<SocketChannel>() {
                    @Throws(Exception::class)
                    override fun initChannel(ch: SocketChannel) {
                        val localProxyHandler =
                            LocalProxyHandler(thisHandler, message.metaData!!["channelId"].toString())
                        ch.pipeline().addLast(ByteArrayDecoder(), ByteArrayEncoder(), localProxyHandler)
                        channelHandlerMap[message.metaData!!["channelId"].toString()] = localProxyHandler
                        channelGroup.add(ch)
                    }
                })
        } catch (e: Exception) {
            val metaData = HashMap<String, Any>()
            metaData["channelId"] = message.metaData!!["channelId"].toString()
            LabMessage().apply {
                type = LabMessageType.DISCONNECTED
                this.metaData = metaData
                ctx!!.writeAndFlush(this)
            }
            channelHandlerMap.remove(message.metaData!!["channelId"])
            e.printStackTrace()
            throw Exception("????????????????????????")
        }
    }

    /**
     * DISCONNECTED
     */
    private fun processDisconnected(message: LabMessage) {
        val channelId: String = message.metaData!!["channelId"].toString()
        val handler = channelHandlerMap[channelId]
        if (handler != null) {
            handler.ctx?.close()
            channelHandlerMap.remove(channelId)
        }
    }

    /**
     * DATA
     */
    private fun processData(message: LabMessage) {
        val channelId: String = message.metaData!!["channelId"].toString()
        val handler = channelHandlerMap[channelId]
        if (handler != null) {
            val ctx = handler.ctx
            ctx?.writeAndFlush(message.data)
        }
    }
}