package io.ditclear.app.model.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * 页面描述：Article
 *
 * Created by ditclear on 2017/11/19.
 */

@Entity(tableName = "articles")
class Article(var title: String?){

    @PrimaryKey
    @ColumnInfo(name = "articleid")
    var id: Int = 0
    var content: String? = null
    var readme: String? = null
    @SerializedName("describe")
    var description: String? = null
    var click: Int = 0
    var channel: Int = 0
    var comments: Int = 0
    var stow: Int = 0
    var upvote: Int = 0
    var downvote: Int = 0
    var url: String? = null
    var pubDate: String? = null
    var thumbnail: String? = null




}