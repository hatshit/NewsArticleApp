package com.harshittest.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.harshittest.apiclient.ServiceApi
import com.harshittest.room.DaoArticle
import com.harshittest.room.EntityArticle
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
        onSuccess: (history: String) -> Unit
    ) {
        // First, fetch data from Room database
        CoroutineScope(Dispatchers.IO).launch {
            val cachedArticles = articleDao.getAllArticles()
            if (cachedArticles.isNotEmpty()) {
                val cachedJson = Gson().toJson(cachedArticles)
                withContext(Dispatchers.Main) {
                    onSuccess(cachedJson)
                }
            }
        }

        // Then fetch the latest articles from the network
        if (AppUtility.isInternetConnected(context)) {
            AppUtility.progressBarShow(context)
            disposable.add(
                serviceApi.getWithJsonObject(
                    "top-headlines?country=us&category=business&apiKey=2369f4864dc1475b9fc474ef2e24e3fd"
                ).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<Response<JsonObject>>() {
                        override fun onSuccess(response: Response<JsonObject>) {
                            if (response.body() != null) {
                                val jsonResponse = response.body().toString()
                                // Use a CoroutineScope to handle database operations
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        val convertedObject: JsonObject = Gson().fromJson(jsonResponse, JsonObject::class.java)
                                        val articlesJson = convertedObject.get("articles")
                                        val articles: List<EntityArticle> = when {
                                            articlesJson.isJsonArray -> {
                                                val listType = object : TypeToken<List<EntityArticle>>() {}.type
                                                Gson().fromJson(articlesJson, listType)
                                            }
                                            articlesJson.isJsonObject -> {
                                                val singleArticle: EntityArticle = Gson().fromJson(articlesJson, EntityArticle::class.java)
                                                listOf(singleArticle)
                                            }
                                            else -> emptyList()
                                        }

                                        // Save articles to Room database
                                        articleDao.deleteAllArticles()
                                        articleDao.insertArticles(articles)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                                onSuccess(jsonResponse)
                                AppUtility.progressBarDissMiss()
                            } else {
                                handleError(response)
                            }
                        }

                        override fun onError(e: Throwable) {
                            handleError(null)
                        }

                        private fun handleError(response: Response<JsonObject>?) {
                            onSuccess("false")
                            AppUtility.progressBarDissMiss()
                            if (response?.code() != 401) {
                                Toast.makeText(
                                    context,
                                    "Something went wrong, please try again.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    })
            )
        } else {
            onSuccess("false")
            // If no network connection, show a message
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(
                    context,
                    "No network connection, please try again later.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}