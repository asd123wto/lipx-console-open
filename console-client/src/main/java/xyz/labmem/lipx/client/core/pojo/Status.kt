package xyz.labmem.lipx.client.core.pojo

/**
 * @description: do something
 * @author: liutianyu
 * @date: 2022/6/14 16:35
 */
enum class Status {

    IDLE, CONNECTING, CONNECTED, RE_CONNECT, FAILED;

    fun getCN(): String {
        return when (this) {
            IDLE -> "空闲"
            CONNECTING -> "正在连接"
            CONNECTED -> "已连接"
            RE_CONNECT -> "正在重连"
            FAILED -> "连接失败"
        }
    }
}