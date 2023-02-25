package com.erdemselvi.kutuphanetakipsistemi.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.erdemselvi.kutuphanetakipsistemi.KitapAlActivity
import com.erdemselvi.kutuphanetakipsistemi.R
import com.erdemselvi.kutuphanetakipsistemi.model.Ogrenciler2
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OgrenciBulAdapter (val activity: Activity,val mContext: Context, val ogrenciler:ArrayList<Ogrenciler2>):
    RecyclerView.Adapter<OgrenciBulAdapter.OgrenciBulTasarim>() {
//    lateinit var activity:Activity
    inner class OgrenciBulTasarim(tasarim: View): RecyclerView.ViewHolder(tasarim){
        val ogrAdSoyad: TextView =tasarim.findViewById(R.id.tvOgrenciAdi)
        val telNo: TextView =tasarim.findViewById(R.id.tvTelNo)
        val OkulNoVeAdi: TextView =tasarim.findViewById(R.id.tvOkulAdi)
        val btSec:Button=tasarim.findViewById(R.id.btSec)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OgrenciBulTasarim {
        val tasarim= LayoutInflater.from(mContext).inflate(R.layout.ogrenci_sec_row,parent,false)

//        this.activity=activity      //setResult aktif olsun diye
        return OgrenciBulTasarim(tasarim)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OgrenciBulTasarim, @SuppressLint("RecyclerView") position: Int) {
        holder.ogrAdSoyad.text=ogrenciler[position].ad+" "+ogrenciler[position].soyad
        holder.telNo.text=ogrenciler[position].telNo

        val ref= FirebaseDatabase.getInstance().getReference("okullar").child(ogrenciler[position].okulId.toString())
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ok in snapshot.children){
//                    Log.e("okul1",snapshot.children.toString())
//                    Log.e("okul2",ok.toString())
                    if (ok!=null) {
                        val o = ok.value.toString()
//                    val okul=ok.child("okulAdi").getValue(String::class.java).toString()
                        holder.OkulNoVeAdi.text = ogrenciler[position].no.toString() + " " + o
                        Log.e("okul3", o)
                    }
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        holder.btSec.setOnClickListener {
            val intent=Intent(mContext,KitapAlActivity::class.java)

            intent.putExtra("id",ogrenciler[position].Id)
            intent.putExtra("ad",ogrenciler[position].ad)
            intent.putExtra("soyad",ogrenciler[position].soyad)
            intent.putExtra("no",ogrenciler[position].no)
            intent.putExtra("telNo",ogrenciler[position].telNo)
            activity.setResult(Activity.RESULT_OK,intent)
            activity.finish()
//            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return ogrenciler.size
    }


}