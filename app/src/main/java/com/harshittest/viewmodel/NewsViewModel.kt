package com.harshittest.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.harshittest.apiclient.ApiClient
import com.harshittest.apiclient.ServiceApi
import com.harshittest.repository.NewsListRepository
import com.harshittest.room.AppDatabase
import com.harshittest.room.EntityArticle
import kotlinx.coroutines.launch

class NewsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NewsListRepository
    private val _articles = MutableLiveData<List<EntityArticle>>()
    val articles: LiveData<List<EntityArticle>> get() = _articles

    init {
        val articleDao = AppDatabase.getDatabase(application).articleDao()
        val serviceApi: ServiceApi = ApiClient.getServices()
        repository = NewsListRepository(serviceApi, articleDao, application)
    }

    fun getNewsList() {
        viewModelScope.launch {
            repository.getNewsListFromRepo(onSuccess = ::onSuccess)
        }
    }

    private fun onSuccess(json: String) {
        Log.d("ViewModel", "Received JSON: $json")
        val listType = object : TypeToken<List<EntityArticle>>() {}.type
        val articles: List<EntityArticle> = Gson().fromJson(json, listType)
        Log.d("ViewModel", "Parsed articles: ${articles.size}")
        _articles.postValue(articles)
    }
}