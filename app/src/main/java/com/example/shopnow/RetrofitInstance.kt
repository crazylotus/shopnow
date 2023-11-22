package com.example.shopnow

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val api : ApiSerive by lazy {
        Retrofit.Builder()
            .baseUrl("https://dummyjson.com/products/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiSerive::class.java)
    }
}