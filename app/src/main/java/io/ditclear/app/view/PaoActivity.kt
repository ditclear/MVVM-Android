package io.ditclear.app.view

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.facebook.stetho.Stetho
import io.ditclear.app.BuildConfig
import io.ditclear.app.R
import io.ditclear.app.databinding.PaoActivityBinding
import io.ditclear.app.di.component.DaggerAppComponent
import io.ditclear.app.di.module.AppModule
import io.ditclear.app.helper.bindLifeCycle
import io.ditclear.app.viewmodel.PaoViewModel
import javax.inject.Inject

class PaoActivity : AppCompatActivity() {

    lateinit var mBinding: PaoActivityBinding

    @Inject
    lateinit var mViewModel: PaoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(applicationContext)
        }

        mBinding = DataBindingUtil.setContentView(this, R.layout.pao_activity)
        setSupportActionBar(mBinding.toolbar)
        mBinding.webView.setOnLongClickListener { true }
        //////di
        getComponent().inject(this)
        ////binding
        mBinding.vm = mViewModel
    }

    fun getComponent() = DaggerAppComponent.builder()
            .appModule(AppModule(applicationContext)).build()

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.let {
            menuInflater.inflate(R.menu.detail_menu, it)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            when (it.itemId) {
                R.id.action_refresh -> loadData()
                else -> { }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadData() {
        mViewModel.loadArticle()
                .bindLifeCycle(this)
                .subscribe({}, { dispatchError(it) })
    }

    private fun dispatchError(error: Throwable?, length: Int = Toast.LENGTH_SHORT) {
        error?.let {
            if (BuildConfig.DEBUG) {
                it.printStackTrace()
            }
            Toast.makeText(this, it.message, length).show()
        }
    }
}


