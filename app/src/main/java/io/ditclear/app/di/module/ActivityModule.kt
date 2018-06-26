package io.ditclear.app.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.ditclear.app.view.PaoActivity

/**
 * 页面描述：ActivityModule
 *
 * Created by ditclear on 2018/6/25.
 */
@Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract fun contributePaoActivity(): PaoActivity

}