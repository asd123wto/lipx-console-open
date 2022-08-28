package xyz.labmem.lipx.client.core.tool

import cn.hutool.core.date.DateUtil
import cn.hutool.core.thread.ExecutorBuilder
import xyz.labmem.lipx.client.core.AppContext.Companion.logs
import xyz.labmem.lipx.client.core.pojo.PortConfig
import java.util.concurrent.ExecutorService


/**
 * @description: do something
 * @author: liutianyu
 * @date: 2022/6/15 17:25
 */
class LogInfo {

    companion object {
        private val executor: ExecutorService = ExecutorBuilder.create()
            .setCorePoolSize(1)
            .setMaxPoolSize(1)
            .setKeepAliveTime(0)
            .build()

        fun appendLog(msg: String) {
            addTask("${DateUtil.now()} 【INFO】 $msg\n")
        }

        fun appendLogError(msg: String) {
            addTask("${DateUtil.now()} 【ERROR】 $msg\n")
        }

        fun appendLog(msg: String, config: PortConfig) {
            addTask("${DateUtil.now()} 【INFO】【${config.serverHost}:${config.serverPort}@${config.targetPort}】 $msg\n")
        }

        fun appendLogError(msg: String, config: PortConfig) {
            addTask("${DateUtil.now()} 【ERROR】【${config.serverHost}:${config.serverPort}@${config.targetPort}】 $msg\n")
        }

        private fun addTask(msg: String) {
            executor.submit {
                logs.add(msg)
                Thread.sleep(200)
            }
        }
    }
}