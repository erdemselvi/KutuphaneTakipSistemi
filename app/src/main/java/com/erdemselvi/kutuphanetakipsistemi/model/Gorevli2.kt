package com.erdemselvi.kutuphanetakipsistemi.model

import java.io.Serializable

data class Gorevli2(var id:String?="",var gorevliId:String?="",var gorevTarihi:Long?=0L,var bitisTarihi:Long?=0,var okulId:String?="") :
    Serializable {
}