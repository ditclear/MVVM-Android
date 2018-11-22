package io.ditclear.app.viewmodel

import io.ditclear.app.ImmediateSchedulerRule
import io.ditclear.app.model.data.Article
import io.ditclear.app.model.local.dao.PaoDao
import io.ditclear.app.model.remote.PaoService
import io.ditclear.app.model.repository.PaoRepo
import io.reactivex.Single
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

/**
 * 页面描述：PaoViewModelTest
 *
 * Created by ditclear on 2018/11/16.
 */
@RunWith(JUnit4::class)
class PaoViewModelTest {

    @Mock
    lateinit var remote: PaoService

    @Mock
    lateinit var local: PaoDao

    lateinit var repo: PaoRepo

    lateinit var viewModel: PaoViewModel

    private val article = mock(Article::class.java)


    @get:Rule
    val testScheduler = ImmediateSchedulerRule.instance


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        repo = spy(PaoRepo(remote, local))
        viewModel = spy(PaoViewModel(repo))

        //让local.getArticleById()方法返回可观测的article
        whenever(local.getArticleById(anyInt())).thenReturn( Single.just(article))
    }

    @Test
    fun `loadArticle success`() {

        //调用方法，进行验证
        viewModel.loadArticle().test()
        testScheduler.advanceTimeBy(500)
        //验证加载中时loading为true
        Assert.assertThat(viewModel.loading.get(),`is`(true))
        //由于有async(1000).1000毫秒的延迟，这里需要加快时间
        testScheduler.advanceTimeBy(500)
        //验证renderDetail()方法有调用
        verify(viewModel).renderDetail(article)
        //验证加载完成时loading为false
        Assert.assertThat(viewModel.loading.get(),`is`(false))

    }

    @Test
    fun `loadArticle success but content is null`() {

        //当访问article的title属性时返回paonet
        whenever(article.title).thenReturn("paonet")
        //当访问article的content属性时返回null
        whenever(article.content).thenReturn(null)

        //调用方法，进行验证
        viewModel.loadArticle().test()

        //由于有async(1000).1000毫秒的延迟，这里需要加快时间
        testScheduler.advanceTimeBy(1000)

        //验证renderDetail()方法有调用
        verify(viewModel).renderDetail(article)

        //验证title的值为paonet
        Assert.assertThat(viewModel.title.get(),`is`("paonet"))
        //验证viewModel.content.get()的值为null
        Assert.assertNull(viewModel.content.get())
    }

    private fun whenever(t: Any?) = `when`(t)
}