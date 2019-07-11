package com.moos.image.loader.app

import android.app.Application
import android.content.ComponentCallbacks2
import com.bumptech.glide.Glide
import com.moos.image.loader.glide.GlideLoaderStrategy
import com.moos.image.loader.loader.ImageLoader

class GlobalApp : Application() {

    override fun onCreate() {
        super.onCreate()
        //初始化全局图片加载策略
        ImageLoader.INSTANCE.init(GlideLoaderStrategy())
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            ImageLoader.INSTANCE.clearMemoryCache(this)
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        ImageLoader.INSTANCE.clearMemoryCache(this)
    }
}