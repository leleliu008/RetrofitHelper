package com.fpliu.newton.http.download

import java.io.File

interface DownloadInfoPersistent {
    /**
     * 把downloadInfo持久化
     */
    fun write(file: File, downloadInfo: DownloadInfo): Boolean

    /**
     * 把持久化的downloadInfo读取到内存中
     */
    fun read(file: File): DownloadInfo?
}