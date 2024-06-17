package com.harshittest.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harshittest.apiclient.ApiClient
import com.harshittest.apiclient.ServiceApi
import com.harshittest.repository.NewsListRepository
import com.harshittest.room.AppDatabase
import com.harshittest.model.Article
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {

    private lateinit var repository: NewsListRepository
    private val _articles = MutableLiveData<List<Article>>()
    val articles: LiveData<List<Article>> get() = _articles
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

    private fun onSuccess(articles: List<Article>) {
        try {
            // Ensure the JSON is valid
            if (articles.isNotEmpty()) {
                _articles.postValue(articles)
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