package com.moos.image.loader.loader


/**
 * @author moosphon
 *
 * Provide available options for image loader.
 * Such as cache size, loading mode, placeholder, error state, image transformation and so on.
 * Why not use Builder mode?
 * 这里是用的是kotlin中的默认参数语法糖：
 * 1. 增加新的配置属性无需额外改动，后期维护更容易
 * 2. 简化代码量，没有一大堆的setter/getter方法
 * 3. 纯kotlin工程，不需要考虑与Java的互操作
 */
data class LoadOptions(
                       var url: String = "",
                       var error: Int = DEFAULT_ERROR_PLACEHOLDER,
                       var placeholder: Int = DEFAULT_ERROR_PLACEHOLDER,
                       var angle: Int = 0,
                       var isSkipMemory: Boolean = false,
                       var isGif: Boolean = false,
                       var displayStyle: LoaderImageScaleType = LoaderImageScaleType.CENTER_CROP,
                       var cacheStyle:
                       LoaderCacheStrategy = LoaderCacheStrategy.RESULT,
                       var loadResultListener: LoaderRequestCallBack? = null) {

    companion object {
        //默认情况下不展示图片加载时占位图和加载失败的占位图
        const val DEFAULT_ERROR_PLACEHOLDER = -1
    }

    /**
     * 图片缓存策略
     */
    enum class LoaderCacheStrategy {
        NONE,
        ALL,
        SOURCE,
        RESULT
    }


    enum class LoaderImageScaleType {
        CENTER_CROP,
        CENTER_INSIDE,
        CENTER_FIT
    }
}