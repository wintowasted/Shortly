package com.example.shortly

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    val api: ShortenUrlApi by lazy {

        Retrofit.Builder()
            .baseUrl("https://api.shrtco.de/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ShortenUrlApi::class.java)
    }
}