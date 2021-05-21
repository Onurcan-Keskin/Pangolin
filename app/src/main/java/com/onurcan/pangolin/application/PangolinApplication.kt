package com.onurcan.pangolin.application

import android.app.Application
import android.content.Context
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.QueueProcessingType
import com.onesignal.OneSignal
import com.onurcan.pangolin.helpers.Constants
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso


class PangolinApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setApp(this)

        initImageLoader(applicationContext)
        /* Picasso Loader */
        val builder = Picasso.Builder(this)
        builder.downloader(OkHttp3Downloader(this, Long.MAX_VALUE))
        val built = builder.build()
        built.setIndicatorsEnabled(false)
        built.isLoggingEnabled = true
        Picasso.setSingletonInstance(built)

        /* OneSignal */
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        OneSignal.initWithContext(this)
        OneSignal.setAppId(Constants.oneSignalAppID)
    }

    fun initImageLoader(context: Context?) {
        val config = ImageLoaderConfiguration.Builder(context)
        config.threadPriority(Thread.NORM_PRIORITY - 2)
        config.denyCacheImageMultipleSizesInMemory()
        config.diskCacheFileNameGenerator(Md5FileNameGenerator())
        config.diskCacheSize(100 * 1024 * 1024)//100 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO)
        config.writeDebugLogs()

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build())
    }

    companion object {
        var instance: PangolinApplication? = null
            private set

        val context: Context
            get() = instance!!.applicationContext

        @Synchronized
        private fun setApp(app: PangolinApplication) {
            instance = app
        }
    }
}