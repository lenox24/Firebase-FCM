package com.example.myapplication.connector

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object Connector {
    var retrofit: Retrofit
    private var api: API
    private const val url = "https://fcm-test-server.herokuapp.com/"

    init {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            //.addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
            .client(client)
            .build()

        api = retrofit.create(API::class.java)
    }

    fun createApi(): API = retrofit.create(
        API::class.java
    )
}