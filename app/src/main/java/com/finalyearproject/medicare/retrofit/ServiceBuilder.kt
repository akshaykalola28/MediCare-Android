package com.finalyearproject.medicare.retrofit

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {

    const val URL = "https://medicare-888f7.appspot.com/"

    private val log = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    private val okHttpClient = OkHttpClient.Builder().addInterceptor(log).build()

    private val retrofit = Retrofit.Builder().baseUrl(URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> buildService(serviceType: Class<T>): T {
        return retrofit.create(serviceType)
    }

    //This both are different use of it. don't confuse. after calling getClient() you have to call create(interface) method

    //with Authorization
    fun getClient(apiKey: String): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val newRequest: Request = chain.request().newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("token", apiKey)
                    .build()
                chain.proceed(newRequest)
            }
            .addInterceptor(log)
            .build()

        return Retrofit.Builder().baseUrl(URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}