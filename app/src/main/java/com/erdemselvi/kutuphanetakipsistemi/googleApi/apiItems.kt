package com.erdemselvi.kutuphanetakipsistemi.googleApi

import com.google.gson.annotations.SerializedName

data class apiItems(
    @SerializedName("items")
    val items: List<Item>,
    @SerializedName("kind")
    val kind: String,
    @SerializedName("totalItems")
    val totalItems: Int
)