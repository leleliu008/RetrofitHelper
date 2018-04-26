package com.fpliu.newton.http.download

/**
 * @author 792793182@qq.com 2017-08-03.
 */
data class DownloadInfo(val filePath: String,
                        val lastModified: String?,
                        val totalByte: Long,
                        val eTag: String?,
                        val isFromNetwork: Boolean) {

    constructor() : this("", null, 0L, null, false)

    fun toJSON(): String {
        val eTag2 = eTag?.replace("\"","\\\"")
        return "{\"filePath\":\"$filePath\",\"lastModified\":\"$lastModified\",\"totalByte\":$totalByte,\"eTag\":\"$eTag2\"}"
    }
}
