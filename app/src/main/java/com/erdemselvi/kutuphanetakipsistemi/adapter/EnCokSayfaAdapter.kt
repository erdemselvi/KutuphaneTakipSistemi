package com.erdemselvi.kutuphanetakipsistemi.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.erdemselvi.kutuphanetakipsistemi.R
import com.erdemselvi.kutuphanetakipsistemi.model.Ogrenciler2
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EnCokSayfaAdapter(private val context: Context, private val list1:ArrayList<String>, private val list2:ArrayList<Int>):
    RecyclerView.Adapter<EnCokSayfaAdapter.EnCokSayfaTasarim>() {
    inner class EnCokSayfaTasarim(view: View) : RecyclerView.ViewHolder(view) {
        val ogrAdi: TextView = view.findViewById(R.id.tvOgrAdi)
        val sayfaSayisi: TextView = view.findViewById(R.id.tvSayfaSayisi)
        var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EnCokSayfaTasarim {
        val tasarim = LayoutInflater.from(context).inflate(R.layout.en_cok_sayfa_row, parent, false)
        return EnCokSayfaTasarim(tasarim)
    }

    override fun onBindViewHolder(holder: EnCokSayfaTasarim, @SuppressLint("RecyclerView") position: Int) {
        val prefences = context.getSharedPreferences("users", Context.MODE_PRIVATE)
        val okullId = prefences.getString("okulId","yok")
        val yetki=prefences.getString("yetki","yok")
        val spOkulId=prefences.getString("spOkulId","0")
//        holder.ogrAdi.text=liste[position].ogrId
        val refOgr = holder.database.getReference("ogrenciler").child(list1[position])
        refOgr.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n", "SuspiciousIndentation")
            override fun onDataChange(snapshot: DataSnapshot) {
                val ogrenci = snapshot.getValue(Ogrenciler2::class.java)
//                for (s in snapshot.children){
//                    if (s!=null){
//                        val ogrenci=s.getValue(Ogrenciler2::class.java)
                if (ogrenci!=null) {
                    if (yetki=="öğrenci" || yetki == "öğretmen"){
                        if (ogrenci.okulId==okullId){
                            holder.ogrAdi.text=(position+1).toString()+". "+ogrenci.ad+" "+ogrenci.soyad+" - "+ogrenci.no.toString()
                            holder.sayfaSayisi.text = list2[position].toString()
                        }
                    }else{
                        if (spOkulId=="0" && list2.size!=0){
                            holder.ogrAdi.text=(position+1).toString()+". "+ogrenci.ad+" "+ogrenci.soyad+" - "+ogrenci.no.toString()
                            holder.sayfaSayisi.text = list2[position].toString()
                        }
                        else{
                            if (ogrenci.okulId==spOkulId){
                                holder.ogrAdi.text=(position+1).toString()+". "+ogrenci.ad+" "+ogrenci.soyad+" - "+ogrenci.no.toString()
                                holder.sayfaSayisi.text = list2[position].toString()
                            }
                        }
                    }
//                            ogrenci.Id=s.key
                }
//                    }
//                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

//        holder.ogrAdi.text=list1[position]

    }

    override fun getItemCount(): Int {
        if (list1.size>10){
            return 10       //Sadece ilk 10 eleman listelensin
        }else{
            return list1.size
        }
    }
}