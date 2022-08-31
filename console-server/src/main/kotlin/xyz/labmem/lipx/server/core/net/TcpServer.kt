package xyz.labmem.lipx.server.core.net

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.*
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import xyz.labmem.lipx.core.tool.LogInfo

/**
 * @description: do something
 * @author: liutianyu
 * @date: 2022/5/30 16:35
 */
class TcpServer {

    private var channel: Channel? = null

    var status: ServerStatus = ServerStatus.CLOSE

    @Synchronized
    @Throws(InterruptedException::class)
    fun bind(port: Int, channelInitializer: ChannelInitializer<*>, successBack: () -> Unit) {
        val bossGroup: EventLoopGroup = NioEventLoopGroup()
        val workerGroup: EventLoopGroup = NioEventLoopGroup()
        try {
            val b = ServerBootstrap()
            b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel::class.java)
                .childHandler(channelInitializer)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
            channel = b.bind(port).sync().channel()
            channel?.closeFuture()?.addListener(ChannelFutureListener {
                workerGroup.shutdownGracefully()
                bossGroup.shutdownGracefully()
            })
            successBack()
        } catch (e: Exception) {
            status = ServerStatus.CLOSE
            workerGroup.shutdownGracefully()
            bossGroup.shutdownGracefully()
            throw e
        }
    }

    @Synchronized
    fun close() {
        if (channel != null) {
            channel!!.close()
        }
        status = ServerStatus.CLOSE
        LogInfo.appendLog("LIPX server 关闭成功")
    }

}