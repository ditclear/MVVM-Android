package io.ditclear.app.viewmodel

import android.databinding.ObservableField
import io.ditclear.app.model.data.Article
import io.ditclear.app.model.remote.PaoService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * 页面描述：PaoViewModel
 * @param animal 数据源Model(MVVM 中的M),负责提供ViewModel中需要处理的数据
 * Created by ditclear on 2017/11/17.
 */
class PaoViewModel(val remote: PaoService) {

    //////////////////data//////////////
    val articleDetail = ObservableField<String>("点击按钮，调用ViewModel中的loadArticle方法，通过DataBinding更新UI")

    //////////////////binding//////////////
    fun loadArticle() {
        //为了简单起见这里先写个默认的id
        remote.getArticleDetail(8773)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ t: Article? ->
                    articleDetail.set(t?.toString())
                }, { t: Throwable? ->
                    articleDetail.set(t?.message ?: "error")
                })
    }
}