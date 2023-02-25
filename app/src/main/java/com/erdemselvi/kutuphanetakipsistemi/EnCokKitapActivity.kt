package com.erdemselvi.kutuphanetakipsistemi

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.erdemselvi.kutuphanetakipsistemi.adapter.EnCokKitapAdapter
import com.erdemselvi.kutuphanetakipsistemi.databinding.ActivityEnCokKitapBinding
import com.erdemselvi.kutuphanetakipsistemi.model.Emanets
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EnCokKitapActivity : AppCompatActivity() {

    lateinit var binding:ActivityEnCokKitapBinding
    lateinit var database: FirebaseDatabase
    private lateinit var emanet:ArrayList<Emanets>
    lateinit var list1:ArrayList<String>
    lateinit var list2:ArrayList<Int>
    lateinit var adapter: EnCokKitapAdapter
    lateinit var liste:ArrayList<String>
    lateinit var result:HashMap<String,Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_en_cok_kitap)
        binding= ActivityEnCokKitapBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        supportActionBar?.title="En Çok Okunan Kitaplar"

        list1=ArrayList()
        list2=ArrayList()
        enCokOkunanKitaplariListele()
        adapter= EnCokKitapAdapter(this,list1,list2)
        binding.rvEnCokOkunanKitap.hasFixedSize()
        binding.rvEnCokOkunanKitap.layoutManager=
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL)
        binding.rvEnCokOkunanKitap.adapter=adapter

    }
    private fun enCokOkunanKitaplariListele() {
        database= FirebaseDatabase.getInstance()
        emanet=ArrayList()
        liste= ArrayList()
        val ref=database.getReference("emanet")
        ref.addValueEventListener(@SuppressLint("SuspiciousIndentation")
        object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                emanet.clear()
                for (s in snapshot.children){
                    val emnt=s.getValue(Emanets::class.java)

                    if (emnt!=null) {
                        emnt.id=s.key
                        if (emnt.geriVermeTarihi != 0L) {
                            emanet.add(emnt)
                            liste.add(emnt.isbn.toString())
                        }
                    }
                }
                emanet.reverse()
                Log.e("emanet",emanet.toString())
                val m= HashMap<String,Int>()
                liste.forEach { if(m[it]!=null) m[it]=m[it]!!+1 else m[it]=1 }
                result=HashMap()
                result= m.toList().sortedByDescending { (key,value)->
                    value
                }.toMap() as HashMap<String, Int>

                result.forEach {
                    list1.add(it.key)
                    list2.add(it.value)
                    Log.e(  "sonuç","${it.key}:${it.value} defa tekrarlandı") }

//                for(e in result){
//                    list1.add(e.key)
//                    list2.add(e.value)
//                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}