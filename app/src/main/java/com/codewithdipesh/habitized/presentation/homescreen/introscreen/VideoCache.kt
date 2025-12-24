package com.codewithdipesh.habitized.presentation.homescreen.introscreen

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File

@UnstableApi
object VideoCache {
    private var simpleCache: SimpleCache? = null
    private const val MAX_CACHE_SIZE = 100L * 1024 * 1024 // 100 MB

    @Synchronized
    fun getInstance(context: Context): SimpleCache {
        if (simpleCache == null) {
            val cacheDir = File(context.cacheDir, "video_cache")
            val databaseProvider = StandaloneDatabaseProvider(context)
            val evictor = LeastRecentlyUsedCacheEvictor(MAX_CACHE_SIZE)
            simpleCache = SimpleCache(cacheDir, evictor, databaseProvider)
        }
        return simpleCache!!
    }

    @Synchronized
    fun release() {
        simpleCache?.release()
        simpleCache = null
    }

    @Synchronized
    fun clearCache(context: Context) {
        // First release the cache
        simpleCache?.release()
        simpleCache = null

        // Then delete the cache directory
        val cacheDir = File(context.cacheDir, "video_cache")
        if (cacheDir.exists()) {
            cacheDir.deleteRecursively()
        }
    }
}
