package com.erdemselvi.kutuphanetakipsistemi

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.net.toUri
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.erdemselvi.kutuphanetakipsistemi.adapter.EnCokAdapter
import com.erdemselvi.kutuphanetakipsistemi.adapter.EnCokKitapAdapter
import com.erdemselvi.kutuphanetakipsistemi.adapter.EnCokSayfaAdapter
import com.erdemselvi.kutuphanetakipsistemi.adapter.KitapAlAdapter
import com.erdemselvi.kutuphanetakipsistemi.databinding.ActivityIstatistikBinding
import com.erdemselvi.kutuphanetakipsistemi.model.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class IstatistikActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIstatistikBinding
    lateinit var ogrenciler:ArrayList<String>
    lateinit var database: FirebaseDatabase
    private lateinit var emanet:ArrayList<Emanets>
    lateinit var liste:ArrayList<String>
    lateinit var result:HashMap<String,Int>
    lateinit var list1:ArrayList<String>
    lateinit var list2:ArrayList<Int>
    lateinit var adapter: EnCokAdapter

    private lateinit var emanet2:ArrayList<Emanets2>
    lateinit var liste2:ArrayList<String>
    lateinit var sayfaSayilari:ArrayList<Int>
    lateinit var list12:ArrayList<String>
    lateinit var list22:ArrayList<Int>
    lateinit var adapter2: EnCokSayfaAdapter
    lateinit var topSayfa:ArrayList<Int>
    lateinit var ogrIdler:ArrayList<String>


    private lateinit var emanet3:ArrayList<Emanets>
    lateinit var list13:ArrayList<String>
    lateinit var list23:ArrayList<Int>
    lateinit var adapter3: EnCokKitapAdapter
    lateinit var liste3:ArrayList<String>
    lateinit var result3:HashMap<String,Int>

    private lateinit var okullar:ArrayList<Okul2>
    lateinit var okulAdi:ArrayList<String>
    lateinit var okullId:ArrayList<String>
    lateinit var spAdapter: ArrayAdapter<String>
    var okulId:String="0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_istatistik)

        binding=ActivityIstatistikBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        supportActionBar?.title="İstatistikler"

        val prefences = getSharedPreferences("users", Context.MODE_PRIVATE)
        val yetki=prefences.getString("yetki","yok")
        okulId=prefences.getString("okulId","0").toString()
        if (yetki=="yönetici"){
            binding.llFiltrele.visibility=View.VISIBLE
        }

        okullar= ArrayList()
        okulAdi=ArrayList()
        okullId=ArrayList()
        okullariListele()
//        okulId=okullId[binding.spOkul.selectedItemPosition]
        binding.spOkul.onItemSelectedListener=object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                okulId=okullId[position]
                Log.e("okulId=",okulId.toString())

                val preferences=getSharedPreferences("users", Context.MODE_PRIVATE)
                val editor = preferences.edit()
                editor.putString("spOkulId",okulId.toString())
                editor.apply()

                kitapSayisi(okulId)
                ogrenciSayisi(okulId)
                okunanKitaplariListele()
                adapter= EnCokAdapter(this@IstatistikActivity,list1,list2)
                binding.rvEnCokKitapOkuyan.hasFixedSize()
                binding.rvEnCokKitapOkuyan.layoutManager=
                    StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                binding.rvEnCokKitapOkuyan.adapter=adapter



                emanet2=ArrayList()
                list12=ArrayList()
                list22=ArrayList()
                topSayfa= ArrayList()
                ogrIdler= ArrayList()
                sayfaSayilari=ArrayList()
                okunanKitaplariListele2()
                adapter2= EnCokSayfaAdapter(this@IstatistikActivity,ogrIdler,topSayfa)
//        adapter= EnCokSayfaAdapter(this,liste,sayfaSayilari)
                binding.rvEnCokSayfa.hasFixedSize()
                binding.rvEnCokSayfa.layoutManager=
                    StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                binding.rvEnCokSayfa.adapter=adapter2

                enCokOkunanKitaplariListele()
                adapter3= EnCokKitapAdapter(this@IstatistikActivity,list13,list23)
                binding.rvEnCokOkunanKitap.hasFixedSize()
                binding.rvEnCokOkunanKitap.layoutManager=
                    StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL)
                binding.rvEnCokOkunanKitap.adapter=adapter3
//                adapter.notifyDataSetChanged()
//                adapter2.notifyDataSetChanged()
//                adapter3.notifyDataSetChanged()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        kitapSayisi(okulId.toString())
        ogrenciSayisi(okulId.toString())

        list1=ArrayList()
        list2=ArrayList()
        okunanKitaplariListele()
        adapter= EnCokAdapter(this,list1,list2)
        binding.rvEnCokKitapOkuyan.hasFixedSize()
        binding.rvEnCokKitapOkuyan.layoutManager=
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        binding.rvEnCokKitapOkuyan.adapter=adapter



        emanet2=ArrayList()
        list12=ArrayList()
        list22=ArrayList()
        topSayfa= ArrayList()
        ogrIdler= ArrayList()
        sayfaSayilari=ArrayList()
        okunanKitaplariListele2()
        adapter2= EnCokSayfaAdapter(this,ogrIdler,topSayfa)
//        adapter= EnCokSayfaAdapter(this,liste,sayfaSayilari)
        binding.rvEnCokSayfa.hasFixedSize()
        binding.rvEnCokSayfa.layoutManager=
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        binding.rvEnCokSayfa.adapter=adapter2


        list13=ArrayList()
        list23=ArrayList()
        enCokOkunanKitaplariListele()
        adapter3= EnCokKitapAdapter(this,list13,list23)
        binding.rvEnCokOkunanKitap.hasFixedSize()
        binding.rvEnCokOkunanKitap.layoutManager=
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL)
        binding.rvEnCokOkunanKitap.adapter=adapter3
    }

    private fun okullariListele() {
        database= FirebaseDatabase.getInstance()
        val ref=database.getReference("okullar")
        ref.addValueEventListener(@SuppressLint("SuspiciousIndentation")
        object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                okullar.clear()
                okulAdi.add("Tüm Okullar")
                okullId.add("0")
                for (s in snapshot.children){
                    val ok=s.getValue(Okul2::class.java)

                    if (ok!=null) {
                        ok.id=s.key

                        okullar.add(ok)
                        okulAdi.add(ok.okulAdi.toString())
                        okullId.add(ok.id.toString())
                    }
                }
                spAdapter= ArrayAdapter(this@IstatistikActivity,R.layout.spinner_item,okulAdi)
                spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spOkul.adapter=spAdapter

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun okunanKitaplariListele() {
        database= FirebaseDatabase.getInstance()
        emanet=ArrayList()
        liste= ArrayList()

        val ref=database.getReference("emanet")
        ref.addValueEventListener(@SuppressLint("SuspiciousIndentation")
        object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                emanet.clear()
                liste.clear()
                list1.clear()
                list2.clear()
                for (s in snapshot.children){
                    val emnt=s.getValue(Emanets::class.java)

                    if (emnt!=null) {
                        emnt.id=s.key
                        if (okulId!="0") {
                            if (emnt.geriVermeTarihi != 0L && emnt.okulId == okulId) {
                                emanet.add(emnt)
                                liste.add(emnt.ogrId.toString())
                            }
                        }else{
                            if (emnt.geriVermeTarihi != 0L) {
                                emanet.add(emnt)
                                liste.add(emnt.ogrId.toString())
                            }
                        }
                    }
                }
                emanet.reverse()
                Log.e("emanet",emanet.toString())
                val m= HashMap<String,Int>()
                liste.forEach { if(m[it]!=null) m[it]=m[it]!!+1 else m[it]=1 }
                result=HashMap()
                if (m.size>1) {
                result= m.toList().sortedByDescending { (key,value)->
                    value
                }.toMap() as HashMap<String, Int>
                }else{
                    result=m
                }
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

    private fun okunanKitaplariListele2() {
        database= FirebaseDatabase.getInstance()

        liste2= ArrayList()

        val ref=database.getReference("emanet")
        ref.addValueEventListener(@SuppressLint("SuspiciousIndentation")
        object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                emanet2.clear()
                sayfaSayilari.clear()
                liste2.clear()
                topSayfa.clear()
                ogrIdler.clear()
                for (s in snapshot.children){
                    val emnt2=s.getValue(Emanets2::class.java)

                    if (emnt2!=null) {
                        emnt2.id=s.key
                        if (okulId!="0") {
                            if (emnt2.geriVermeTarihi != 0L && emnt2.okulId == okulId) {
                                emanet2.add(emnt2)
                                liste2.add(emnt2.ogrId.toString())
                                sayfaSayilari.add(emnt2.sayfaSayisi!!.toInt())
                                Log.e("kitapId", emnt2.kitapId.toString())

                            }
                        }else{
                            if (emnt2.geriVermeTarihi != 0L) {
                                emanet2.add(emnt2)
                                liste2.add(emnt2.ogrId.toString())
                                sayfaSayilari.add(emnt2.sayfaSayisi!!.toInt())
                                Log.e("kitapId", emnt2.kitapId.toString())

                            }
                        }
                    }
                }





//                emanet.reverse()
//                Log.e("emanet",emanet.toString())
                val m= HashMap<String,Int>()
                val n= HashMap<String,Int>()
                liste2.forEach { if(m[it]!=null) m[it]=m[it]!!+1 else m[it]=1 }

                m.forEach {
                    var top=0
                    for ((sayac, a) in liste2.withIndex()){
                        if (it.key==a){
                            top+=sayfaSayilari[sayac]
                        }
                    }
                    n.put(it.key,top)
//                    topSayfa.add(top)
//                    ogrIdler.add(it.key)

                }
                var sonListe:HashMap<String,Int>
                sonListe=HashMap()
                if (n.size>1) {
                sonListe=n.toList().sortedByDescending { (key,value)->
                    value
                }.toMap() as HashMap<String, Int>
                }else{
                    sonListe=n
                }
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

                Log.e("emanetson",emanet2.toString())


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
                adapter2.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun enCokOkunanKitaplariListele() {
        database= FirebaseDatabase.getInstance()
        emanet3=ArrayList()
        liste3= ArrayList()
        list13= ArrayList()
        list23= ArrayList()
        val ref3=database.getReference("emanet")
        ref3.addValueEventListener(@SuppressLint("SuspiciousIndentation")
        object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                emanet3.clear()
                liste3.clear()
                list13.clear()
                list23.clear()
                for (s in snapshot.children){
                    val emnt=s.getValue(Emanets::class.java)

                    if (emnt!=null) {
                        emnt.id=s.key
                        if (okulId!="0") {
                            if (emnt.geriVermeTarihi != 0L && emnt.okulId == okulId) {
                                emanet3.add(emnt)
                                liste3.add(emnt.isbn.toString())
                            }
                        }else{
                            if (emnt.geriVermeTarihi != 0L) {
                                emanet3.add(emnt)
                                liste3.add(emnt.isbn.toString())
                            }
                        }
                    }
                }
                emanet3.reverse()
                Log.e("emanet",emanet3.toString())
                val m3= HashMap<String,Int>()
                liste3.forEach { if(m3[it]!=null) m3[it]=m3[it]!!+1 else m3[it]=1 }
                result3=HashMap()
                if (m3.size>2) {
                    result3 = m3.toList().sortedByDescending { (key, value) ->
                        value
                    }.toMap() as HashMap<String, Int>
                }else{
                    result3=m3
                }
                result3.forEach {
                    list13.add(it.key)
                    list23.add(it.value)
                    Log.e(  "sonuç","${it.key}:${it.value} defa tekrarlandı") }

//                for(e in result){
//                    list1.add(e.key)
//                    list2.add(e.value)
//                }
                adapter3.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
    fun kitapSayisi(okulId:String){
        database= FirebaseDatabase.getInstance()
        val ref3=database.getReference("kitaplar")
        ref3.addValueEventListener(@SuppressLint("SuspiciousIndentation")
        object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (okulId=="0"){
                    binding.tvKitapSayisi.text=snapshot.childrenCount.toString()
                }
                else{
                    var sayac=0
                    for (k in snapshot.children){
                        val kitap=k.getValue(Kitaplar2::class.java)
                        if (kitap!=null){
                            kitap.id=k.key
                            if (okulId!="0"){
                                if (okulId==kitap.okulId) {
                                    sayac++
                                    }
                            }else{
                                sayac++
                            }
                        }
                    }
                    binding.tvKitapSayisi.text=sayac.toString()
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
    fun ogrenciSayisi(okulId:String){
        database= FirebaseDatabase.getInstance()
        val ref3=database.getReference("ogrenciler")
        ref3.addValueEventListener(@SuppressLint("SuspiciousIndentation")
        object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (okulId=="0") {
                    binding.tvOgrenciSayisi.text = snapshot.childrenCount.toString()
                }
                else{
                    var sayac=0
                    for (k in snapshot.children){
                        val ogr=k.getValue(Ogrenciler2::class.java)
                        if (ogr!=null){
                            if (okulId!="0"){
                            if (okulId==ogr.okulId) {
                                sayac++
                            }
                            }else{
                                sayac++
                            }
                        }
                    }
                    binding.tvOgrenciSayisi.text=sayac.toString()
                    sayac=0
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}