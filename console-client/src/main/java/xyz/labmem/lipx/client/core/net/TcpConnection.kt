package xyz.labmem.lipx.client.core.net

import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel

/**
 * @description: do something
 * @author: liutianyu
 * @date: 2022/5/30 15:45
 */
class TcpConnection {

    var channel: Channel? = null

    @Throws(InterruptedException::class)
    fun connect(host: String, port: Int, channelInitializer: ChannelInitializer<*>): ChannelFuture {
        val workerGroup = NioEventLoopGroup()
        return try {
            channel = Bootstrap().apply {
                group(workerGroup)
                channel(NioSocketChannel::class.java)
                option(ChannelOption.SO_KEEPALIVE, true)
                handler(channelInitializer)
            }.connect(host, port).sync().channel()
            channel!!.closeFuture().addListener { workerGroup.shutdownGracefully() }
        } catch (e: Exception) {
            workerGroup.shutdownGracefully()
            throw e
        }
    }
}