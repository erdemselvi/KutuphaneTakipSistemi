package com.erdemselvi.kutuphanetakipsistemi

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.erdemselvi.kutuphanetakipsistemi.adapter.KitapListeleAdapter
import com.erdemselvi.kutuphanetakipsistemi.databinding.ActivityKitapListeleBinding
import com.erdemselvi.kutuphanetakipsistemi.model.Emanet2
import com.erdemselvi.kutuphanetakipsistemi.model.Kitaplar2
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class KitapListeleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKitapListeleBinding
    private lateinit var adapter:KitapListeleAdapter
    private lateinit var database: FirebaseDatabase
    lateinit var kitaplar:ArrayList<Kitaplar2>
    lateinit var emanet:ArrayList<Emanet2>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kitap_listele)
        binding=ActivityKitapListeleBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        kitaplar= ArrayList()
        emanet=ArrayList()

        database=FirebaseDatabase.getInstance()
        if (binding.rbTumu.isChecked){
            kitaplariListele()
        }
        if (binding.rbOgrdekiler.isChecked){
            kitaplariListeleOgrenci()
        }
        if (binding.rbKutuphanedekiler.isChecked){
            kitaplariListeleKutuphane()

        }
        binding.rbTumu.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
//                kitaplar.clear()
                kitaplariListele()
//                adapter=KitapListeleAdapter(this,kitaplar)
//                binding.rvKitaplar.setHasFixedSize(true)
//                binding.rvKitaplar.adapter=adapter
            }
        }
        binding.rbOgrdekiler.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
//                kitaplar.clear()
                kitaplariListeleOgrenci()
//                adapter=KitapListeleAdapter(this,kitaplar)
//                binding.rvKitaplar.setHasFixedSize(true)
//                binding.rvKitaplar.adapter=adapter

            }
        }
        binding.rbKutuphanedekiler.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
//                kitaplar.clear()
                kitaplariListeleKutuphane()
//                adapter=KitapListeleAdapter(this,kitaplar)
//                binding.rvKitaplar.setHasFixedSize(true)
//                binding.rvKitaplar.adapter=adapter
            }
        }

//        binding.rvKitaplar.setHasFixedSize(true)
        binding.rvKitaplar.layoutManager=StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL)
//        binding.rvKitaplar.layoutManager=LinearLayoutManager(this@KitapListeleActivity)
        adapter=KitapListeleAdapter(this,kitaplar)
        binding.rvKitaplar.adapter=adapter

    }

    @SuppressLint("SuspiciousIndentation")
    private fun kitaplariListele() {
        val prefences = getSharedPreferences("users", Context.MODE_PRIVATE)
        val okulId = prefences.getString("okulId","yok")
        val yetki = prefences.getString("yetki","öğrenci")
        val ref=database.getReference("kitaplar")
            ref.addValueEventListener(object :ValueEventListener{
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    kitaplar.clear()
                    for (k in snapshot.children){
                        val kitap=k.getValue(Kitaplar2::class.java)
                        if (kitap!=null){
                            if (yetki=="öğrenci" || yetki == "öğretmen") {
                                if (okulId == kitap.okulId) {
                                    kitap.id=k.key
                                    kitaplar.add(kitap)
                                }
                            }else {
                                kitap.id = k.key
                                kitaplar.add(kitap)
                            }
                        }
                    }
                    Log.e("kitaplar",kitaplar.toString())
                    adapter.notifyDataSetChanged()

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    private fun kitaplariListeleOgrenci(){
//        emanet=ArrayList()
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
        val prefences = getSharedPreferences("users", Context.MODE_PRIVATE)
        val okulId = prefences.getString("okulId","yok")
        val yetki = prefences.getString("yetki","öğrenci")
        val ref=database.getReference("kitaplar")
        ref.addValueEventListener(object :ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                kitaplar.clear()
                for (k in snapshot.children){
                    val kitap=k.getValue(Kitaplar2::class.java)
                    if (kitap!=null){
                        if (yetki=="öğrenci" || yetki == "öğretmen"){
                            if (okulId==kitap.okulId){
                                kitap.id=k.key


                                for (e in emanet){
                                    if (kitap.id==e.kitapId) {
                                        kitaplar.add(kitap)
                                    }
                                }
                            }
                        }
                        else{
                            kitap.id=k.key


                            for (e in emanet){
                                if (kitap.id==e.kitapId) {
                                    kitaplar.add(kitap)
                                }
                            }
                        }




                    }
                }
                Log.e("kitaplar",kitaplar.toString())
                adapter.notifyDataSetChanged()


            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
    private fun kitaplariListeleKutuphane(){
        val prefences = getSharedPreferences("users", Context.MODE_PRIVATE)
        val okulId = prefences.getString("okulId","yok")
        val yetki = prefences.getString("yetki","öğrenci")
//        emanet=ArrayList()
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
        val ref=database.getReference("kitaplar")
        ref.addValueEventListener(object :ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                kitaplar.clear()
                var sayac=0
                for (k in snapshot.children){
                    sayac=0
                    val kitap=k.getValue(Kitaplar2::class.java)
                    if (kitap!=null){
                        if (yetki=="öğrenci" || yetki == "öğretmen") {
                            if (okulId == kitap.okulId) {
                                kitap.id = k.key


                                for (e in emanet) {

                                    if (kitap.id != e.kitapId) {
                                        sayac += 1

                                    }
                                }
                                if (sayac == emanet.size) {
                                    kitaplar.add(kitap)
                                }
                            }
                        }else {
                            kitap.id = k.key


                            for (e in emanet) {

                                if (kitap.id != e.kitapId) {
                                    sayac += 1

                                }
                            }
                            if (sayac == emanet.size) {
                                kitaplar.add(kitap)
                            }
                        }

                    }
                }
                Log.e("kitaplar",kitaplar.toString())
                adapter.notifyDataSetChanged()


            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

//        emanet=ArrayList()
//
//
//        val ref=database.getReference("kitaplar")
//        ref.addValueEventListener(object :ValueEventListener{
//            @SuppressLint("NotifyDataSetChanged")
//            override fun onDataChange(snapshot: DataSnapshot) {
//                kitaplar.clear()
//                for (k in snapshot.children){
//                    val kitap=k.getValue(Kitaplar2::class.java)
//                    if (kitap!=null){
//                        kitap.id=k.key
//
//
////                        for (e in emanet){
////                            if (kitap.id!=e.kitapId) {
//                                kitaplar.add(kitap)
////                            }
////                        }
//
//
//                    }
//                }
//                Log.e("kitaplar",kitaplar.toString())
//
//
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//
//            }
//
//        })
//        val ktp:ArrayList<Kitaplar2> = ArrayList()
//        val refEmanet=FirebaseDatabase.getInstance().getReference("emanet")
//        refEmanet.addValueEventListener(object :ValueEventListener{
//            @SuppressLint("NotifyDataSetChanged")
//            override fun onDataChange(snapshot: DataSnapshot) {
//                emanet.clear()
//                for (e in snapshot.children){
//                    val emnt=e.getValue(Emanet2::class.java)
//                    if (emnt!=null && emnt.geriVermeTarihi==0L){
//                        for (k in kitaplar){
//                            if (k.id!=emnt.kitapId){
//                                ktp.add(k)
//                            }
//                        }
////                        emanet.add(emnt)
//
//                    }
//                }
////                kitaplar.clear()
//                kitaplar=ktp
//                Log.e("emanet",emanet.toString())
//                adapter.notifyDataSetChanged()
//
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//
//            }
//
//        })
    }
}