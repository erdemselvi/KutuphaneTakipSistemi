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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.erdemselvi.kutuphanetakipsistemi.KitapVerActivity
import com.erdemselvi.kutuphanetakipsistemi.R
import com.erdemselvi.kutuphanetakipsistemi.model.Emanet2
import com.erdemselvi.kutuphanetakipsistemi.model.Kitaplar2
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.collection.LLRBNode.Color
import com.squareup.picasso.Picasso

class KitapBulAdapter(val activity: Activity,val context: Context, val kitaplar:ArrayList<Kitaplar2>):RecyclerView.Adapter<KitapBulAdapter.KitapBulTasarim>() {

    inner class KitapBulTasarim(view: View):RecyclerView.ViewHolder(view){
        val tvKitapAdi:TextView=view.findViewById(R.id.tvKitapAdi)
        val tvYazarAdi:TextView=view.findViewById(R.id.tvYazar)
        val tvOzet:TextView=view.findViewById(R.id.tvOzet)
        val ivResim:ImageView=view.findViewById(R.id.ivResim)
        val btEkle:Button=view.findViewById(R.id.btEkle)
        val clKitap:LinearLayout=view.findViewById(R.id.clKitap)
        lateinit var emanet:ArrayList<Emanet2>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KitapBulTasarim {
        val tasarim=LayoutInflater.from(context).inflate(R.layout.kitap_ver_bul_row,parent,false)
        return KitapBulTasarim(tasarim)
    }

    override fun onBindViewHolder(holder: KitapBulTasarim, @SuppressLint("RecyclerView") position: Int) {
        var sayac=0
        var emanetDiziSayisi=0
        holder.tvKitapAdi.text=kitaplar[position].kitapAdi
        holder.tvYazarAdi.text=kitaplar[position].yazarAdi
        holder.tvOzet.text=kitaplar[position].kisaOzet
        Picasso.with(context)
            .load(kitaplar[position].resimUrl!!.toUri())

            .resize(128, 187)         //optional
            .centerCrop()                        //optional
            .into(holder.ivResim)
        holder.btEkle.setOnClickListener {
            val intent= Intent(context, KitapVerActivity::class.java)

            intent.putExtra("id",kitaplar[position].id)
            intent.putExtra("kitapAdi",kitaplar[position].kitapAdi)
            intent.putExtra("yazarAdi",kitaplar[position].yazarAdi)
            intent.putExtra("ozet",kitaplar[position].kisaOzet)
            intent.putExtra("ISBN",kitaplar[position].isbn)
            intent.putExtra("resimUrl",kitaplar[position].resimUrl)
            intent.putExtra("sayfaSayisi",kitaplar[position].sayfaSayisi)
            activity.setResult(Activity.RESULT_OK,intent)
            activity.finish()
        }

        //Emanette olan kitapları gri renkte göster ve ekle butonunu gizle
        holder.emanet=ArrayList()
        val refEmanet= FirebaseDatabase.getInstance().getReference("emanet")

//        //emanet veritabanında kaç tane kayıt var diye sayılıyor
//        refEmanet.addValueEventListener(object :ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
////                if (snapshot.value != null) {   // Check for data snapshot has some value
////                    emanetDiziSayisi=
////                        snapshot.childrenCount.toInt()  // check for counts of data snapshot children
////                }
//                for (e in snapshot.children) {
//                    val emnt = e.getValue(Emanet2::class.java)
//                    emanetDiziSayisi++
//
//                }
//                Log.e("dizisayısı",emanetDiziSayisi.toString())
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//
//        })
//        ///////////

        refEmanet.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                holder.emanet.clear()
                for (e in snapshot.children){
                    val emnt=e.getValue(Emanet2::class.java)
                    if (emnt!=null && emnt.geriVermeTarihi==0L && emnt.kitapId==kitaplar[position].id){
                        holder.clKitap.setBackgroundColor(ContextCompat.getColor(context,R.color.gray))
                        holder.btEkle.visibility=View.GONE
                        holder.emanet.add(emnt)

                    }
                    else{

                        if (emnt != null) {
                            holder.emanet.add(emnt)
                            sayac++
                        }
                    }
//                    if(emnt!=null && (emnt.geriVermeTarihi!=0L && emnt.kitapId==kitaplar[position].id)){
//                        holder.clKitap.setBackgroundColor(ContextCompat.getColor(context,R.color.teal_200))
//                        holder.btEkle.visibility=View.VISIBLE
//                        holder.emanet.add(emnt)
//
//                    }
//                    if (emnt!=null  && emnt.kitapId!=kitaplar[position].id){
//                        holder.clKitap.setBackgroundColor(ContextCompat.getColor(context,R.color.teal_200))
//                        holder.btEkle.visibility=View.VISIBLE
//                        holder.emanet.add(emnt)
//                    }
                }
                if (sayac==holder.emanet.size){
                   holder.clKitap.setBackgroundColor(ContextCompat.getColor(context,R.color.teal_200))
                   holder.btEkle.visibility=View.VISIBLE
                }
                Log.e("emanet",holder.emanet.toString())
 //               notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    override fun getItemCount(): Int {
        return kitaplar.size
    }
}