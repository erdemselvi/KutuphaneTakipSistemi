package com.erdemselvi.kutuphanetakipsistemi

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.erdemselvi.kutuphanetakipsistemi.adapter.*
import com.erdemselvi.kutuphanetakipsistemi.databinding.ActivityAnaBinding
import com.erdemselvi.kutuphanetakipsistemi.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AnaActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    private lateinit var binding: ActivityAnaBinding
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

    private lateinit var emanet4:ArrayList<Emanets>
    private lateinit var ogrenci4:ArrayList<Ogrenciler2>
    private lateinit var kitap4:ArrayList<Kitaplar2>
//    private lateinit var kitapAl:ArrayList<KitapAl>
    lateinit var adapter4: KitapAlOgrenciAdapter

    private lateinit var emanet5:ArrayList<Emanets>
    private lateinit var ogrenci5:ArrayList<Ogrenciler2>
    private lateinit var kitap5:ArrayList<Kitaplar2>

    var okullId:String?=""
    //    private lateinit var kitapAl:ArrayList<KitapAl>
    lateinit var adapter5: AlinmisKitaplarAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_ana)

        binding=ActivityAnaBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        database= FirebaseDatabase.getInstance()
        mAuth=FirebaseAuth.getInstance()
//        val currentUser=mAuth.currentUser

//        val donusenKelime=currentUser?.uid
        val prefences = getSharedPreferences("users", Context.MODE_PRIVATE)
        okullId = prefences.getString("okulId","yok")
        gorevliKontroluYap()


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


        emanet4=ArrayList()
        ogrenci4= ArrayList()
        kitap4= ArrayList()
//        kitapAl= ArrayList()
        alinanKitaplariListele()

//        emanet4.reverse() // Son alınan kitaplar üstte gözüksün diye dizi ters çevriliyor
        binding.rvOgrenci.setHasFixedSize(true)
        binding.rvOgrenci.layoutManager= StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL)
        adapter4= KitapAlOgrenciAdapter(this,emanet4)
        binding.rvOgrenci.adapter=adapter4

        emanet5=ArrayList()
        ogrenci5= ArrayList()
        kitap5= ArrayList()
//        kitapAl= ArrayList()
        alinmisKitaplariListele()

//        emanet4.reverse() // Son alınan kitaplar üstte gözüksün diye dizi ters çevriliyor
        binding.rvAlinmisKitaplar.setHasFixedSize(true)
        binding.rvAlinmisKitaplar.layoutManager= StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL)
        adapter5= AlinmisKitaplarAdapter(this,emanet5)
        binding.rvAlinmisKitaplar.adapter=adapter5

        binding.tvCikis.setOnClickListener {
            mAuth.signOut()
            val intent= Intent(this,GirisActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.btQrKod.setOnClickListener {
            val intent= Intent(this,QrKodActivity::class.java)
            startActivity(intent)
        }
    }

    private fun gorevliKontroluYap() {

        var okulId:String?=""
        var ogrId:String?=""
        val currentUser=mAuth.currentUser

        val ogrUid=currentUser!!.uid
        Log.e("öğrenciUid",ogrUid)
        val myRef=database.getReference("ogrenciler")
        myRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                for(s in snapshot.children){
                    val profil = s.getValue(Ogrenciler::class.java)
                    if (profil!=null && profil.ogrId.toString()==ogrUid){
                        okulId=profil.okulId
                        ogrId= s.key
                        binding.tvOgrId.text=s.key.toString()
                        binding.tvKullaniciAdi.text="Merhaba "+profil.ad+" "+profil.soyad
                        Log.e("öğrenciId",binding.tvOgrId.text.toString())
                    }
                }
                val ref = FirebaseDatabase.getInstance().getReference("gorevliler")
                ref.addValueEventListener(object:ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var sayac=0
                        for (s in snapshot.children){
                            val gorev=s.getValue(Gorevli::class.java)
                            Log.e("öğrenciIdgorevli",binding.tvOgrId.text.toString())

                            if (gorev!=null && gorev.gorevliId==binding.tvOgrId.text.toString() && gorev.bitisTarihi==0L){
                                sayac++
                            }
                        }
                        if (sayac!=0){
                            binding.btDashBoard.visibility=View.VISIBLE
                        }
                        else{
                            binding.btDashBoard.visibility=View.GONE
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })



        binding.btDashBoard.setOnClickListener {
            val intent=Intent(this,DashBoardOgrenciActivity::class.java)
            startActivity(intent)
        }

        database= FirebaseDatabase.getInstance()
        val refOkul=database.getReference("okullar").child(okullId.toString())
        refOkul.addValueEventListener(@SuppressLint("SuspiciousIndentation")
        object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {

//                for (s in snapshot.children){
                    val ok=snapshot.getValue(Okul2::class.java)

                    if (ok!=null) {
                        ok.id=snapshot.key
//                        if (s.key==okulId){
                            binding.tvOkulAdi.text="Okul: ${ok.okulAdi}"
//                        }

//                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun okunanKitaplariListele() {

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
                        if (emnt.geriVermeTarihi != 0L && emnt.okulId==okullId) {
                            emanet.add(emnt)
                            liste.add(emnt.ogrId.toString())
                        }
                    }
                }
                emanet.reverse()
                Log.e("emanet",emanet.toString())
                val m= HashMap<String,Int>()
                liste.forEach { if(m[it]!=null) m[it]=m[it]!!+1 else m[it]=1 }
                result=HashMap()
                if (m.size>1) {
                    result = m.toList().sortedByDescending { (key, value) ->
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
//        database= FirebaseDatabase.getInstance()

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
                        if (emnt2.geriVermeTarihi != 0L && emnt2.okulId==okullId) {
                            emanet2.add(emnt2)
                            liste2.add(emnt2.ogrId.toString())
                            sayfaSayilari.add(emnt2.sayfaSayisi!!.toInt())
                            Log.e("kitapId",emnt2.kitapId.toString())

                        }
                    }
                }





//                emanet.reverse()
//                Log.e("emanet",emanet.toString())
                var m= HashMap<String,Int>()
                var n= HashMap<String,Int>()
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
//                m.forEach {
//                    var top=0
//                    var sayac=0
//                    for (a in liste2){
//                        if (it.key==a){
//                            top+=sayfaSayilari[sayac]
//                            sayac++
//                        }
//                    }
//                    n.put(it.key,top)
////                    topSayfa.add(top)
////                    ogrIdler.add(it.key)
//
//                }
                var sonListe:HashMap<String,Int>
                sonListe=HashMap()
                if (n.size>1) {
                    sonListe = n.toList().sortedByDescending { (key, value) ->
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
//        database= FirebaseDatabase.getInstance()
        emanet3=ArrayList()
        liste3= ArrayList()
        val ref3=database.getReference("emanet")
        ref3.addValueEventListener(@SuppressLint("SuspiciousIndentation")
        object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                emanet3.clear()
                for (s in snapshot.children){
                    val emnt=s.getValue(Emanets::class.java)

                    if (emnt!=null) {
                        emnt.id=s.key
                        if (emnt.geriVermeTarihi != 0L && emnt.okulId==okullId) {
                            emanet3.add(emnt)
                            liste3.add(emnt.isbn.toString())
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

    @SuppressLint("SuspiciousIndentation")
    private fun alinanKitaplariListele() {

//        database= FirebaseDatabase.getInstance()
        val currentUser=mAuth.currentUser

        val kullaniciId=currentUser?.uid
        var pushId=""
        val database2= FirebaseDatabase.getInstance()
        val myRef2=database2.getReference("ogrenciler")
        myRef2.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {


                for(o in snapshot.children){
                    val profil = o.getValue(Ogrenciler::class.java)
                    if (profil != null && profil.ogrId.toString()==kullaniciId) {
                       pushId=o.key.toString()



                    }
                }

            }


            override fun onCancelled(error: DatabaseError) {

            }

        })

        emanet4=ArrayList()
        val ref4=database.getReference("emanet")
        ref4.addValueEventListener(@SuppressLint("SuspiciousIndentation")
        object :ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                emanet4.clear()
                for (s in snapshot.children){
                    val emnt=s.getValue(Emanets::class.java)

                    if (emnt!=null) {
                        emnt.id=s.key
                        if (emnt.geriVermeTarihi == 0L) {



                            if (emnt.ogrId==pushId){
                                emanet4.add(emnt)
                            }
                        }
                    }
                }
                emanet4.reverse()
                binding.tvEnCok.text="Emanet Aldığınız Kitaplar("+emanet4.size+" adet)"
//                binding.rvOgrenci.setHasFixedSize(true)
//                binding.rvOgrenci.layoutManager= StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
//                adapter=KitapAlAdapter(this@KitapAlActivity,emanet)
//                binding.rvOgrenci.adapter=adapter
//                Log.e("emanet",emanet.toString())
                adapter4.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun alinmisKitaplariListele() {

//        database= FirebaseDatabase.getInstance()
        val currentUser=mAuth.currentUser

        val kullaniciId=currentUser?.uid
        var pushId=""
        val database2= FirebaseDatabase.getInstance()
        val myRef2=database2.getReference("ogrenciler")
        myRef2.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {


                for(o in snapshot.children){
                    val profil = o.getValue(Ogrenciler::class.java)
                    if (profil != null && profil.ogrId.toString()==kullaniciId) {
                        pushId=o.key.toString()



                    }
                }

            }


            override fun onCancelled(error: DatabaseError) {

            }

        })

        emanet5=ArrayList()
        val ref5=database.getReference("emanet")
        ref5.addValueEventListener(@SuppressLint("SuspiciousIndentation")
        object :ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                emanet5.clear()
                for (s in snapshot.children){
                    val emnt=s.getValue(Emanets::class.java)

                    if (emnt!=null) {
                        emnt.id=s.key
                        if (emnt.geriVermeTarihi != 0L) {



                            if (emnt.ogrId==pushId){
                                emanet5.add(emnt)
                            }
                        }
                    }
                }
                emanet5.reverse()
                binding.tvEnCok4.text="Okuduğunuz Kitaplar("+emanet5.size+" adet)"
//                binding.rvOgrenci.setHasFixedSize(true)
//                binding.rvOgrenci.layoutManager= StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
//                adapter=KitapAlAdapter(this@KitapAlActivity,emanet)
//                binding.rvOgrenci.adapter=adapter
//                Log.e("emanet",emanet.toString())
                adapter5.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}