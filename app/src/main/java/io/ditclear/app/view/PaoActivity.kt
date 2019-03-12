package io.ditclear.app.view

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.uber.autodispose.AutoDispose
import com.uber.autodispose.SingleSubscribeProxy
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import io.ditclear.app.BuildConfig
import io.ditclear.app.R
import io.ditclear.app.databinding.PaoActivityBinding
import io.ditclear.app.viewmodel.PaoViewModel
import io.reactivex.Single
import org.koin.android.viewmodel.ext.android.getViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class PaoActivity : AppCompatActivity() {

    private val mBinding: PaoActivityBinding by lazy {
        DataBindingUtil.setContentView<PaoActivityBinding>(this, R.layout.pao_activity)
    }

    //di
    private val mViewModel: PaoViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(mBinding.toolbar)
        ////binding
        mBinding.vm = mViewModel
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.let {
            menuInflater.inflate(R.menu.detail_menu, it)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_refresh -> mViewModel.loadArticle()
                    .bindLifeCycle(this)
                    .subscribe({}, { dispatchFailure(it) })
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //处理错误
    private fun dispatchFailure(error: Throwable?, length: Int = Toast.LENGTH_SHORT) {
        error?.let {
            if (BuildConfig.DEBUG) {
                it.printStackTrace()
            }
            Toast.makeText(this, it.message, length).show()
        }
    }
    fun <T> Single<T>.bindLifeCycle(owner: LifecycleOwner): SingleSubscribeProxy<T> =
            this.`as`(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(owner, Lifecycle.Event.ON_DESTROY)))

}
