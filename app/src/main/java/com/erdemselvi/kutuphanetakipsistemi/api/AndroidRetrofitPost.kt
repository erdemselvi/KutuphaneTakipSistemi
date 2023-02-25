package com.erdemselvi.kutuphanetakipsistemi.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Post(
    @SerializedName("authors")
    @Expose
    val authors: List<Author>,
    @SerializedName("classifications")
    @Expose
    val classifications: Classifications,
    @SerializedName("contributions")
    @Expose
    val contributions: List<String>,
    @SerializedName("covers")
    @Expose
    val covers: List<Int>,
    @SerializedName("created")
    @Expose
    val created: Created,
    @SerializedName("first_sentence")
    @Expose
    val first_sentence: FirstSentence,
    @SerializedName("identifiers")
    @Expose
    val identifiers: Identifiers,
    @SerializedName("isbn_10")
    @Expose
    val isbn_10: List<String>,
    @SerializedName("isbn_13")
    @Expose
    val isbn_13: List<String>,
    @SerializedName("key")
    @Expose
    val key: String,
    @SerializedName("languages")
    @Expose
    val languages: List<Language>,
    @SerializedName("last_modified")
    @Expose
    val last_modified: LastModified,
    @SerializedName("latest_revision")
    @Expose
    val latest_revision: Int,
    @SerializedName("local_id")
    @Expose
    val local_id: List<String>,
    @SerializedName("number_of_pages")
    @Expose
    val number_of_pages: Int,
    @SerializedName("ocaid")
    @Expose
    val ocaid: String,
    @SerializedName("publish_date")
    @Expose
    val publish_date: String,
    @SerializedName("publishers")
    @Expose
    val publishers: List<String>,
    @SerializedName("revision")
    @Expose
    val revision: Int,
    @SerializedName("source_records")
    @Expose
    val source_records: List<String>,
    @SerializedName("title")
    @Expose
    val title: String,
    @SerializedName("type")
    @Expose
    val type: Type,
    @SerializedName("works")
    @Expose
    val works: List<Work>
): Serializable

data class Author(
    @SerializedName("key")
    @Expose
    val key: String
): Serializable

class Classifications: Serializable

data class Created(
    @SerializedName("type")
    @Expose
    val type: String,
    @SerializedName("value")
    @Expose
    val value: String
): Serializable

data class FirstSentence(
    @SerializedName("type")
    @Expose
    val type: String,
    @SerializedName("value")
    @Expose
    val value: String
): Serializable

data class Identifiers(
    @SerializedName("goodreads")
    @Expose
    val goodreads: List<String>,
    @SerializedName("librarything")
    @Expose
    val librarything: List<String>
): Serializable

data class Language(
    @SerializedName("key")
    @Expose
    val key: String
): Serializable

data class LastModified(
    @SerializedName("type")
    @Expose
    val type: String,
    @SerializedName("value")
    @Expose
    val value: String
): Serializable

data class Type(
    @SerializedName("key")
    @Expose
    val key: String
): Serializable

data class Work(
    @SerializedName("key")
    @Expose
    val key: String
): Serializable

