package com.erdemselvi.kutuphanetakipsistemi.model

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class Ogrenciler2(var Id:String?="", var ad:String?="", var no:Int?=1, var okulId:String?="",
                       var soyad:String?="", var telNo:String?="", var email:String?="",
                       var ogrId:String?="", val kayitTarihi:Long?=0L) : Serializable
