package com.harshittest.apiclient

import com.google.gson.JsonObject
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Url

interface ServiceApi {

    @GET
    fun getWithJsonObject(@Url url:String):Single<Response<JsonObject>>

}
