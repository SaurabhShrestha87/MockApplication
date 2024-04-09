package com.example.myapplication.retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {

    private var retrofitBuilder: Retrofit? = null

    private fun retrofit(url: String): Retrofit {
        return retrofitBuilder ?: run {
            retrofitBuilder = Retrofit.Builder()
                .baseUrl(url)
                .client(OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            retrofitBuilder!!
        }
    }


    fun getApi(url: String): Api {
        return retrofit(url).create(Api::class.java)
    }

}
