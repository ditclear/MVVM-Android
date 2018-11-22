package io.ditclear.app.model.repository

import android.arch.persistence.room.EmptyResultSetException
import io.ditclear.app.model.data.Article
import io.ditclear.app.model.local.dao.PaoDao
import io.ditclear.app.model.remote.PaoService
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

/**
 * 页面描述：PaoRepoTest
 *
 * Created by ditclear on 2018/11/21.
 */
class PaoRepoTest {

    private val local = mock(PaoDao::class.java)
    private val remote = mock(PaoService::class.java)
    private val repo = PaoRepo(remote, local)
    private val article = mock(Article::class.java)

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

    }

    @Test fun `local getArticleById`(){
        //当本地能查到数据
        whenever(local.getArticleById(1)).thenReturn(Single.just(article))
        repo.getArticleDetail(1).test()
        //验证local.getArticleById()被调用
        verify(local).getArticleById(1)
        //验证remote.getArticleById()方法不被调用
        verify(remote, never()).getArticleById(1)
        //验证local.insertArticle()方法不被调用
        verify(local, never()).insertArticle(article)
    }

    @Test
    fun `remote getArticleById`() {
        //当本地不能查到数据,则访问网络数据
        whenever(local.getArticleById(1)).thenReturn(Single.error<Article>(EmptyResultSetException("")))
        //当调用remote.getArticleById(1)时返回数据
        whenever(remote.getArticleById(1)).thenReturn(Single.just(article))
        repo.getArticleDetail(1).test()
        //验证local.getArticleById(1)方法被调用
        verify(local).getArticleById(1)
        //验证remote.getArticleById(1)方法被调用
        verify(remote).getArticleById(1)
        //验证local.insertArticle(article)方法被调用
        verify(local).insertArticle(article)
    }

    private fun whenever(t: Any?) = `when`(t)

}