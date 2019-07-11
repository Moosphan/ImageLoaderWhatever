# ImageLoaderWhatever
Android 主流图片加载引擎的动态替换方案。

一行代码，即可解决项目后期更换图片加载引擎的繁琐操作痛点，提升项目的可维护性。

## 用法

这里以 Glide 图片加载框架为例：

1. 在 Application 中初始化：

   ```kotlin
   //指定图片加载策略
   ImageLoader.INSTANCE.init(GlideLoaderStrategy())
   ```

2. 具体用法：

   ```kotlin
   ImageLoader
              .INSTANCE
              .with {
                       url = "资源地址"
                				angle = 60
                       error = R.drawable.userheadholder
                       placeholder = R.drawable.userheadholder
                       cacheStyle = LoadOptions.LoaderCacheStrategy.RESULT
                       displayStyle = LoadOptions.LoaderImageScaleType.CENTER_CROP
               }.into(imageView)
   ```

## 思想

这里运用的是"策略模式"，如 **`GlideLoaderStrategy`** 只是我们其中的一种策略实现方式，你可以基于现有规则实现自己的策略，比如 **`PicassoLoaderStrategy`** 、**`FrescoLoaderStrategy`** 又或者其他任何图片加载引擎。当然，也可以使用其他设计模式（如代理模式），根据自己喜好，只要最终达到了解耦的目的即可。先来看一下预想的UML设计图：



![Android主流图片框架封装UML (2)](/Users/moos/Desktop/Android主流图片框架封装UML (2).png)

图中可以看出，我们最初的设想是：通过 `ImageLoader` 来管理整体的策略配置。正如面向对象原则中**依赖倒置原则**所阐述的那样，它只需要与 `ILoader` 产生依赖关系，而不应该与具体实现（如GlideLoaderStrategy）产生关系。这样一来，不论我们增加了其他任何一种策略的实现，都不会影响到我们最终代码使用的地方。

当然，为了简化我们 `ILoader` 中的大量参数配置的情况，我们可以将参数配置单独提取到 `LoadOptions` 类当中去：

```kotlin
data class LoadOptions(
                       var url: String = "",
                       var error: Int = DEFAULT_ERROR_PLACEHOLDER,
                       var placeholder: Int = DEFAULT_ERROR_PLACEHOLDER,
                       var angle: Int = 0,
                       var isSkipMemory: Boolean = false,
                       var isGif: Boolean = false,
                       var displayStyle: LoaderImageScaleType = 	            																					LoaderImageScaleType.CENTER_CROP,
                       var cacheStyle:
                       			LoaderCacheStrategy = LoaderCacheStrategy.RESULT,
                       var loadResultListener: LoaderRequestCallBack? = null) {


    companion object {
        //默认情况下不展示图片加载时占位图和加载失败的占位图
        const val DEFAULT_ERROR_PLACEHOLDER = -1
    }
}
```

由于本项目基于 Kotlin 语言，所以就借助于它那"得天独厚"的特性—默认参数方法，来实现图片加载所需参数的配置。可能会有人问：为什么不用建造者模式？其实我的想法很简单，看个人喜好就可以了。采用以上方式个人感觉有以下几个好处：

1. 增加新的配置属性无需额外改动，后期维护更佳容易。
2. 简化代码量，没有一大堆setter/getter方法，配置信息一目了然。
3. 纯kotlin工程，不需要考虑与Java的互操作性。

如果你对kotlin感兴趣，可以先去了解一下**默认参数**这个强大的特性，一旦用起来你会发现根本爱不释手😁。

## 如何扩展

如果想要接入其他图片加载引擎，可以实现自己的策略，只需要实现以下接口并重写对应方法即可：

```kotlin
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
```

## 关于

如果你看了本方案，有什么问题或者好的提议，欢迎 Issue 或者 PR😂。也欢迎感兴趣人士一起完善和丰富本项目，争取应用到实际项目中去。