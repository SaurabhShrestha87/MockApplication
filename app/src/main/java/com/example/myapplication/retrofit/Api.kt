package com.example.myapplication.retrofit

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface Api {

    @POST("login")
     fun postData(@Header("Content-Type") contentType: String?, @Body body: EmulatorData) : Call<EmulatorData?>?
}