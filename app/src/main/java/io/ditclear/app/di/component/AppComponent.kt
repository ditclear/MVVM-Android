package io.ditclear.app.di.component

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import io.ditclear.app.PaoApp
import io.ditclear.app.di.module.ActivityModule
import io.ditclear.app.di.module.AppModule
import io.ditclear.app.di.module.ViewModelModule
import javax.inject.Singleton

@Singleton
@Component(modules = [(AndroidInjectionModule::class), (AppModule::class), (ViewModelModule::class), (ActivityModule::class)]
)
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(application: PaoApp)
}