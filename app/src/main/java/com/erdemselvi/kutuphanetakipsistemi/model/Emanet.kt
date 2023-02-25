package com.erdemselvi.kutuphanetakipsistemi.model

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class Emanet(var isbn:String?="", var ogrId:String?="", var emanetTarihi: Long =0, var geriVermeTarihi:Long =0):Serializable