package com.fpliu.newton.http.download

import org.json.JSONObject
import java.io.File

/**
 * 持久化到文件中，这是默认实现，您可以参考此实现，比如，持久化到数据库中等
 */
open class DownloadInfoPersistent2File(private val persistentDir: String? = null) : DownloadInfoPersistent {

    override fun write(file: File, downloadInfo: DownloadInfo): Boolean {
        getMetaFile(file).writeText(downloadInfo.toJSON(), Charsets.UTF_8)
        return true
    }

    override fun read(file: File): DownloadInfo? {
        val metaFile = getMetaFile(file)
        return if (metaFile.exists()) {
            val jsonObject = JSONObject(metaFile.readText(Charsets.UTF_8))
            val lastModified = jsonObject.getString("lastModified")
            val totalByte = jsonObject.getLong("totalByte")
            val eTag = jsonObject.getString("eTag")
            DownloadInfo(file.absolutePath, lastModified, totalByte, eTag, false)
        } else null
    }

    protected fun getMetaFile(file: File): File {
        return if (persistentDir == null || persistentDir == "") {
            File("${file.absolutePath}.meta")
        } else {
            val dir = File(persistentDir)
            if (dir.exists()) {
                if (dir.isDirectory) {
                    File(dir, "${file.name}.meta")
                } else {
                    File("${file.absolutePath}.meta")
                }
            } else {
                if (dir.mkdirs()) {
                    File(dir, "${file.name}.meta")
                } else {
                    if (dir.exists()) {
                        File(dir, "${file.name}.meta")
                    } else {
                        File("${file.absolutePath}.meta")
                    }
                }
            }
        }
    }
}