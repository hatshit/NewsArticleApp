package com.harshittest.repository

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.harshittest.apiclient.ServiceApi
import com.harshittest.model.Article
import com.harshittest.room.DaoArticle
import com.harshittest.utility.AppUtility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.util.*

class NewsListRepository(
    private val serviceApi: ServiceApi,
    private val articleDao: DaoArticle,
    private val context: Context
) {

    fun getNewsListFromRepo(
        disposable: CompositeDisposable = CompositeDisposable(),
        onSuccess: (articles: List<Article>) -> Unit,
        onError: (e: Throwable) -> Unit
    ) {
        // First, fetch data from Room database
        CoroutineScope(Dispatchers.IO).launch {
            val cachedArticles = articleDao.getAllArticles()
            Log.d("NewsListRepository", "Fetched articles from DB: ${cachedArticles}")
            onSuccess(cachedArticles)
            // If no articles in cache, fetch from network
            fetchArticlesFromNetwork(disposable, onSuccess, onError)
        }
    }

    private fun fetchArticlesFromNetwork(
        disposable: CompositeDisposable,
        onSuccess: (articles: List<Article>) -> Unit,
        onError: (e: Throwable) -> Unit
    ) {

        if (AppUtility.isInternetConnected(context)) {
            Log.d("NewsListRepository", "Showing progress dialog")
            disposable.add(
                serviceApi.getWithJsonObject(
                    "top-headlines?country=us&category=business&apiKey=2369f4864dc1475b9fc474ef2e24e3fd"
                ).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { Log.d("NewsListRepository", "Network request initiated") }
                    .subscribeWith(object : DisposableSingleObserver<Response<JsonObject>>() {
                        override fun onSuccess(response: Response<JsonObject>) {
                            if (response.body() != null) {
                                val jsonResponse = response.body().toString()
                                Log.d("NewsListRepository", "Network response body: $jsonResponse")
                                val convertedObject: JsonObject = Gson().fromJson(jsonResponse, JsonObject::class.java)
                                val articlesJsonArray = convertedObject.getAsJsonArray("articles")
                                val articles = ArrayList<Article>()
                                for (jsonElement in articlesJsonArray) {
                                    val jsonObj = jsonElement.asJsonObject
                                    var author = jsonObj.get("author")?.takeIf { !it.isJsonNull }?.asString
                                    val title = jsonObj.get("title")?.takeIf { !it.isJsonNull }?.asString
                                    val description = jsonObj.get("description")?.takeIf { !it.isJsonNull }?.asString
                                    val url = jsonObj.get("url")?.takeIf { !it.isJsonNull }?.asString
                                    val urlToImage = jsonObj.get("urlToImage")?.takeIf { !it.isJsonNull }?.asString
                                    val publishedAt = jsonObj.get("publishedAt")?.takeIf { !it.isJsonNull }?.asString
                                    val content = jsonObj.get("content")?.takeIf { !it.isJsonNull }?.asString

                                    if (author != null && title != null && description != null && url != null &&
                                        urlToImage != null && publishedAt != null && content != null) {
                                        val article =    Article(
                                            author = author,
                                            title = title,
                                            description = description,
                                            url = url,
                                            urlToImage = urlToImage,
                                            publishedAt = publishedAt,
                                            content = content
                                        )
                                        articles.add(article)
                                    }
                                }
                                onSuccess(articles)
                                // Use a CoroutineScope to handle database operations
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        Log.d("NewsListRepository", "Parsed articles from network: ${articles.size}")
                                        // Save articles to Room database
                                        articleDao.deleteAllArticles()
                                        articleDao.insertArticles(articles)
                                    } catch (e: Exception) {
                                        Log.e("NewsListRepository", "Error saving articles to DB", e)
                                        e.printStackTrace()
                                    }
                                }

                                AppUtility.progressBarDissMiss()
                            } else {
                                Log.d("NewsListRepository", "Response body is null")
                                handleError(response, onError)
                            }
                        }



                        override fun onError(e: Throwable) {
                            Log.e("NewsListRepository", "Network request failed", e)
                            handleError(null, onError)
                        }

                        private fun handleError(response: Response<JsonObject>?, onError: (e: Throwable) -> Unit) {
                            onError(Throwable("Error fetching data from network"))
                            if (response?.code() != 401) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    val cachedArticles = articleDao.getAllArticles()
                                    Log.d("NewsListRepository", "Fetched articles from DB: ${cachedArticles}")
                                    if (cachedArticles.isNotEmpty()) {
                                        val cachedJson = Gson().toJson(cachedArticles)
                                        withContext(Dispatchers.Main) {
                                            onSuccess(cachedArticles)
                                        }
                                    }
                                }
                                AppUtility.progressBarDissMiss()
                            }
                        }
                    })
            )
        } else {
            Log.d("NewsListRepository", "No internet connection or invalid context type")
            onError(Throwable("No internet connection"))
            // First, fetch data from Room database
            CoroutineScope(Dispatchers.IO).launch {
                val cachedArticles = articleDao.getAllArticles()
                Log.d("NewsListRepository", "Fetched articles from DB: ${cachedArticles}")
                if (cachedArticles.isNotEmpty()) {
                    val cachedJson = Gson().toJson(cachedArticles)
                    withContext(Dispatchers.Main) {
                        onSuccess(cachedArticles)
                    }
                }
            }
            AppUtility.progressBarDissMiss()
        }
    }
}
