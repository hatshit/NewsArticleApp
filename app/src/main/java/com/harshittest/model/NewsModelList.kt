package com.harshittest.model

data class NewsModelList(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)