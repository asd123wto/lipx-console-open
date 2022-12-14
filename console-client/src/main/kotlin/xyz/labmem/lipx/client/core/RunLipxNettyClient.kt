package xyz.labmem.lipx.client.core

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.timeout.IdleStateHandler
import xyz.labmem.lipx.client.core.AppContext.Companion.cacheData
import xyz.labmem.lipx.client.core.handler.LabNettyClientHandler
import xyz.labmem.lipx.client.core.net.TcpConnection
import xyz.labmem.lipx.core.PortConfig
import xyz.labmem.lipx.core.Status
import xyz.labmem.lipx.core.tool.LogInfo
import xyz.labmem.lipx.netty.core.codec.LabMessageDecoder
import xyz.labmem.lipx.netty.core.codec.LabMessageEncoder

/**
 * @description: main
 * @author: liutianyu
 * @date: 2022/5/27 13:11
 */
class LabSSHPenetrationClient(
    private var config: PortConfig,
) {
    private lateinit var tcp: TcpConnection

    private var remake = false

    private var remakeCount = 0

    fun connect() {
        try {
            tcp = TcpConnection()
            val future = tcp.connect(
                config.serverHost!!,
                config.serverPort!!,
                object : ChannelInitializer<SocketChannel>() {
                    @Throws(Exception::class)
                    override fun initChannel(ch: SocketChannel) {
                        ch.pipeline().addLast(
                            LengthFieldBasedFrameDecoder(Int.MAX_VALUE, 0, 4, 0, 4),
                            LabMessageDecoder(), LabMessageEncoder(),
                            IdleStateHandler(60, 30, 0),
                            LabNettyClientHandler(config) { b, r ->
                                if (!remake)
                                    remake = b
                                if (r) {
                                    remake = false
                                }
                                if (b) {
                                    remakeCount = 0
                                    cacheData[config.id]?.status = Status.CONNECTED
                                } else {
                                    cacheData[config.id]?.status = Status.FAILED
                                    AppContext.connectList.remove(config.id)
                                }
                            }
                        )
                    }
                })
            // ????????????
            future.addListener {
                Thread() {
                    while (remake) {
                        try {
                            remakeCount++
                            cacheData[config.id]?.status = Status.RE_CONNECT
                            LogInfo.appendLog("????????????????????????...", config)
                            connect()
                            break
                        } catch (e: Exception) {
                            var factor = 1L
                            if (remakeCount in 61..149) {
                                factor = 3
                            }
                            if (remakeCount in 150..299) {
                                factor = 6
                            }
                            if (remakeCount > 300) {
                                factor = 12
                            }
                            Thread.sleep(10000 * factor)
                        }
                    }
                }.start()
            }
        } catch (e: Exception) {
            cacheData[config.id]?.status = Status.FAILED
            AppContext.connectList.remove(config.id)
            LogInfo.appendLogError("lipx????????????:${e.message}", config)
        }
    }

    fun close() {
        LogInfo.appendLog("??????????????????????????????", config)
        try {
            remake = false
            tcp.channel?.close()
            LogInfo.appendLog("???????????????", config)
        } catch (e: Exception) {
            LogInfo.appendLogError("??????????????????:${e.message}", config)
        } finally {
            cacheData[config.id]?.status = Status.IDLE
            AppContext.connectList.remove(config.id)
        }
    }

    fun restart(newConfig: PortConfig) {
        config = newConfig
        cacheData[config.id]?.status = Status.RE_CONNECT
        LogInfo.appendLog("??????????????????????????????", config)
        try {
            remake = false
            tcp.channel?.close()
            LogInfo.appendLog("??????????????????????????????", config)
            connect()
        } catch (e: Exception) {
            cacheData[config.id]?.status = Status.IDLE
            LogInfo.appendLogError("??????????????????:${e.message}", config)
        }
    }

}