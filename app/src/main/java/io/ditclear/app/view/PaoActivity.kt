package io.ditclear.app.view

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.facebook.stetho.Stetho
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import dagger.android.AndroidInjection
import io.ditclear.app.BuildConfig
import io.ditclear.app.R
import io.ditclear.app.databinding.PaoActivityBinding
import io.ditclear.app.viewmodel.PaoViewModel
import javax.inject.Inject

class PaoActivity : RxAppCompatActivity() {

    lateinit var mBinding : PaoActivityBinding

    lateinit var mViewModel : PaoViewModel

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        //////di
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(applicationContext)
        }
        mBinding=DataBindingUtil.setContentView(this,R.layout.pao_activity)
        setSupportActionBar(mBinding.toolbar)
        mBinding.webView.setOnLongClickListener { true }
        mViewModel=ViewModelProviders.of(this,factory).get(PaoViewModel::class.java)
        ////binding
        mBinding.vm=mViewModel
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
                R.id.action_refresh -> mViewModel.loadArticle().compose(bindToLifecycle())
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
