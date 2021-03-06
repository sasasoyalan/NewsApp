package com.sssoyalan.newsapp.models.news

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sssoyalan.newsapp.models.news.Source
import java.io.Serializable

@Entity(tableName = "article")
data class Article(
    @PrimaryKey( autoGenerate = true ) val uId: Int? = null,
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: Source?,
    val title: String?,
    val url: String?,
    var fav : Boolean?,
    val urlToImage: String?
) : Serializable
