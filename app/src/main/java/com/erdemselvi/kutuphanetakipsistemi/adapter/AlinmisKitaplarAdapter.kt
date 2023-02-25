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

class AlinmisKitaplarAdapter(private val context: Context, private val emanet:ArrayList<Emanets>)
    :RecyclerView.Adapter<AlinmisKitaplarAdapter.AlinmisKitaplarTasarim>(){

    inner class AlinmisKitaplarTasarim(view: View):RecyclerView.ViewHolder(view){
        var kitapAdi:TextView=view.findViewById(R.id.tvKitapAdi)
        var yazarAdi:TextView=view.findViewById(R.id.tvYazar)
        var alinanTarih:TextView=view.findViewById(R.id.tvAlinanTarih)
        var teslimTarihi:TextView=view.findViewById(R.id.tvTeslimTarihi)
        var sayfaSayisi:TextView=view.findViewById(R.id.tvSayfaSayisi)
        var database: FirebaseDatabase= FirebaseDatabase.getInstance()
        var clKitapAl:ConstraintLayout=view.findViewById(R.id.clKitapAl)
        var ivResim:ImageView=view.findViewById(R.id.ivResim)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlinmisKitaplarTasarim {
        val tasarim=LayoutInflater.from(context).inflate(R.layout.alinmis_kitaplar_row,parent,false)
        return AlinmisKitaplarTasarim(tasarim)
    }

    @SuppressLint("SetTextI18n", "SuspiciousIndentation")
    override fun onBindViewHolder(holder: AlinmisKitaplarTasarim, @SuppressLint("RecyclerView") position: Int) {

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
                                holder.sayfaSayisi.text=kitap.sayfaSayisi.toString()+" sayfa"

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

        holder.alinanTarih.text="Aldığın Tarih:"+getDate(emanet[position].emanetTarihi, "dd/MM/yyyy, hh:mm")

        holder.teslimTarihi.text="Teslim Tarihi:"+getDate(emanet[position].geriVermeTarihi, "dd/MM/yyyy, hh:mm")


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