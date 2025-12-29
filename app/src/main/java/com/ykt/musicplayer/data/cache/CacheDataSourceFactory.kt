package com.ykt.musicplayer.data.cache

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.CacheDataSource

@OptIn(UnstableApi::class)
fun buildCacheDataSourceFactory(context: Context): DataSource.Factory {
    val cache = AudioCache.getCache(context)
    val upstreamFactory = DefaultDataSource.Factory(context)
    return CacheDataSource.Factory()
        .setCache(cache)
        .setUpstreamDataSourceFactory(upstreamFactory)
        .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
}