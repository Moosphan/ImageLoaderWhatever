package com.moos.image.loader.glide

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.moos.image.loader.loader.ILoader
import com.moos.image.loader.loader.LoadOptions
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import java.io.File
import java.util.*


/**
 * @author moosphon
 *
 * Load image by Glide strategy.
 */
class GlideLoaderStrategy : ILoader {
    override fun getCacheSize(context: Context): String {
        return formatCacheSize(getGlideCacheSize(File(getCacheDir())))
    }

    override fun loadImage(target: View, loadOptions: LoadOptions) {
        requestImage(target, loadOptions)
    }

    @SuppressLint("CheckResult")
    private fun requestImage(target: View, options: LoadOptions){
        val builder: RequestBuilder<Any> = generateRequestBuilder(target, options) as RequestBuilder<Any>
        builder.listener(object : RequestListener<Any>{
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Any>?,
                isFirstResource: Boolean
            ): Boolean {
                if (options.loadResultListener != null) {
                    options.loadResultListener?.onFailed()
                }
                return false
            }

            override fun onResourceReady(
                resource: Any?,
                model: Any?,
                target: Target<Any>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                if (options.loadResultListener != null) {
                    options.loadResultListener?.onSuccess()
                }
                return false
            }

        })
        if (!options.url.isBlank())
            builder.load(options.url).into(target as ImageView)
        else
            throw IllegalArgumentException("You should give an image resource URL first!")
    }

    override fun clearMemoryCache(context: Context) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Glide.get(context).clearMemory()
        }
    }

    override fun clearDiskCache(context: Context) {
        Glide.get(context).clearDiskCache()
    }

    override fun pause(context: Context) {
        Glide.with(context).pauseRequests()
    }

    override fun resume(context: Context) {
        Glide.with(context).resumeRequests()
    }

    /**
     * Apply loader options and generate image request builder.
     * You can see loader option detail from [LoadOptions]
     */
    @SuppressLint("CheckResult")
    private fun generateRequestBuilder(targetView: View, options: LoadOptions) : RequestBuilder<*> {
        val requestBuilder = if (options.isGif)
            Glide.with(targetView).asGif()
        else
            Glide.with(targetView).asBitmap()
        requestBuilder.apply(getLoaderOptions(options))
        return requestBuilder
    }

    /**
     * generate glide options [com.bumptech.glide.request.RequestOptions] for displaying image.
     */
    @SuppressLint("CheckResult")
    private fun getLoaderOptions(options: LoadOptions) : RequestOptions {
        val glideOptions = RequestOptions()
        if (options.error != LoadOptions.DEFAULT_ERROR_PLACEHOLDER)
            glideOptions.fallback(options.error)
        if (options.placeholder != LoadOptions.DEFAULT_ERROR_PLACEHOLDER)
            glideOptions.placeholder(options.placeholder)
        when(options.cacheStyle) {
            LoadOptions.LoaderCacheStrategy.NONE     -> glideOptions.diskCacheStrategy(DiskCacheStrategy.NONE)
            LoadOptions.LoaderCacheStrategy.ALL      -> glideOptions.diskCacheStrategy(DiskCacheStrategy.ALL)
            LoadOptions.LoaderCacheStrategy.SOURCE   -> glideOptions.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            LoadOptions.LoaderCacheStrategy.RESULT   -> glideOptions.diskCacheStrategy(DiskCacheStrategy.DATA)
        }
        if (options.angle != 0) {
            glideOptions.transform(RoundedCornersTransformation(options.angle, 0))
        }

        if (options.isSkipMemory) {
            glideOptions.skipMemoryCache(true)
        }

        when (options.displayStyle) {
            LoadOptions.LoaderImageScaleType.CENTER_CROP    -> glideOptions.centerCrop()
            LoadOptions.LoaderImageScaleType.CENTER_INSIDE  -> glideOptions.centerInside()
            LoadOptions.LoaderImageScaleType.CENTER_FIT     -> glideOptions.fitCenter()
        }

        return glideOptions
    }

    /**
     * 获取glide缓存目录的大小
     */
    private fun getGlideCacheSize(target: File) : Long {
        var size: Long = 0
        try {
            val fileList = target.listFiles()
            for (file in fileList) {
                size += if (file.isDirectory) getGlideCacheSize(file) else file.length()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return size
    }


    /**
     * glide默认缓存路径
     */
    private fun getCacheDir() = InternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR

    /**
     * 转化为文件的大小单位
     */
    private fun formatCacheSize(size: Long): String {
        return when {
            size < 0 -> throw IllegalArgumentException("File size cant less than 0!")
            size < 1024 -> String.format(Locale.getDefault(), "%.3fB", size.toDouble())
            size < 1048576 -> String.format(Locale.getDefault(), "%.3fKB", size.toDouble() / 1024)
            size < 1073741824 -> String.format(Locale.getDefault(), "%.3fMB", size.toDouble() / 1048576)
            else -> String.format(Locale.getDefault(), "%.3fGB", size.toDouble() / 1073741824)
        }
    }

}