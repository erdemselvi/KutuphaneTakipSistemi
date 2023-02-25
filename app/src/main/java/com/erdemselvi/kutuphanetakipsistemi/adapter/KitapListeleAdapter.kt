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
import com.squareup.picasso.Picasso

class KitapListeleAdapter(private val mContext: Context,private val kitaplar: ArrayList<Kitaplar2>)
    :RecyclerView.Adapter<KitapListeleAdapter.KitapListeleTasarim>(){

inner class KitapListeleTasarim(tasarim:View):RecyclerView.ViewHolder(tasarim){
    var kitapAdi:TextView=tasarim.findViewById(R.id.tvKitapAdi)
    var yazarAdi:TextView=tasarim.findViewById(R.id.tvYazar)
    var ozet:TextView=tasarim.findViewById(R.id.tvOzet)
    var resim:ImageView=tasarim.findViewById(R.id.ivResim)
}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KitapListeleTasarim {
        val tasarim=LayoutInflater.from(mContext).inflate(R.layout.kitap_row,parent,false)
        return KitapListeleTasarim(tasarim)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: KitapListeleTasarim, position: Int) {
        holder.kitapAdi.text=(position+1).toString()+"-"+kitaplar[position].kitapAdi.toString()
        holder.yazarAdi.text=kitaplar[position].yazarAdi.toString()
        holder.ozet.text=kitaplar[position].kisaOzet.toString()
        if (kitaplar[position].resimUrl!=null){
            Picasso.with(mContext)
                .load(kitaplar[position].resimUrl!!.toUri())

                .resize(128, 187)         //optional
                .centerCrop()                        //optional
                .into(holder.resim)
        }

    }

    override fun getItemCount(): Int {
        return kitaplar.size
    }

}