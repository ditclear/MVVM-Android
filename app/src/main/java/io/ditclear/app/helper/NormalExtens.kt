package io.ditclear.app.helper

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import us.feras.mdv.MarkdownView
import java.util.concurrent.TimeUnit

/**
 * 页面描述：NormalExtens
 *
 * Created by ditclear on 2017/11/19.
 */
fun MarkdownView.setMarkdown(markdown: String?) {
    loadMarkdown(markdown, "file:///android_asset/markdown.css")
}

fun <T> Single<T>.async(withDelay: Long = 0): Single<T> =
        this.subscribeOn(Schedulers.io())
                .delay(withDelay, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
