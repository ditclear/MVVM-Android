package io.ditclear.app.model.repository

import io.ditclear.app.model.local.dao.PaoDao
import io.ditclear.app.model.remote.PaoService

/**
 * 页面描述：PaoRepo
 *
 * Created by ditclear on 2018/4/14.
 */
class PaoRepo constructor(private val remote:PaoService, private val local :PaoDao){

    fun getArticleDetail(id:Int)= local.getArticleById(id)
            .onErrorResumeNext {
                remote.getArticleById(id)
                        .doOnSuccess { local.insertArticle(it) }
            }


}