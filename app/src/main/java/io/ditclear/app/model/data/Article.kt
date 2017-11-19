package io.ditclear.app.model.data

/**
 * 页面描述：Article
 *
 * Created by ditclear on 2017/11/19.
 */
data class Article(
        var id: Int = 0,
        var title: String?,
        var readme: String?,
        var describe: String?,
        var click: Int = 0,
        var channel: Int = 0,
        var comments: Int = 0,
        var stow: Int = 0,
        var upvote: Int = 0,
        var downvote: Int = 0,
        var url: String?,
        var pubDate: String?,
        var thumbnail: String?) {

    var content: String? = null


}
