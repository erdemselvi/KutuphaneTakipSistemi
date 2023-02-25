package com.erdemselvi.kutuphanetakipsistemi.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.erdemselvi.kutuphanetakipsistemi.R
import com.erdemselvi.kutuphanetakipsistemi.model.Gorevli2
import com.erdemselvi.kutuphanetakipsistemi.model.Ogrenciler2
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class GorevliAdapter(val context:Context, val gorevli:ArrayList<Gorevli2>):RecyclerView.Adapter<GorevliAdapter.GorevliTasarim>() {

    inner class GorevliTasarim(view: View):RecyclerView.ViewHolder(view){
        val gorevliAdi:TextView=view.findViewById(R.id.tvGorevliAdi)
        val gorevTarihi:TextView=view.findViewById(R.id.tvGorevTarihi)
        val sil:Button=view.findViewById(R.id.btSil)
        val database=FirebaseDatabase.getInstance()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GorevliTasarim {
        val tasarim=LayoutInflater.from(context).inflate(R.layout.gorevli_row,parent,false)
        return GorevliTasarim(tasarim)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: GorevliTasarim, @SuppressLint("RecyclerView") position: Int) {
        val refOgr=holder.database.getReference("ogrenciler").child(gorevli[position].gorevliId.toString())
        refOgr.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n", "SuspiciousIndentation")
            override fun onDataChange(snapshot: DataSnapshot) {
                val ogrenci=snapshot.getValue(Ogrenciler2::class.java)
//                for (s in snapshot.children){
//                    if (s!=null){
//                        val ogrenci=s.getValue(Ogrenciler2::class.java)
                if (ogrenci!=null){
//                            ogrenci.Id=s.key
                    ogrenci.Id=snapshot.key
                    if (ogrenci.Id==gorevli[position].gorevliId){
                        holder.gorevliAdi.text=ogrenci.ad+" "+ogrenci.soyad+" "+ogrenci.no

                    }
                }
//                    }
//                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        holder.gorevTarihi.text="Görev Tarih:"+getDate(gorevli[position].gorevTarihi!!, "dd/MM/yyyy, hh:mm")

        holder.sil.setOnClickListener {
            val myRef2 = holder.database.getReference("gorevliler").child(gorevli[position].id.toString())
            val alertAl= AlertDialog.Builder(context)
            alertAl.setTitle("Görevli Silme İşlemi")
                .setMessage("Görevliyi Silmek İstiyormusunuz?")
                .setIcon(R.drawable.gorevli)
                .setPositiveButton("Sil"){_,_->

                    //Bugünün tarihi Long olarak hesaplanıp diziye aktarılıyor
                    val update = mapOf("bitisTarihi" to Date().time)
                    myRef2.updateChildren(update)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(context,"Görevli Başarı ile Silindi", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context,"Görevli Silinemedi!!", Toast.LENGTH_LONG).show()
                            }
                        }
                }
                .setNegativeButton("iptal"){_, _ -> }
                .create().show()

        }
    }

    override fun getItemCount(): Int {
        return gorevli.size
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