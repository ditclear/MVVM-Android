package io.ditclear.app.model.remote

import io.ditclear.app.model.data.Article
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 页面描述：PaoService
 *
 * Created by ditclear on 2017/11/19.
 */
interface PaoService{
    /**
     * 文章详情
     */
    @GET("article_detail.php")
    fun getArticleDetail(@Query("id") id: Int): Single<Article>

}