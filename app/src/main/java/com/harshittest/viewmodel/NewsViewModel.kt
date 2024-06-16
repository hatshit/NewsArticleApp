package com.harshittest.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.harshittest.apiclient.ApiClient
import com.harshittest.apiclient.ServiceApi
import com.harshittest.repository.NewsListRepository
import com.harshittest.room.AppDatabase
import com.harshittest.room.EntityArticle
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {

    private lateinit var repository: NewsListRepository
    private val _articles = MutableLiveData<List<EntityArticle>>()
    val articles: LiveData<List<EntityArticle>> get() = _articles
    private val _errorState = MutableLiveData<Boolean>()
    val errorState: LiveData<Boolean> get() = _errorState

    fun setRepository(context: Context) {
        val articleDao = AppDatabase.getDatabase(context).articleDao()
        val serviceApi: ServiceApi = ApiClient.getServices()
        repository = NewsListRepository(serviceApi, articleDao, context)
    }

    fun getNewsList() {
        Log.d("NewsViewModel", "Fetching news list")
        viewModelScope.launch {
            repository.getNewsListFromRepo(
                onSuccess = ::onSuccess,
                onError = ::onError
            )
        }
    }

    private fun onSuccess(json: String) {
        try {
            // Ensure the JSON is valid
            if (json.isNotEmpty() && json != "false") {
                // Check if the JSON is an array (coming from the database)
                if (json.startsWith("[")) {
                    val listType = object : TypeToken<List<EntityArticle>>() {}.type
                    val articles: List<EntityArticle> = Gson().fromJson(json, listType)
                    Log.d("NewsViewModel", "Parsed articles from DB: ${articles.size}")
                    _articles.postValue(articles)
                } else {
                    // Parse JSON object (coming from the network)
                    val jsonObject = JsonParser.parseString(json).asJsonObject
                    val articlesJsonArray = jsonObject.getAsJsonArray("articles")

                    val articles: List<EntityArticle> = articlesJsonArray.map { jsonElement ->
                        val jsonObj = jsonElement.asJsonObject
                        EntityArticle(
                            author = jsonObj.get("author")?.takeIf { !it.isJsonNull }?.asString,
                            title = jsonObj.get("title")?.takeIf { !it.isJsonNull }?.asString,
                            description = jsonObj.get("description")?.takeIf { !it.isJsonNull }?.asString,
                            url = jsonObj.get("url")?.takeIf { !it.isJsonNull }?.asString,
                            urlToImage = jsonObj.get("urlToImage")?.takeIf { !it.isJsonNull }?.asString,
                            publishedAt = jsonObj.get("publishedAt")?.takeIf { !it.isJsonNull }?.asString,
                            content = jsonObj.get("content")?.takeIf { !it.isJsonNull }?.asString
                        )
                    }
                    Log.d("NewsViewModel", "Parsed articles from network: ${articles.size}")
                    _articles.postValue(articles)
                }
            } else {
                _articles.postValue(emptyList())  // Post an empty list to LiveData
            }
        } catch (e: Exception) {
            _articles.postValue(emptyList())  // Post an empty list to LiveData in case of error
        }
    }

    private fun onError(e: Throwable) {
        Log.e("NewsViewModel", "Error fetching articles", e)
        _articles.postValue(emptyList())  // Post an empty list to LiveData in case of error
    }
}