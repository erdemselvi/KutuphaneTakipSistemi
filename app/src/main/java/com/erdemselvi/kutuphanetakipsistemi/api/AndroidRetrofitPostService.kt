package com.erdemselvi.kutuphanetakipsistemi.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PostService {
    @GET("/isbn/{isbn}")
    fun listPost(@Path("isbn") isbn: String): Call<List<Post>>
}