package com.erdemselvi.kutuphanetakipsistemi.googleApi

import com.erdemselvi.kutuphanetakipsistemi.api.PostService
import com.google.android.gms.common.api.internal.ApiKey
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PostService : PostService {
    @GET("volumes")
    fun listPost(@Query("q") isbn: String,@Query("key") apiKey: String): Call<apiItems>
}