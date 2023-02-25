package com.erdemselvi.kutuphanetakipsistemi.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.erdemselvi.kutuphanetakipsistemi.R
import com.erdemselvi.kutuphanetakipsistemi.model.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class KitapAlAdapter(private val context: Context,private val emanet:ArrayList<Emanets>)
    :RecyclerView.Adapter<KitapAlAdapter.KitapAlTasarim>(){

    inner class KitapAlTasarim(view: View):RecyclerView.ViewHolder(view){
        var kitapAdi:TextView=view.findViewById(R.id.tvKitapAdi)
        var yazarAdi:TextView=view.findViewById(R.id.tvYazar)
        var ogrAdi:TextView=view.findViewById(R.id.tvOgrAdi)
        var alinanTarih:TextView=view.findViewById(R.id.tvAlinanTarih)
        var teslimSuresi:TextView=view.findViewById(R.id.tvTeslimSuresi)
        var gecenSure:TextView=view.findViewById(R.id.tvGecenSure)
        var btAl:Button=view.findViewById(R.id.btAl)
        var database: FirebaseDatabase= FirebaseDatabase.getInstance()
        var clKitapAl:ConstraintLayout=view.findViewById(R.id.clKitapAl)
        var ivResim:ImageView=view.findViewById(R.id.ivResim)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KitapAlTasarim {
        val tasarim=LayoutInflater.from(context).inflate(R.layout.kitap_al_row,parent,false)
        return KitapAlTasarim(tasarim)
    }

    @SuppressLint("SetTextI18n", "SuspiciousIndentation")
    override fun onBindViewHolder(holder: KitapAlTasarim, @SuppressLint("RecyclerView") position: Int) {

        val refKitap=holder.database.getReference("kitaplar").child(emanet[position].kitapId.toString())
        refKitap.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n", "SuspiciousIndentation")
            override fun onDataChange(snapshot: DataSnapshot) {
                val kitap=snapshot.getValue(Kitaplar2::class.java)

//                for (s in snapshot.children){
//                    if (s!=null){
//
//                        val kitap=s.getValue(Kitaplar2::class.java)
                        if (kitap!=null) {
//                            kitap.id = s.key
                            kitap.id=snapshot.key
                            if (kitap.id==emanet[position].kitapId){
                                holder.kitapAdi.text=kitap.kitapAdi
                                holder.yazarAdi.text=kitap.yazarAdi

                                Picasso.with(context)
                                    .load(kitap.resimUrl!!.toUri())

                                .resize(128, 187)         //optional
                                .centerCrop()                        //optional
                                    .into(holder.ivResim)
                            }
                        }
//                    }
//                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        val refOgr=holder.database.getReference("ogrenciler").child(emanet[position].ogrId.toString())
        refOgr.addValueEventListener(object :ValueEventListener{
            @SuppressLint("SetTextI18n", "SuspiciousIndentation")
            override fun onDataChange(snapshot: DataSnapshot) {
                val ogrenci=snapshot.getValue(Ogrenciler2::class.java)
//                for (s in snapshot.children){
//                    if (s!=null){
//                        val ogrenci=s.getValue(Ogrenciler2::class.java)
                        if (ogrenci!=null){
//                            ogrenci.Id=s.key
                            ogrenci.Id=snapshot.key
                            if (ogrenci.Id==emanet[position].ogrId){
                                holder.ogrAdi.text=ogrenci.ad+" "+ogrenci.soyad+" "+ogrenci.no
                            }
                        }
//                    }
//                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        holder.alinanTarih.text="Alındığı Tarih:"+getDate(emanet[position].emanetTarihi, "dd/MM/yyyy, hh:mm")

        holder.teslimSuresi.text="Kitabı ${emanet[position].teslimSuresi.toString()} günlüğüne aldı"

        val simdikiTarih = Date().time
        val gecenSure=simdikiTarih-emanet[position].emanetTarihi
        val saniyeyeCevir = (gecenSure) / 1000

        val day = String.format("%d", saniyeyeCevir / 86400)
        val hour = String.format("%d", (saniyeyeCevir % 86400) / 3600)
        val minute =
            String.format("%d", ((saniyeyeCevir % 86400) % 3600) / 60)
//        val second =
//            String.format("%02d", ((saniyeyeCevir % 86400) % 3600) % 60)
        val sure = "Geçen Süre: $day gün, $hour saat, $minute dakika"
        holder.gecenSure.text=sure
        if (day.toInt()>=emanet[position].teslimSuresi!!.toInt()){
            holder.clKitapAl.setBackgroundColor(ContextCompat.getColor(context,R.color.yellow))
            holder.gecenSure.setTextColor(ContextCompat.getColor(context,R.color.red))
        }
        else{
            holder.clKitapAl.setBackgroundColor(ContextCompat.getColor(context,R.color.teal_200))
            holder.gecenSure.setTextColor(ContextCompat.getColor(context,R.color.black))
        }
//        val formatt =SimpleDateFormat("dd/MM/yyyy, hh:mm")
//        val dateString = formatt.format(Date(emanet[position].emanetTarihi))
//        holder.alinanTarih.text=dateString
        holder.btAl.setOnClickListener {

            val myRef2 = holder.database.getReference("emanet").child(emanet[position].id.toString())
            val alertAl=AlertDialog.Builder(context)
                alertAl.setTitle("Kitap Teslim Alma İşlemi")
                    .setMessage("Kitabı Teslim Almak İstiyormusunuz?")
                    .setIcon(R.drawable.ic_baseline_menu_book_24)
                    .setPositiveButton("Al"){_,_->

                        //Bugünün tarihi Long olarak hesaplanıp diziye aktarılıyor
                        val update = mapOf("geriVermeTarihi" to Date().time)
                        myRef2.updateChildren(update)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Toast.makeText(context,"Kitap Başarı ile teslim alındı", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context,"Kitap Teslim Alınamadı!!", Toast.LENGTH_LONG).show()
                                }
                            }
                    }
                    .setNegativeButton("iptal"){_, _ -> }
                    .create().show()

        }
//        holder.kitapAdi.text=emanet[position].kitapAdi
//        holder.yazarAdi.text=emanet[position].yazarAdi
//        holder.ogrAdi.text=emanet[position].ad+" "+emanet[position].soyad+" "+emanet[position].no

    }

    override fun getItemCount(): Int {
        return emanet.size
    }
    @SuppressLint("SimpleDateFormat")
    fun getDate(milliSeconds: Long, dateFormat: String?): String? {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat)

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

}