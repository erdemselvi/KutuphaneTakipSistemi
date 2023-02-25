package com.erdemselvi.kutuphanetakipsistemi.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.erdemselvi.kutuphanetakipsistemi.R
import com.erdemselvi.kutuphanetakipsistemi.model.Kitaplar2
import com.erdemselvi.kutuphanetakipsistemi.model.Ogrenciler2
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class EnCokKitapAdapter(val context: Context, private val list1:ArrayList<String>, private val list2:ArrayList<Int>):RecyclerView.Adapter<EnCokKitapAdapter.EnCokKitapTasarim>() {

    inner class EnCokKitapTasarim(view: View):RecyclerView.ViewHolder(view){
        var database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val kitapAdi: TextView =view.findViewById(R.id.tvKitapAdi)
        val okunmaSayisi: TextView =view.findViewById(R.id.tvOkunmaSayisi)
        val kitapResmi:ImageView=view.findViewById(R.id.ivKitapResmi)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EnCokKitapTasarim {
        val tasarim=LayoutInflater.from(context).inflate(R.layout.en_cok_kitap_row,parent,false)
        return EnCokKitapTasarim(tasarim)
    }

    override fun onBindViewHolder(holder: EnCokKitapTasarim, @SuppressLint("RecyclerView") position: Int) {
        val prefences = context.getSharedPreferences("users", Context.MODE_PRIVATE)
        val okullId = prefences.getString("okulId","yok")
        val yetki=prefences.getString("yetki","yok")
        val spOkulId=prefences.getString("spOkulId","0")
        val refOgr=holder.database.getReference("kitaplar")
        refOgr.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n", "SuspiciousIndentation")
            override fun onDataChange(snapshot: DataSnapshot) {
//                val ogrenci=snapshot.getValue(Ogrenciler2::class.java)
                for (s in snapshot.children){
                    if (s!=null){
                        val kitap=s.getValue(Kitaplar2::class.java)
                        if (kitap!=null){
                            if (yetki=="öğrenci" || yetki == "öğretmen"){
                                if (kitap.okulId==okullId){
                                    if (kitap.isbn==list1[position])
                                    {
//                            ogrenci.Id=s.key
                                        holder.kitapAdi.text=(position+1).toString()+". "+kitap.kitapAdi
                                        Picasso.with(context)
                                            .load(kitap.resimUrl!!.toUri())

                                            .resize(128, 187)         //optional
                                            .centerCrop()                        //optional
                                            .into(holder.kitapResmi)
                                        holder.okunmaSayisi.text=list2[position].toString()
                                    }
                                }
                            }
                            else{
                                if (spOkulId=="0"){
                                    if (kitap.isbn==list1[position])
                                    {
//                            ogrenci.Id=s.key
                                        holder.kitapAdi.text=(position+1).toString()+". "+kitap.kitapAdi
                                        Picasso.with(context)
                                            .load(kitap.resimUrl!!.toUri())

                                            .resize(128, 187)         //optional
                                            .centerCrop()                        //optional
                                            .into(holder.kitapResmi)
                                        holder.okunmaSayisi.text=list2[position].toString()
                                    }
                                }
                                else{
                                    if (kitap.okulId==spOkulId){
                                        if (kitap.isbn==list1[position])
                                        {
//                            ogrenci.Id=s.key
                                            holder.kitapAdi.text=(position+1).toString()+". "+kitap.kitapAdi
                                            Picasso.with(context)
                                                .load(kitap.resimUrl!!.toUri())

                                                .resize(128, 187)         //optional
                                                .centerCrop()                        //optional
                                                .into(holder.kitapResmi)
                                            holder.okunmaSayisi.text=list2[position].toString()
                                        }
                                    }
                                }

                            }

                        }

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    override fun getItemCount(): Int {
        if (list1.size>10){
            return 10       //Sadece ilk 10 eleman listelensin
        }else{
            return list1.size
        }
    }
}