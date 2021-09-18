package com.sssoyalan.newsapp.models.today

data class Today(
    val pagecreatedate: String,
    val status: String,
    val success: Boolean,
    val tarihtebugun: List<Tarihtebugun>
)