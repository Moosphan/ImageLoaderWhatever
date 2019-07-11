package com.moos.image.loader.loader

import android.content.Context
import android.view.View

/**
 * provide base interface of strategy that used to load images.
 * @author Moosphon
 */
interface ILoader {
    /**
     * Load the image resource and apply options into target view.
     */
    fun loadImage(target: View, loadOptions: LoadOptions)

    /**
     * Clear the cache from app cache.
     */
    fun clearMemoryCache(context: Context)

    /**
     * Clear the cache from disk.
     */
    fun clearDiskCache(context: Context)

    /**
     * pause the image loading process.
     */
    fun pause(context: Context)

    /**
     * resume the image loading process.
     */
    fun resume(context: Context)

    /**
     * get size(MB) of image cache.
     */
    fun getCacheSize(context: Context) : String
}