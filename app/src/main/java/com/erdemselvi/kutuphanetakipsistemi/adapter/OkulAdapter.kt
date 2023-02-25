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
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import kotlin.collections.ArrayList

class OkulAdapter(val context: Context, val okul:ArrayList<Okul2>) : RecyclerView.Adapter<OkulAdapter.OkulTasarim>(){

    inner class OkulTasarim(view: View):RecyclerView.ViewHolder(view){
        val okulAdi:TextView=view.findViewById(R.id.tvOkulAdi)
        val sehir:TextView=view.findViewById(R.id.tvOkulSehir)
        val sil: Button =view.findViewById(R.id.btSil)
        val database= FirebaseDatabase.getInstance()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OkulTasarim {
        val tasarim=LayoutInflater.from(context).inflate(R.layout.okul_row,parent,false)
        return OkulTasarim(tasarim)
    }

    override fun onBindViewHolder(holder: OkulTasarim, position: Int) {
        holder.okulAdi.text=okul[position].okulAdi
        holder.sehir.text=okul[position].sehir

        holder.sil.setOnClickListener {
            val myRef2 = holder.database.getReference("okullar").child(okul[position].id.toString())
            val alertAl= AlertDialog.Builder(context)
            alertAl.setTitle("Okul Silme İşlemi")
                .setMessage("Okulu Silmek İstiyormusunuz?")
                .setIcon(R.drawable.school)
                .setPositiveButton("Sil"){_,_->

                    myRef2.removeValue()
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(context,"Okul Başarı ile Silindi", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context,"Okul Silinemedi!!", Toast.LENGTH_LONG).show()
                            }
                        }
                }
                .setNegativeButton("iptal"){_, _ -> }
                .create().show()

        }
    }

    override fun getItemCount(): Int {
        return okul.size
    }
}