package com.erdemselvi.kutuphanetakipsistemi.googleApi

import com.google.gson.annotations.SerializedName

data class VolumeInfo(
    @SerializedName("allowAnonLogging")
    val allowAnonLogging: Boolean,
    @SerializedName("authors")
    val authors: List<String>,
    @SerializedName("averageRating")
    val averageRating: Float,
    @SerializedName("canonicalVolumeLink")
    val canonicalVolumeLink: String,
    @SerializedName("categories")
    val categories: List<String>,
    @SerializedName("contentVersion")
    val contentVersion: String,
    @SerializedName("description")
    val description: String?="",
    @SerializedName("imageLinks")
    val imageLinks: ImageLinks,
    @SerializedName("industryIdentifiers")
    val industryIdentifiers: List<IndustryIdentifier>,
    @SerializedName("infoLink")
    val infoLink: String,
    @SerializedName("language")
    val language: String,
    @SerializedName("maturityRating")
    val maturityRating: String,
    @SerializedName("pageCount")
    val pageCount: Int,
    @SerializedName("panelizationSummary")
    val panelizationSummary: PanelizationSummary,
    @SerializedName("previewLink")
    val previewLink: String,
    @SerializedName("printType")
    val printType: String,
    @SerializedName("publishedDate")
    val publishedDate: String,
    @SerializedName("publisher")
    val publisher: String,
    @SerializedName("ratingsCount")
    val ratingsCount: Int?=0,
    @SerializedName("readingModes")
    val readingModes: ReadingModes,
    @SerializedName("title")
    val title: String
)