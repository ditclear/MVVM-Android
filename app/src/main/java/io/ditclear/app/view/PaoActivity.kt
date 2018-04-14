package io.ditclear.app.view

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.facebook.stetho.Stetho
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import io.ditclear.app.BuildConfig
import io.ditclear.app.R
import io.ditclear.app.databinding.PaoActivityBinding
import io.ditclear.app.helper.Constants
import io.ditclear.app.model.local.AppDatabase
import io.ditclear.app.model.remote.PaoService
import io.ditclear.app.model.repository.PaoRepo
import io.ditclear.app.viewmodel.PaoViewModel
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class PaoActivity : RxAppCompatActivity() {

    lateinit var mBinding : PaoActivityBinding
    lateinit var mViewMode : PaoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(applicationContext)
        }

        mBinding=DataBindingUtil.setContentView(this,R.layout.pao_activity)
        setSupportActionBar(mBinding.toolbar)
        mBinding.webView.setOnLongClickListener { true }
        //////model
        val remote=Retrofit.Builder()
                .baseUrl(Constants.HOST_API)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(PaoService::class.java)

        val local=AppDatabase.getInstance(applicationContext).paoDao()

        /////ViewModel
        mViewMode= PaoViewModel(PaoRepo(remote, local))
        ////binding
        mBinding.vm=mViewMode
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.let {
            menuInflater.inflate(R.menu.detail_menu,it)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            when(it.itemId){
                R.id.action_refresh -> mViewMode.loadArticle().compose(bindToLifecycle())
                        .subscribe { _, error -> dispatchError(error) }
                else -> { }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //依旧不依赖于具体实现
    private fun dispatchError(error:Throwable?, length:Int=Toast.LENGTH_SHORT){
        error?.let {
            it.printStackTrace()
            Toast.makeText(this,it.message,length).show()
        }
    }
}
