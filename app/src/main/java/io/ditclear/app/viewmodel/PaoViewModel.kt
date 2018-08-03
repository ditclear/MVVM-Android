package io.ditclear.app.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.ditclear.app.helper.Utils
import io.ditclear.app.helper.async
import io.ditclear.app.helper.set
import io.ditclear.app.model.data.Article
import io.ditclear.app.model.repository.PaoRepo
import io.reactivex.Single
import javax.inject.Inject

/**
 * 页面描述：PaoViewModel
 * @param animal 数据源Model(MVVM 中的M),负责提供ViewModel中需要处理的数据
 * Created by ditclear on 2017/11/17.
 */
class PaoViewModel @Inject constructor(private val repo: PaoRepo) :ViewModel(){

    //////////////////data//////////////
    val loading=MutableLiveData<Boolean>()
    val content = MutableLiveData<String>()
    val title = MutableLiveData<String>()

    val error = MutableLiveData<Throwable>()

    init {
        loading.set(false)
    }

    //////////////////binding//////////////
    fun loadArticle():Single<Article> =
            repo.getArticleDetail(8773)
                .async(1000)
                .doOnSuccess { t: Article? ->
                    t?.let {
                        title.set(it.title)
                        it.content?.let {
                            val articleContent=Utils.processImgSrc(it)
                            content.set(articleContent)
                        }

                    }
                }
                .doOnSubscribe { startLoad()}
                .doAfterTerminate { stopLoad() }



    private fun startLoad(){
        loading.set(true)
        title.set("Loading...")
    }
    private fun stopLoad()=loading.set(false)
}