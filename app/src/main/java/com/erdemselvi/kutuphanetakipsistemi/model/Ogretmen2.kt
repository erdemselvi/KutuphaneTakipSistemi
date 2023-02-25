package com.erdemselvi.kutuphanetakipsistemi.model

import java.io.Serializable

data class Ogretmen2(var Id:String?="", var ad:String?="", var okulId:String?="",
                     var soyad:String?="", var telNo:String?="", var email:String?="",
                     var ogretmenId:String?="", val kayitTarihi:Long?=0L, val yetki:Int?=0) : Serializable {
}