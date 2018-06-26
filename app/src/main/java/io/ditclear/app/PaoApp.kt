package io.ditclear.app

import android.app.Activity
import android.app.Application
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import io.ditclear.app.di.android.AppInjector
import javax.inject.Inject

/**
 * 页面描述：PaoApp
 *
 * Created by ditclear on 2018/6/25.
 */
class PaoApp : Application(),HasActivityInjector{

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()

        AppInjector.init(this)
    }

    override fun activityInjector() = dispatchingAndroidInjector

}