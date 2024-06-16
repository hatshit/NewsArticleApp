package com.harshittest.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface DaoArticle {
    @Query("SELECT * FROM articles")
    suspend fun getAllArticles(): List<EntityArticle>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<EntityArticle>)

    @Query("DELETE FROM articles")
    suspend fun deleteAllArticles()
}