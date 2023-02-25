package com.erdemselvi.kutuphanetakipsistemi.adapter

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
import com.erdemselvi.kutuphanetakipsistemi.model.Okul
import com.erdemselvi.kutuphanetakipsistemi.model.Okul2
import com.erdemselvi.kutuphanetakipsistemi.model.Yonetici2
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import kotlin.collections.ArrayList

class YoneticiAdapter(val context: Context, val yonetici:ArrayList<Yonetici2>) : RecyclerView.Adapter<YoneticiAdapter.YoneticiTasarim>(){

    inner class YoneticiTasarim(view: View):RecyclerView.ViewHolder(view){
        val email:TextView=view.findViewById(R.id.tvEmail)
        val adSoyad:TextView=view.findViewById(R.id.tvAdsoyad)
        val sil: Button =view.findViewById(R.id.btSil)
        val database= FirebaseDatabase.getInstance()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YoneticiTasarim {
        val tasarim=LayoutInflater.from(context).inflate(R.layout.yonetici_row,parent,false)
        return YoneticiTasarim(tasarim)
    }

    override fun onBindViewHolder(holder: YoneticiTasarim, position: Int) {
        holder.email.text=yonetici[position].email
        holder.adSoyad.text=yonetici[position].adSoyad

        holder.sil.setOnClickListener {
            val myRef2 = holder.database.getReference("yoneticiler").child(yonetici[position].id.toString())
            val alertAl= AlertDialog.Builder(context)
            alertAl.setTitle("Yönetici Silme İşlemi")
                .setMessage("Yöneticiyi Silmek İstiyormusunuz?")
                .setIcon(R.drawable.school)
                .setPositiveButton("Sil"){_,_->

                    myRef2.removeValue()
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(context,"Yönetici Başarı ile Silindi", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context,"Yönetici Silinemedi!!", Toast.LENGTH_LONG).show()
                            }
                        }
                }
                .setNegativeButton("iptal"){_, _ -> }
                .create().show()

        }
    }

    override fun getItemCount(): Int {
        return yonetici.size
    }
}