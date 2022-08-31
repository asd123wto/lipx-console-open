package xyz.labmem.lipx.server.core

import cn.hutool.core.date.DateUtil
import cn.hutool.log.StaticLog
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.timeout.IdleStateHandler
import xyz.labmem.lipx.core.ServerConfig
import xyz.labmem.lipx.netty.core.codec.LabMessageDecoder
import xyz.labmem.lipx.netty.core.codec.LabMessageEncoder
import xyz.labmem.lipx.server.core.handler.LabServerHandler
import xyz.labmem.lipx.server.core.net.ServerStatus
import xyz.labmem.lipx.server.core.net.TcpServer
import xyz.labmem.lipx.core.tool.LogInfo

/**
 * @description: do something
 * @author: liutianyu
 * @date: 2022/5/30 14:05
 */
class LabNettyPenetrationServer {

    private val nettyServer = TcpServer()

    @Throws(InterruptedException::class)
    fun start(config: ServerConfig) {
        if (getStatus() == ServerStatus.CLOSE) {
            nettyServer.status = ServerStatus.PROCESSING
            nettyServer.bind(config.port, object : ChannelInitializer<SocketChannel>() {
                @Throws(Exception::class)
                override fun initChannel(ch: SocketChannel) {
                    val serverHandler = LabServerHandler(config)
                    ch.pipeline().addLast(
                        LengthFieldBasedFrameDecoder(Int.MAX_VALUE, 0, 4, 0, 4),
                        LabMessageDecoder(), LabMessageEncoder(),
                        IdleStateHandler(60, 30, 0), serverHandler
                    )
                }
            }) {
                nettyServer.status = ServerStatus.RUN
                LogInfo.Companion.appendLog("LIPX server 启动成功，端口:${config.port}")
                StaticLog.info("netty启动成功，端口:${config.port}")
                AppContext.startDate = DateUtil.now()
            }
        } else {
            LogInfo.Companion.appendLogError("LIPX server 未关闭，启动失败")
        }
    }

    fun close() {
        nettyServer.status = ServerStatus.PROCESSING
        nettyServer.close()

    }

    fun restart(config: ServerConfig) {
        close()
        start(config)
    }

    fun getStatus(): ServerStatus = nettyServer.status


}