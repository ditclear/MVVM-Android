package io.ditclear.app.di.component

import dagger.Component
import io.ditclear.app.di.module.AppModule
import io.ditclear.app.view.PaoActivity
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent{

    fun inject(activity: PaoActivity)
}