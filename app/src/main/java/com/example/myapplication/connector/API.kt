package com.example.myapplication.connector

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface API {
    @POST("/api/a/send/token")
    fun sendToken(@Body reqJson: JsonObject): Call<Void>

    @POST("/api/a/update/agree")
    fun updateAgree(@Body reqJson: JsonObject): Call<Void>

}