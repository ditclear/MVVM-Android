package io.ditclear.app.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

/**
 * 页面描述：PaoViewModelFactory
 *
 * Created by ditclear on 2018/7/4.
 */

class PaoViewModelFactory @Inject constructor(private val creators:Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val creator = creators[modelClass]?:creators.entries.firstOrNull{
            modelClass.isAssignableFrom(it.key)
        }?.value?:throw IllegalArgumentException("unknown model class $modelClass")
        try {
            @Suppress("UNCHECKED_CAST")
            return creator.get() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

}