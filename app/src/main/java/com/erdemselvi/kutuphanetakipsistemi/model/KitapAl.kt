package com.erdemselvi.kutuphanetakipsistemi.model

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class KitapAl(var emanetId:String?="",var ad:String?="", var no:Int?=1, var okulId:Int?=0,
                   var soyad:String?="", var telNo:String?="",var isbn:String?="",
                   var kitapAdi:String?="", var yazarAdi:String?="",var resimUrl:String?="") : Serializable {
}