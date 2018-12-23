package io.ditclear.app

import android.app.Application
import io.ditclear.app.di.appModule
import org.koin.android.ext.android.startKoin
import org.koin.android.logger.AndroidLogger

/**
 * 页面描述：PaoApp
 *
 * Created by ditclear on 2018/6/25.
 */
class PaoApp : Application() {


    override fun onCreate() {
        super.onCreate()

        startKoin(this, appModule, logger = AndroidLogger(showDebug = BuildConfig.DEBUG))
    }


}