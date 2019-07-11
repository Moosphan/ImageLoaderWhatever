package com.moos.image.loader.loader

import android.content.Context
import android.view.View
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory

/**
 * @author moosphon
 *
 * The actual using loader for images.
 * User no need to care the loader's engine internal mechanism.
 * Just care about which engine strategy you want to use.
 */
class ImageLoader private constructor() {

    private lateinit var mOptions: LoadOptions

    /**
     * 全局单例实现,延迟加载
     */
    companion object {
        val INSTANCE : ImageLoader by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { ImageLoader() }
        private var mLoader: ILoader? = null
    }

    /**
     * 初始化并设置图片加载策略
     * 在[android.app.Application]中
     */
    fun init(loaderStrategy: ILoader) {
        mLoader = loaderStrategy
    }

//    fun with(data: LoadOptions.() -> Unit) = LoadOptions().apply(data){
//        mOptions = this
//    }

    /**
     * 借助于kotlin[apply]方法以及lambda表达式来构造生成[LoadOptions]
     * 简化代码，提升可读性
     */
    fun with(data: LoadOptions.() -> Unit): ImageLoader {
        mOptions = LoadOptions().apply(data)
        return this
    }

    /**
     * 将图片加载到目标视图[View]
     * 这里之所以用[View]，是由于第三方图片加载库很可能会出现自定义ImageView的情况
     * 例如：Facebook的Fresco
     * 需要注意的是：必须要确保此时[mOptions]以及初始化完毕
     */
    fun into(target: View) {
        mLoader?.loadImage(target, mOptions) ?: error()
    }

    /**
     * 清除磁盘缓存(子线程)
     */
    fun clearDiskCache(ctx: Context) {
        mLoader?.clearDiskCache(ctx) ?: error()
    }

    /**
     * 清除内存缓存(UI线程)
     */
    fun clearMemoryCache(ctx: Context) {
        mLoader?.clearMemoryCache(ctx) ?: error()
    }

    /**
     * 获取图片缓存大小
     */
    fun getCacheSize(ctx: Context) : String {
        return mLoader?.getCacheSize(ctx) ?: error()
    }


    private fun error(): Nothing =
        throw IllegalArgumentException("You must provide an image loader strategy first!")


}