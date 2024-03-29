package com.erdemselvi.kutuphanetakipsistemi.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private var retrofit: Retrofit? = null

    fun getClient(): Retrofit {
        if (retrofit == null)
            retrofit =
                Retrofit.Builder().baseUrl(Constant.baseUrl).addConverterFactory(
                    GsonConverterFactory.create()).build()

        return retrofit as Retrofit
    }
}