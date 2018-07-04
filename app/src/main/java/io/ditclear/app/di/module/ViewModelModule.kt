package io.ditclear.app.di.module

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.ditclear.app.di.android.ViewModelKey
import io.ditclear.app.viewmodel.PaoViewModel
import io.ditclear.app.viewmodel.PaoViewModelFactory

/**
 * 页面描述：ViewModelModule
 *
 * Created by ditclear on 2018/7/4.
 */

@Module
abstract class ViewModelModule{

    @Binds
    @IntoMap
    @ViewModelKey(PaoViewModel::class)
    abstract fun bindPaoViewModel(viewModel: PaoViewModel):ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory:PaoViewModelFactory):ViewModelProvider.Factory
}