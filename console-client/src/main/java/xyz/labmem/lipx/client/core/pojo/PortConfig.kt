package xyz.labmem.lipx.client.core.pojo


/**
 * @description: do something
 * @author: liutianyu
 * @date: 2022/6/9 16:11
 */
class PortConfig {

    var id: String? = null

    var serverHost: String? = null

    var serverPort: Int? = null

    var proxyHost: String? = null

    var proxyPort: Int? = null

    var targetPort: Int? = null

    var password: String? = null

    var remark: String? = null

    var status = Status.IDLE

    var wls = HashSet<String>()

}