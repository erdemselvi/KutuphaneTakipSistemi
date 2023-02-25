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
import com.erdemselvi.kutuphanetakipsistemi.OgretmenBulActivity
import com.erdemselvi.kutuphanetakipsistemi.R
import com.erdemselvi.kutuphanetakipsistemi.model.Ogretmen2
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OgretmenBulAdaptor(val activity: Activity,val context: Context, val liste:ArrayList<Ogretmen2>):RecyclerView.Adapter<OgretmenBulAdaptor.OgretmenBulTasarim>() {
    inner class OgretmenBulTasarim(tasarim: View):RecyclerView.ViewHolder(tasarim){
        val ogrAdSoyad: TextView =tasarim.findViewById(R.id.tvOgretmenAdi)
        val telNo: TextView =tasarim.findViewById(R.id.tvTelNo)
        val okulAdi: TextView =tasarim.findViewById(R.id.tvOkulAdi)
        val btSec: Button =tasarim.findViewById(R.id.btSec)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OgretmenBulTasarim {
      val tasarim=LayoutInflater.from(context).inflate(R.layout.ogretmen_bul_row,parent,false)
        return OgretmenBulTasarim(tasarim)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OgretmenBulTasarim, position: Int) {
        holder.ogrAdSoyad.text=liste[position].ad+" "+liste[position].soyad

        val ref= FirebaseDatabase.getInstance().getReference("okullar").child(liste[position].okulId.toString())
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ok in snapshot.children){
//                    Log.e("okul1",snapshot.children.toString())
//                    Log.e("okul2",ok.toString())
                    if (ok!=null) {
                        val o = ok.value.toString()
//                    val okul=ok.child("okulAdi").getValue(String::class.java).toString()
                        holder.okulAdi.text = o
                        Log.e("okul3", o)
                    }
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        holder.btSec.setOnClickListener {
            val intent= Intent(context, OgretmenBulActivity::class.java)

            intent.putExtra("id",liste[position].Id)
            intent.putExtra("ad",liste[position].ad)
            intent.putExtra("soyad",liste[position].soyad)
            intent.putExtra("telNo",liste[position].telNo)
            activity.setResult(Activity.RESULT_OK,intent)
            activity.finish()

        }
    }

    override fun getItemCount(): Int {
        return liste.size
    }
}