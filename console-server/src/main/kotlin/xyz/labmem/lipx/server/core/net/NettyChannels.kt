package xyz.labmem.lipx.server.core.net

import io.netty.channel.group.ChannelGroup
import io.netty.channel.group.DefaultChannelGroup
import io.netty.util.concurrent.GlobalEventExecutor

class NettyChannels {

    companion object {

        val channels: ChannelGroup = DefaultChannelGroup(GlobalEventExecutor.INSTANCE)

        val registerMap = HashMap<Int, String>()
    }

}