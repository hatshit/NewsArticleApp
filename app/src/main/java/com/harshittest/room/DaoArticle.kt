package com.harshittest.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.harshittest.model.Article


@Dao
interface DaoArticle {
    @Query("SELECT * FROM articles")
    suspend fun getAllArticles(): List<Article>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<Article>)

    @Query("DELETE FROM articles")
    suspend fun deleteAllArticles()
}