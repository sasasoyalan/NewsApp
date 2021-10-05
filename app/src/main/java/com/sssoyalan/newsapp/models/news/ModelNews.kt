package com.sssoyalan.newsapp.models.news

import java.io.Serializable


data class ModelNews(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
) : Serializable

data class Source(
        val id: Any,
        val name: String
) : Serializable