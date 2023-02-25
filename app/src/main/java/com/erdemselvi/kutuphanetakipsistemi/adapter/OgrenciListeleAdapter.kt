package com.erdemselvi.kutuphanetakipsistemi.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.erdemselvi.kutuphanetakipsistemi.R
import com.erdemselvi.kutuphanetakipsistemi.model.Kitaplar2
import com.erdemselvi.kutuphanetakipsistemi.model.Ogrenciler2
import com.erdemselvi.kutuphanetakipsistemi.model.Okul
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList

class OgrenciListeleAdapter(val mContext: Context, val ogrenciler:ArrayList<Ogrenciler2>,
                            val kontrol:Int, val emanetTarihi:ArrayList<Long>,val emanetKitapId:ArrayList<String>,val emanetSuresi:ArrayList<Int>)
    :RecyclerView.Adapter<OgrenciListeleAdapter.OgrenciListeleTasarim>() {

    inner class OgrenciListeleTasarim(tasarim:View):RecyclerView.ViewHolder(tasarim){
        val ogrAdSoyad:TextView=tasarim.findViewById(R.id.tvOgrenciAdi)
        val telNo:TextView=tasarim.findViewById(R.id.tvTelNo)
        val okulNo:TextView=tasarim.findViewById(R.id.tvOkulNo)
        val okulAdi:TextView=tasarim.findViewById(R.id.tvOkulAdi)
        val bilgi:TextView=tasarim.findViewById(R.id.tvBilgi)
        var database: FirebaseDatabase= FirebaseDatabase.getInstance()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OgrenciListeleTasarim {
       val tasarim=LayoutInflater.from(mContext).inflate(R.layout.ogrenci_row,parent,false)
        return OgrenciListeleTasarim(tasarim)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OgrenciListeleTasarim, @SuppressLint("RecyclerView") position: Int) {


        holder.ogrAdSoyad.text=ogrenciler[position].ad+" "+ogrenciler[position].soyad
        holder.telNo.text=ogrenciler[position].telNo
        holder.okulNo.text = ogrenciler[position].no.toString()

        val ref=FirebaseDatabase.getInstance().getReference("okullar").child(ogrenciler[position].okulId.toString())
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
//                for (ok in snapshot.children){
//                    Log.e("okul1",snapshot.children.toString())
//                    Log.e("okul2",ok.toString())
//                    if (ok!=null) {
                        val o = snapshot.getValue(Okul::class.java)
//                    val okul=ok.child("okulAdi").getValue(String::class.java).toString()
                        holder.okulAdi.text = o!!.okulAdi +" - "+o.sehir

//                    }
//                }


            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        if (kontrol==1){
            holder.bilgi.visibility=View.VISIBLE
            var bilgiYazisi=""
            val refKitap=holder.database.getReference("kitaplar").child(emanetKitapId[position])
            refKitap.addValueEventListener(object : ValueEventListener {
                @SuppressLint("SetTextI18n", "SuspiciousIndentation")
                override fun onDataChange(snapshot: DataSnapshot) {
                    val kitap=snapshot.getValue(Kitaplar2::class.java)

                    if (kitap!=null) {
                        bilgiYazisi+=kitap.kitapAdi+" kitabı\n"
                        Log.e("kitapAdıAdaptor",kitap.kitapAdi.toString())
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
            Log.e("emanetTarihiAdaptor" ,emanetTarihi[position].toString())

            val simdikiTarih = Date().time
            val gecenSure=simdikiTarih-emanetTarihi[position]
            val saniyeyeCevir = (gecenSure) / 1000
            val day = String.format("%d", saniyeyeCevir / 86400)
            val hour = String.format("%d", (saniyeyeCevir % 86400) / 3600)
            val minute =
                String.format("%d", ((saniyeyeCevir % 86400) % 3600) / 60)
//        val second =
//            String.format("%02d", ((saniyeyeCevir % 86400) % 3600) % 60)
            val sure = "Geçen Süre: $day gün, $hour saat, $minute dakika"
            bilgiYazisi+=emanetSuresi[position].toString()+" günlüğüne alındı.\n"
            bilgiYazisi+=sure
            holder.bilgi.text=bilgiYazisi
        }

    }

    override fun getItemCount(): Int {
        return ogrenciler.size
    }
}