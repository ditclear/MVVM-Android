package io.ditclear.app.di

import io.ditclear.app.helper.Constants
import io.ditclear.app.model.local.AppDatabase
import io.ditclear.app.model.local.dao.PaoDao
import io.ditclear.app.model.remote.PaoService
import io.ditclear.app.model.repository.PaoRepo
import io.ditclear.app.viewmodel.PaoViewModel
import io.reactivex.Single
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.experimental.builder.viewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import org.koin.experimental.builder.create
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val viewModelModule = module {
    viewModel { PaoViewModel(get<PaoRepo>()) }
    //or use reflection
//    viewModel<PaoViewModel>()

}

val repoModule = module {

    factory  <PaoRepo> { PaoRepo(get(), get()) }
    //其实就是
    //factory <PaoRepo> { PaoRepo(get<PaoService>(), get<PaoDao>())  }

}

val remoteModule = module {

    single<Retrofit> {
        Retrofit.Builder()
                .baseUrl(Constants.HOST_API)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    single<PaoService> { get<Retrofit>().create(PaoService::class.java) }
}


val localModule = module {

    single<AppDatabase> { AppDatabase.getInstance(androidApplication()) }

    single<PaoDao> { get<AppDatabase>().paoDao() }
}


val appModule = listOf(viewModelModule, repoModule, remoteModule, localModule)