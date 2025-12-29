package com.ykt.musicplayer.data.cache

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File

@UnstableApi
object AudioCache {
    private const val CACHE_SIZE: Long = 100 * 1024 * 1024 // 100MB

    private var simpleCache: SimpleCache? = null

    fun getCache(context: Context): SimpleCache {
        if (simpleCache == null) {
            val cacheDir = File(context.cacheDir, "media")
            val evictor = LeastRecentlyUsedCacheEvictor(CACHE_SIZE)

            simpleCache = SimpleCache(
                cacheDir,
                evictor,
                StandaloneDatabaseProvider(context),
            )
        }
        return simpleCache!!
    }
}