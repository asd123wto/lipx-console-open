package xyz.labmem.lipx.server.core.handler

import cn.hutool.core.date.DateUtil
import cn.hutool.json.JSONArray
import cn.hutool.log.StaticLog
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.bytes.ByteArrayDecoder
import io.netty.handler.codec.bytes.ByteArrayEncoder
import io.netty.handler.ipfilter.IpFilterRule
import io.netty.handler.ipfilter.IpFilterRuleType
import io.netty.handler.ipfilter.IpSubnetFilterRule
import io.netty.handler.ipfilter.RuleBasedIpFilter
import xyz.labmem.lipx.core.ServerConfig
import xyz.labmem.lipx.core.tool.LogInfo
import xyz.labmem.lipx.server.core.net.NettyChannels.Companion.channels
import xyz.labmem.lipx.netty.core.handler.LabCommonHandler
import xyz.labmem.lipx.netty.core.protocol.LabMessage
import xyz.labmem.lipx.netty.core.protocol.LabMessageType
import xyz.labmem.lipx.server.core.AppContext.Companion.clientList
import xyz.labmem.lipx.server.core.ClientData
import xyz.labmem.lipx.server.core.net.TcpServer
import java.net.InetSocketAddress

/**
 * @description: do something
 * @author: liutianyu
 * @date: 2022/5/30 16:38
 */
class LabServerHandler(
    private val server: ServerConfig
) : LabCommonHandler() {

    private var register = false
    private var port: Int? = null
    private var ip: String? = null

    private val remoteConnectionServer = TcpServer()


    @Throws(Exception::class)
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        val message = msg as LabMessage
        ip = (ctx.channel()?.remoteAddress() as InetSocketAddress).address.hostAddress
        if (message.type == LabMessageType.REGISTER) {
            processRegister(message)
        } else if (register) {
            if (message.type === LabMessageType.DISCONNECTED) {
                processDisconnected(message)
            } else if (message.type === LabMessageType.DATA) {
                processData(message)
            } else if (message.type === LabMessageType.KEEPALIVE) {
                // 心跳包
            } else {
                LogInfo.appendLogError("未知请求协议！")
                throw Exception("Unknown type: ${message.type}")
            }
        } else {
            ctx.close()
        }
    }

    @Throws(Exception::class)
    override fun channelInactive(ctx: ChannelHandlerContext) {
        remoteConnectionServer.close()
        if (register) {
            clientList.remove("$ip:$port")
            LogInfo.appendLog("netty server 已关闭端口：$port")
            StaticLog.info("netty server 已关闭端口：$port")
            ctx.close()
        }
    }

    /**
     * REGISTER
     */
    private fun processRegister(message: LabMessage) {
        val metaData = HashMap<String, Any>()
        val password: String = message.metaData!!["password"].toString()
        val cwls =  message.metaData!!["wls"] as JSONArray
        if (server.wls.isEmpty() || server.wls.contains(ip)) {
            if (server.password != password) {
                metaData["success"] = false
                metaData["reason"] = "Token is wrong"
            } else {
                val port = message.metaData!!["port"] as Int
                try {
                    val thisHandler = this
                    remoteConnectionServer.bind(port, object : ChannelInitializer<SocketChannel>() {
                        @Throws(Exception::class)
                        override fun initChannel(ch: SocketChannel) {
                            ch.pipeline().apply {
                                if (cwls.isNotEmpty()) {
                                    val rejectAll = object : IpFilterRule {
                                        override fun matches(remoteAddress: InetSocketAddress): Boolean = true
                                        override fun ruleType(): IpFilterRuleType = IpFilterRuleType.REJECT
                                    }
                                    addLast(
                                        "ipFilter",
                                        RuleBasedIpFilter(* cwls.map {
                                            IpSubnetFilterRule(
                                                it.toString(),
                                                32,
                                                IpFilterRuleType.ACCEPT
                                            )
                                        }.toTypedArray(), rejectAll)
                                    )
                                }
                                addLast(ByteArrayDecoder(), ByteArrayEncoder(), RemoteProxyHandler(thisHandler, port))
                            }
                            channels.add(ch)
                        }
                    }) {
                        metaData["success"] = true
                        this.port = port
                        register = true
                        clientList["$ip:$port"] = ClientData().apply c@{
                            name = message.metaData!!["name"].toString()
                            ip = this@LabServerHandler.ip
                            this.port = port
                            connectTime = DateUtil.now()
                            connectChannel = this@LabServerHandler
                        }
                        LogInfo.appendLog("客户端[$ip]注册成功，启动代理端口： $port")
                    }
                } catch (e: Exception) {
                    metaData["success"] = false
                    metaData["reason"] = e.message!!
                    e.printStackTrace()
                }
            }
        } else {
            metaData["success"] = false
            metaData["reason"] = "拒绝连接！"
            LogInfo.appendLogError("客户端[$ip]不在白名单，已拒绝连接！")
        }
        LabMessage().apply {
            type = LabMessageType.REGISTER_RESULT
            this.metaData = metaData
            ctx?.writeAndFlush(this)
        }
        if (!register) {
            LogInfo.appendLogError("客户端[$ip]注册失败:${metaData["reason"]}")
            ctx?.close()
        }
    }

    /**
     * DATA
     */
    private fun processData(message: LabMessage) {
        channels.writeAndFlush(
            message.data
        ) { channel: Channel ->
            channel.id().asLongText() == message.metaData!!["channelId"]
        }
//        message.data?.let {
//            LipxStaticObj.addOutputStreamByte(it.size)
//        }
    }

    /**
     * DISCONNECTED
     */
    private fun processDisconnected(message: LabMessage) {
        channels.close { channel: Channel ->
            channel.id().asLongText() == message.metaData!!["channelId"]
        }
    }

}