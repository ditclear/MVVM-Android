package io.ditclear.app.view

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.ditclear.app.R
import io.ditclear.app.databinding.PaoActivityBinding
import io.ditclear.app.model.remote.PaoService
import io.ditclear.app.viewmodel.PaoViewModel
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class PaoActivity : AppCompatActivity() {

    lateinit var mBinding : PaoActivityBinding
    lateinit var mViewMode : PaoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding=DataBindingUtil.setContentView(this,R.layout.pao_activity)

        //////model
        val remote=Retrofit.Builder()
                .baseUrl("http://api.jcodecraeer.com/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(PaoService::class.java)

        /////ViewModel
        mViewMode= PaoViewModel(remote)
        ////binding
        mBinding.vm=mViewMode
    }
}
