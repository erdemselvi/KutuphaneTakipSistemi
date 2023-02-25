package com.erdemselvi.kutuphanetakipsistemi.model

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class Kitaplar2(var id:String?="", var isbn:String?="", var kitapAdi:String?="", var yazarAdi:String?="",
                     var kisaOzet:String?="", var kategori:String?="", var sayfaSayisi:Int?=0,
                     var yayinTarihi:String?="", var dil:String?="", var derece:Int?=0,
                     var resimUrl:String?="",var okulId:String?=""): Serializable {

}