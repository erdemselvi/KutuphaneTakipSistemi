package com.erdemselvi.kutuphanetakipsistemi

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.erdemselvi.kutuphanetakipsistemi.adapter.OgrenciListeleAdapter
import com.erdemselvi.kutuphanetakipsistemi.databinding.ActivityOgrenciListeleBinding
import com.erdemselvi.kutuphanetakipsistemi.model.Emanet2
import com.erdemselvi.kutuphanetakipsistemi.model.Ogrenciler2
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList

class OgrenciListeleActivity : AppCompatActivity() {

    lateinit var binding: ActivityOgrenciListeleBinding
    lateinit var adapter:OgrenciListeleAdapter
    lateinit var database: FirebaseDatabase
    lateinit var ogrenciler:ArrayList<Ogrenciler2>
    lateinit var emanet:ArrayList<Emanet2>

    lateinit var emanetTarihi:ArrayList<Long>
    lateinit var emanetKitapId:ArrayList<String>
    lateinit var emanetSuresi:ArrayList<Int>
    var kontrol=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ogrenci_listele)
        binding= ActivityOgrenciListeleBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        emanet=ArrayList()
        ogrenciler= ArrayList()
        emanetTarihi= ArrayList()
        emanetKitapId= ArrayList()
        emanetSuresi= ArrayList()

        database=FirebaseDatabase.getInstance()

        if (binding.rbTumOgr.isChecked){
            kontrol=0
            ogrencileriListele()
        }
        if (binding.rbKitapAlanlar.isChecked){
            kontrol=0
            ogrListeleKitap()
        }
        if (binding.rbTeslimEtmeyenler.isChecked){
            kontrol=1
            kitTesEtmeyenler()
        }

        binding.rbTumOgr.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                kontrol=0
                ogrencileriListele()
//                adapter= OgrenciListeleAdapter(this,ogrenciler)
//                binding.rvOgrenciler.setHasFixedSize(true)
//                binding.rvOgrenciler.adapter=adapter
            }
        }
        binding.rbKitapAlanlar.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                kontrol=0
                ogrListeleKitap()
//                adapter= OgrenciListeleAdapter(this,ogrenciler)
//                binding.rvOgrenciler.setHasFixedSize(true)
//                binding.rvOgrenciler.adapter=adapter

            }
        }
        binding.rbTeslimEtmeyenler.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                kontrol=1
                kitTesEtmeyenler()
//                adapter= OgrenciListeleAdapter(this,ogrenciler)
//                binding.rvOgrenciler.setHasFixedSize(true)
//                binding.rvOgrenciler.adapter=adapter
            }
        }

        binding.rvOgrenciler.setHasFixedSize(true)
        binding.rvOgrenciler.layoutManager=StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL)
        Log.e("kontrolAdaptor",kontrol.toString())
        adapter= OgrenciListeleAdapter(this,ogrenciler,kontrol,emanetTarihi,emanetKitapId,emanetSuresi)
        binding.rvOgrenciler.adapter=adapter

    }


    private fun ogrencileriListele() {

        val ref=database.getReference("ogrenciler")
        ref.addValueEventListener(object :ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                ogrenciler.clear()
                for (o in snapshot.children){
                    val ogr=o.getValue(Ogrenciler2::class.java)
                    if (ogr != null) {
                        ogr.Id=o.key
                        ogrenciler.add(ogr)
                    }
                }
                binding.rvOgrenciler.layoutManager=StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL)

                adapter= OgrenciListeleAdapter(this@OgrenciListeleActivity,ogrenciler,kontrol,emanetTarihi,emanetKitapId,emanetSuresi)
                binding.rvOgrenciler.adapter=adapter
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun ogrListeleKitap() {

        val refEmanet=FirebaseDatabase.getInstance().getReference("emanet")
        refEmanet.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                emanet.clear()
                for (e in snapshot.children){
                    val emnt=e.getValue(Emanet2::class.java)
                    if (emnt!=null && emnt.geriVermeTarihi==0L){
                        emanet.add(emnt)

                    }
                }
                Log.e("emanet",emanet.toString())
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        val ref=database.getReference("ogrenciler")
        ref.addValueEventListener(object :ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")

            override fun onDataChange(snapshot: DataSnapshot) {
                ogrenciler.clear()
                var sayac=0
                for (o in snapshot.children){
                    sayac=0
                    val ogr=o.getValue(Ogrenciler2::class.java)
                    Log.e("ogrenciler",ogr.toString())
                    if (ogr != null) {
                        ogr.Id=o.key

                        for (e in emanet){
                           if (ogr.Id ==e.ogrId){
                               sayac+=1
                           }
                        }


                    }
                    if (sayac!=0){
                        if (ogr != null) {
                            ogrenciler.add(ogr)
                        }
                    }
                }
//                if (ogrenciler.isEmpty()){
//                    val ad:HashMap<String,Any?> = HashMap()
//                    ad.put("Id","0")
//                    ad.put("ad","Kayıt Yok")
//                    ad.put("soyad","Kayıt Yok")
//                    ad.put("no",0)
//                    ad.put("okulId",0)
//                    ad.put("telNo","Kayıt Yok")
//                    ad.put("email","Kayıt Yok")
//                    ad.put("ogrId","Kayıt Yok")
//                    ad.put("kayitTarihi",1000)
//                    ogrenciler.add(ad)
//                }
                Log.e("ogrenci",ogrenciler.toString())
                binding.rvOgrenciler.layoutManager=StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL)

                adapter= OgrenciListeleAdapter(this@OgrenciListeleActivity,ogrenciler,kontrol,emanetTarihi,emanetKitapId,emanetSuresi)
                binding.rvOgrenciler.adapter=adapter
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun kitTesEtmeyenler() {

        val refEmanet=FirebaseDatabase.getInstance().getReference("emanet")
        refEmanet.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                emanet.clear()
                for (e in snapshot.children){
                    val emnt=e.getValue(Emanet2::class.java)
                    if (emnt!=null && emnt.geriVermeTarihi==0L){
                        emanet.add(emnt)

                    }
                }
                Log.e("emanet",emanet.toString())
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        val ref=database.getReference("ogrenciler")
        ref.addValueEventListener(object :ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")

            override fun onDataChange(snapshot: DataSnapshot) {
                emanetSuresi.clear()
                emanetTarihi.clear()
                emanetKitapId.clear()
                ogrenciler.clear()
                var sayac=0
                for (o in snapshot.children){
                    sayac=0
                    val ogr=o.getValue(Ogrenciler2::class.java)
                    Log.e("ogrenciler",ogr.toString())
                    if (ogr != null) {
                        ogr.Id=o.key

                        for (e in emanet){
                            if (ogr.Id ==e.ogrId){
                                val simdikiTarih = Date().time
                                val gecenSure=simdikiTarih-e.emanetTarihi
                                val saniyeyeCevir = (gecenSure) / 1000

                                val day = String.format("%d", saniyeyeCevir / 86400)
                                if (day.toInt()>e.teslimSuresi!!.toInt()){
                                    sayac+=1
                                    emanetTarihi.add(e.emanetTarihi)
                                    emanetSuresi.add(e.teslimSuresi!!)
                                    emanetKitapId.add(e.kitapId.toString())

                                }

                            }
                        }


                    }
                    if (sayac!=0){
                        if (ogr != null) {
                            ogrenciler.add(ogr)
                        }
                    }
                }
                Log.e("emanetTarihi",emanetTarihi.toString())
                Log.e("emanetSuresi",emanetSuresi.toString())
                Log.e("ogrenci",ogrenciler.toString())
                Log.e("kontrol",kontrol.toString())
                binding.rvOgrenciler.layoutManager=StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL)

                adapter= OgrenciListeleAdapter(this@OgrenciListeleActivity,ogrenciler,kontrol,emanetTarihi,emanetKitapId,emanetSuresi)
                binding.rvOgrenciler.adapter=adapter
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })


    }

}