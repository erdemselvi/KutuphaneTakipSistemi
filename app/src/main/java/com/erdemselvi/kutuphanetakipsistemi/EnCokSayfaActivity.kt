package com.erdemselvi.kutuphanetakipsistemi

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.erdemselvi.kutuphanetakipsistemi.adapter.EnCokSayfaAdapter
import com.erdemselvi.kutuphanetakipsistemi.databinding.ActivityEnCokSayfaBinding
import com.erdemselvi.kutuphanetakipsistemi.model.Emanets2
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EnCokSayfaActivity : AppCompatActivity() {

    lateinit var binding:ActivityEnCokSayfaBinding
    lateinit var database: FirebaseDatabase
    private lateinit var emanet:ArrayList<Emanets2>
    lateinit var liste:ArrayList<String>
    lateinit var sayfaSayilari:ArrayList<Int>
    lateinit var list1:ArrayList<String>
    lateinit var list2:ArrayList<Int>
    lateinit var adapter: EnCokSayfaAdapter
    lateinit var topSayfa:ArrayList<Int>
    lateinit var ogrIdler:ArrayList<String>

//    lateinit var result:HashMap<String,Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_en_cok_sayfa)
        binding=ActivityEnCokSayfaBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        supportActionBar?.title="Öğrencilerin Okudukları Sayfa Sayıları"
        emanet=ArrayList()
        list1=ArrayList()
        list2=ArrayList()
        topSayfa= ArrayList()
        ogrIdler= ArrayList()
        sayfaSayilari=ArrayList()
        okunanKitaplariListele()
        adapter= EnCokSayfaAdapter(this,ogrIdler,topSayfa)
//        adapter= EnCokSayfaAdapter(this,liste,sayfaSayilari)
        binding.rvEnCokSayfa.hasFixedSize()
        binding.rvEnCokSayfa.layoutManager=
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        binding.rvEnCokSayfa.adapter=adapter

    }
    private fun okunanKitaplariListele() {
        database= FirebaseDatabase.getInstance()

        liste= ArrayList()

        val ref=database.getReference("emanet")
        ref.addValueEventListener(@SuppressLint("SuspiciousIndentation")
        object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                emanet.clear()
                sayfaSayilari.clear()
                for (s in snapshot.children){
                    val emnt=s.getValue(Emanets2::class.java)

                    if (emnt!=null) {
                        emnt.id=s.key
                        if (emnt.geriVermeTarihi != 0L) {
                            emanet.add(emnt)
                            liste.add(emnt.ogrId.toString())
                            sayfaSayilari.add(emnt.sayfaSayisi!!.toInt())
                            Log.e("kitapId",emnt.kitapId.toString())

                        }
                    }
                }





//                emanet.reverse()
//                Log.e("emanet",emanet.toString())
                var m= HashMap<String,Int>()
                var n= HashMap<String,Int>()
                liste.forEach { if(m[it]!=null) m[it]=m[it]!!+1 else m[it]=1 }

                m.forEach {
                    var top=0
                    for ((sayac, a) in liste.withIndex()){
                        if (it.key==a){
                            top+=sayfaSayilari[sayac]
                        }
                    }
                    n.put(it.key,top)
//                    topSayfa.add(top)
//                    ogrIdler.add(it.key)

                }
                val sonListe=n.toList().sortedByDescending { (key,value)->
                    value
                }.toMap() as HashMap<String, Int>
                sonListe.forEach {
                    topSayfa.add(it.value)
                    ogrIdler.add(it.key)
                }

//                result=HashMap()
//                result= m.toList().sortedByDescending { (key,value)->
//                    value
//                }.toMap() as HashMap<String, Int>
//
//                result.forEach {
//                    list1.add(it.key)
//                    list2.add(it.value)
//                    Log.e(  "sonuç","${it.key}:${it.value} defa tekrarlandı") }

//                for(e in result){
//                    list1.add(e.key)
//                    list2.add(e.value)
//                }

                Log.e("emanetson",emanet.toString())


Log.e("sayfasayısıson",sayfaSayilari.toString())
//                var sayac=0
//                for (list1 in liste){
//                    var toplam=0
//                    for (list2 in liste){
//                        if (list1==list2){
//                            var sayac2=0
//                            for (idler in ogrIdler){
//
//                                if (list1==idler){
//                                    sayac2++
//
//                                }
//                            }
//                            if (sayac2==0){
//                                toplam+=sayfaSayilari[sayac]
//                            }
//                        }
//                    }
//                    topSayfa.add(toplam)
//                    ogrIdler.add(list1)
//                    sayac++
//                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }
}