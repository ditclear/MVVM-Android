package io.ditclear.app.helper

import android.databinding.BindingAdapter
import android.view.View
import android.widget.Toast
import us.feras.mdv.MarkdownView

/**
 * 页面描述：NormalBinds
 *
 * Created by ditclear on 2017/11/19.
 */
@BindingAdapter(value = "markdown")
fun bindMarkDown(v: MarkdownView, markdown: String?) {
    markdown?.let {
        v.setMarkdown(it)
    }
}

@BindingAdapter(value = "toast")
fun bindToast(v: View,msg:Throwable ?){
    msg?.let {
        Toast.makeText(v.context,it.message,Toast.LENGTH_SHORT).show()
    }
}