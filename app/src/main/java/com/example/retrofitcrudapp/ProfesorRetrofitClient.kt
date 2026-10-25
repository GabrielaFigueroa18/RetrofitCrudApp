
package com.example.retrofitcrudapp

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ProfesorRetrofitClient {

    private const val BASE_URL =
        "https://6adcdc6efa5e73d4ab2dc36c.mockapi.io/alumno"

    val instance: ProfesorApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProfesorApi::class.java)
    }
}
