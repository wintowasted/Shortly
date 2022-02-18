package com.example.shortly

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ShortenUrlApi {

    @GET("shorten")
    suspend fun getShortenUrl(@Query("url")url:String): Response<ShortenedUrl>
}