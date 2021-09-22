package com.sssoyalan.newsapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sssoyalan.newsapp.models.Article

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article) : Long

    @Query(" SELECT * FROM article")
    fun getAll(): LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)

}