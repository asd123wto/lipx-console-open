package xyz.labmem.lipx.server.core

import cn.hutool.core.io.file.FileReader
import cn.hutool.core.io.file.FileWriter
import cn.hutool.crypto.CryptoException
import cn.hutool.crypto.SecureUtil
import cn.hutool.crypto.symmetric.SymmetricAlgorithm
import cn.hutool.crypto.symmetric.SymmetricCrypto
import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil
import cn.hutool.log.StaticLog
import xyz.labmem.lipx.core.ServerConfig
import xyz.labmem.lipx.core.tool.getJarParentPath
import xyz.labmem.lipx.server.shutdown
import java.io.File


/**
 * @description: 持久化配置信息
 * @author: liutianyu
 * @date: 2022/6/15 12:23
 */
val sysVersion = System.getProperties()["os.name"]

class ConfigData {

    companion object {

        var key: String? = null

        private var hasKey = true

        private var appPath: String = when (sysVersion) {
            "Windows 10", "Windows 11" -> {
                "${System.getenv("APPDATA")}/labmemApp/lipx-server"
            }

            "Linux" -> {
                (System.getProperty("user.home") ?: "~") + "/labmemApp/lipx-server"
            }

            else -> {
                "${ConfigData::class.getJarParentPath()}/labmemApp/lipx-server"
            }
        }

        fun init() {
            if (key == null) {
                key = "labmem-lipxServer"
                hasKey = false
            }

            val dir = File(appPath)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            var labkeyName: String? = null
            if (hasKey) {
                File("$appPath/lipx_s.labkey").apply {
                    if (!exists()) {
                        labkeyName = "/lipx_s.labkey"
                    }
                }
            } else {
                File("$appPath/lipx.labkey").apply {
                    if (!exists())
                        labkeyName = "/lipx.labkey"
                }
            }
            labkeyName?.let {
                val smkey = SecureUtil.generateKey(SymmetricAlgorithm.DESede.value).encoded
                FileWriter(appPath + it).write(smkey, 0, smkey.size)
                //生成签名
                val dataStr = SymmetricCrypto(SymmetricAlgorithm.DESede, smkey).encryptHex(key)
                FileWriter("$appPath/lipx.labsign").write(dataStr)
            }
        }

        private fun getKey(): SymmetricCrypto {
            val labsign = File("$appPath/lipx.labsign")
            if (labsign.exists()) {
                //读取私密密钥
                val keyFile = File("$appPath/lipx_s.labkey")
                if (keyFile.exists()) {
                    val sm = SymmetricCrypto(SymmetricAlgorithm.DESede, FileReader(keyFile).readBytes())
                    if (sm.decryptStr(FileReader(labsign).readString()) == key)
                        return sm
                }
            }
            //读取默认密钥
            val keyFile = File("$appPath/lipx.labkey")
            if (!keyFile.exists())
                shutdown()
            return SymmetricCrypto(SymmetricAlgorithm.DESede, FileReader(keyFile).readBytes())
        }

        private fun read(): JSONObject? {
            File("$appPath${if (hasKey) "/lipxSer_s.data" else "/lipxSer.data"}").apply {
                if (exists()) {
                    val data = FileReader(this).readString()
                    try {
                        getKey().apply {
                            val decryptStr: String = decryptStr(data)
                            return JSONUtil.parseObj(decryptStr)
                        }
                    } catch (ce: CryptoException) {
                        StaticLog.warn("数据密钥错误，若进行操作将覆盖原来的数据！")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    write(ServerConfig())
                    return read()
                }
            }
            return null
        }

        //写入文件
        fun write(config: ServerConfig) {
            getKey().apply {
                val dataStr = encryptHex(JSONUtil.parseObj(config).toStringPretty())
                if (hasKey) {
                    FileWriter("$appPath/lipxSer_s.data").write(dataStr)
                } else
                    FileWriter("$appPath/lipxSer.data").write(dataStr)
            }
        }

        fun refreshData() {
            AppContext.cacheData = read()?.toBean(ServerConfig::class.java)
        }

    }

}