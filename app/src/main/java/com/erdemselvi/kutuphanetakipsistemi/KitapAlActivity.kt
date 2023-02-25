package com.erdemselvi.kutuphanetakipsistemi

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.erdemselvi.kutuphanetakipsistemi.adapter.KitapAlAdapter
import com.erdemselvi.kutuphanetakipsistemi.databinding.ActivityKitapAlBinding
import com.erdemselvi.kutuphanetakipsistemi.model.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.zxing.client.android.Intents
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions

class KitapAlActivity : AppCompatActivity() {

    companion object {
        const val NEW_ITEM_ACTIVITY_REQUEST_CODE = 1
    }

    var qrDeger:String=""
//    lateinit var emanetDizi:ArrayList<Emanet>
    lateinit var database: FirebaseDatabase
//    private lateinit var profill:ArrayList<Ogrenciler>

    private lateinit var emanet:ArrayList<Emanets>
    private lateinit var ogrenci:ArrayList<Ogrenciler2>
    private lateinit var kitap:ArrayList<Kitaplar2>
//    private lateinit var yeniListe:ArrayList<Emanets>

    private lateinit var kitapAl:ArrayList<KitapAl>
//    lateinit var k:KitapAl
    lateinit var adapter:KitapAlAdapter

    private val qrLauncher = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents == null) {
            val originalIntent = result.originalIntent
            if (originalIntent == null) {
                Log.d("KitapTeslimActivity", "Tarama İptal Edildi")
                Toast.makeText(this, "İptal Edildi", Toast.LENGTH_LONG).show()
            } else if (originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                Log.d("KitapAlActivity","Eksik kamera izni nedeniyle iptal edilen tarama")
                Toast.makeText(this,"Eksik kamera izni nedeniyle iptal edildi",Toast.LENGTH_LONG).show()
            }
        } else {
            qrDeger=result.contents
            if (qrDeger!="") {

                Toast.makeText(applicationContext, "QRCode başarıyla okundu.", Toast.LENGTH_LONG).show()
//                binding.etOgrenciAdi.setText(qrDeger)
//                binding.etOgrId.setText(qrDeger)
                database= FirebaseDatabase.getInstance()
                val myRef=database.getReference("ogrenciler")
                myRef.addValueEventListener(object : ValueEventListener {
                    @SuppressLint("SetTextI18n")
                    override fun onDataChange(snapshot: DataSnapshot) {


                        for(s in snapshot.children){
                            val profil = s.getValue(Ogrenciler::class.java)
                            if (profil != null && profil.ogrId.toString()==qrDeger) {
                                binding.tvOgrId.text = s.key
                                binding.tvOgrenciAdi.text = profil.ad+" "+profil.soyad
                                binding.tvOkulNo.text = profil.no.toString()
                                binding.tvTelNo.text = profil.telNo

                                ogrenciKitapAlBul(binding.tvOgrId.text.toString())
                                binding.btIptal.setOnClickListener {
                                    alinanKitaplariListele()
                                    emanet.reverse()
                                    binding.btIptal.visibility=View.INVISIBLE
                                    binding.tvOgrenciAdi.text="Öğrencinin Adı Soyadı"
                                    binding.tvTelNo.text="Telefon Numarası"
                                    binding.tvOkulNo.text="Okul No"

                                }
                                binding.tvTelNo.setOnClickListener {
                                    checkPermission()
                                    callPhone()
                                }

                            }
                        }

                    }


                    override fun onCancelled(error: DatabaseError) {

                    }

                })




            }
            Log.d("KitapAlActivity", "Tarandı")
        }
    }


    lateinit var binding: ActivityKitapAlBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kitap_al)
        binding=ActivityKitapAlBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        supportActionBar?.title="Kitap Teslim Alma Ekranı"

        emanet=ArrayList()
        ogrenci= ArrayList()
        kitap= ArrayList()
        kitapAl= ArrayList()
        alinanKitaplariListele()
        emanet.reverse() // Son alınan kitaplar üstte gözüksün diye dizi ters çevriliyor
        binding.rvOgrenci.setHasFixedSize(true)
        binding.rvOgrenci.layoutManager= StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        adapter=KitapAlAdapter(this,emanet)
        binding.rvOgrenci.adapter=adapter


        binding.btQrCodeTara.setOnClickListener {
            val options= ScanOptions()
            options.setOrientationLocked(true)
                .setTimeout(8000)

            qrDeger= qrLauncher.launch(options).toString()
        }
        binding.btOgrenciBul.setOnClickListener {
            val intent = Intent(this, OgrenciBulActivity::class.java)
            startActivityForResult(intent, NEW_ITEM_ACTIVITY_REQUEST_CODE)
        }

    }

    @SuppressLint("SuspiciousIndentation")
    private fun alinanKitaplariListele() {
//        database= FirebaseDatabase.getInstance()
//        emanet=ArrayList()
//        kitapAl= ArrayList()
////        var emanetId:String?=""
////        var ad:String?=""
////        var soyad:String?=""
////        var no:Int?=0
////        var okulId:Int?=0
////        var telNo:String?=""
////        var kitapAdi:String?=""
////        var yazarAdi:String?=""
////        var isbn:String?=""
////        var resimUrl:String?=""
////        val ktpAl=HashMap<String,Any>()
//        k=KitapAl()
////        var sayac=0
//        val ref=database.getReference("emanet")
//        ref.addValueEventListener(@SuppressLint("SuspiciousIndentation")
//        object :ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                kitapAl.clear()
//                for (s in snapshot.children){
//                    k=KitapAl()
//                    val emnt=s.getValue(Emanet2::class.java)
//                    if (emnt!=null) {
//                        if (emnt.geriVermeTarihi == 0L) {
////                            kitapAl[sayac].emanetId=s.key
////                            emanetId=s.key
//                            k.emanetId=s.key
//                            Log.e("emnt.kitapId",emnt.kitapId.toString())
////                            ktpAl.put("emanetId",s.key.toString())
//                            //Kitap Id ile Kitap bilgilerine ulaşılıyor
//                            val refKitap=database.getReference("kitaplar").child(emnt.kitapId.toString())
//                            refKitap.addValueEventListener(object : ValueEventListener {
//                                override fun onDataChange(snapshot: DataSnapshot) {
//                                    val kitap=snapshot.getValue(Kitaplar2::class.java)
//
//                                            if (kitap!=null) {
//                                                kitap.id = s.key
//
//                                                k.kitapAdi=kitap.kitapAdi
//                                                k.yazarAdi=kitap.yazarAdi
//                                                k.isbn=kitap.isbn
//                                                k.resimUrl=kitap.resimUrl
////                                                    kitapAdi=kitap.kitapAdi
////                                                    isbn=kitap.isbn
////                                                    yazarAdi=kitap.yazarAdi
////                                                    resimUrl=kitap.resimUrl
////                                                    kitapAl[sayac].kitapAdi=kitap.kitapAdi
////                                                    kitapAl[sayac].isbn=kitap.isbn
////                                                    kitapAl[sayac].yazarAdi=kitap.yazarAdi
////                                                    kitapAl[sayac].resimUrl=kitap.resimUrl
////                                                    ktpAl.put("isbn",kitap.isbn.toString())
////                                                    ktpAl.put("kitapAdi",kitap.kitapAdi.toString())
////                                                    ktpAl.put("yazarAdi",kitap.yazarAdi.toString())
////                                                    ktpAl.put("resimUrl",kitap.resimUrl.toString())
//                                                val refOgr=database.getReference("ogrenciler").child(emnt.ogrId.toString())
//                                                refOgr.addValueEventListener(object :ValueEventListener{
//                                                    @SuppressLint("SetTextI18n")
//                                                    override fun onDataChange(snapshot: DataSnapshot) {
//                                                        val ogrenci=snapshot.getValue(Ogrenciler2::class.java)
//
//                                                        if (ogrenci!=null){
//                                                            ogrenci.Id=s.key
//                                                            k.ad=ogrenci.ad
//                                                            k.soyad=ogrenci.soyad
//                                                            k.no=ogrenci.no
//                                                            k.okulId=ogrenci.okulId
//                                                            k.telNo=ogrenci.telNo
////                                                    ad=ogrenci.ad
////                                                    soyad=ogrenci.soyad
////                                                    no=ogrenci.no
////                                                    okulId=ogrenci.okulId
////                                                    telNo=ogrenci.telNo
////
////                                                    kitapAl[sayac].ad=ogrenci.ad
////                                                    kitapAl[sayac].soyad=ogrenci.soyad
////                                                    kitapAl[sayac].no=ogrenci.no
////                                                    kitapAl[sayac].okulId=ogrenci.okulId
////                                                    kitapAl[sayac].telNo=ogrenci.telNo
////                                                    ktpAl.put("ad",ogrenci.ad.toString())
////                                                    ktpAl.put("soyad",ogrenci.soyad.toString())
////                                                    ktpAl.put("no",ogrenci.no!!.toInt())
////                                                    ktpAl.put("okulId",ogrenci.okulId!!.toInt())
////                                                    ktpAl.put("telNo",ogrenci.telNo.toString())
//                                                            Log.e("k dizisi",k.toString())
//                                                            kitapAl.add(k)
//                                                            adapter.notifyDataSetChanged()
//                                                            Log.e("kitapAl2",kitapAl.toString())
//                                                        }
//
//                                                    }
//
//
//
//                                                    override fun onCancelled(error: DatabaseError) {
//                                                        TODO("Not yet implemented")
//                                                    }
//
//                                                })
//                                                }
//
//
//                                            }
//
//
//
//                                override fun onCancelled(error: DatabaseError) {
//                                    TODO("Not yet implemented")
//                                }
//
//                            })
//
//                            //Ogrenci Id ile Öğrenci bilgilerine ulaşılıyor
//
////                            val k=KitapAl(emanetId,ad, no,okulId,soyad,telNo,isbn,kitapAdi,yazarAdi,resimUrl)
//
////                            emanet.add(emnt)
////                            kitapAl.add(ktpAl as KitapAl)
////                            sayac++
//                        }
//                    }
//                }
////                kitapAl.add(k)
//                Log.e("kitapAl2",kitapAl.toString())
//
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//
//        })
        database= FirebaseDatabase.getInstance()
        emanet=ArrayList()
        val ref=database.getReference("emanet")
            ref.addValueEventListener(@SuppressLint("SuspiciousIndentation")
            object :ValueEventListener{
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    emanet.clear()
                    for (s in snapshot.children){
                        val emnt=s.getValue(Emanets::class.java)

                        if (emnt!=null) {
                            emnt.id=s.key
                            if (emnt.geriVermeTarihi == 0L) {
                                emanet.add(emnt)
                            }
                        }
                    }
                    emanet.reverse()
                    binding.rvOgrenci.setHasFixedSize(true)
                    binding.rvOgrenci.layoutManager= StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                    adapter=KitapAlAdapter(this@KitapAlActivity,emanet)
                    binding.rvOgrenci.adapter=adapter
                    Log.e("emanet",emanet.toString())
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK){
            binding.btIptal.visibility=View.VISIBLE
            binding.tvOgrenciAdi.text=data?.getStringExtra("ad")+" "+data?.getStringExtra("soyad")
            binding.tvTelNo.text=data?.getStringExtra("telNo")
            binding.tvOkulNo.text=data?.getIntExtra("no",1).toString()
//            binding.tvOgrenciBilgisi.text=data?.getStringExtra("id")+" "+data?.getStringExtra("ad")+" "+data?.getStringExtra("soyad")
            ogrenciKitapAlBul(data?.getStringExtra("id").toString())
//            adapter.notifyDataSetChanged()
            binding.btIptal.setOnClickListener {
                alinanKitaplariListele()
                emanet.reverse()
                binding.btIptal.visibility=View.INVISIBLE
                binding.tvOgrenciAdi.text="Öğrencinin Adı Soyadı"
                binding.tvTelNo.text="Telefon Numarası"
                binding.tvOkulNo.text="Okul No"

            }
            binding.tvTelNo.setOnClickListener {
                checkPermission()
                callPhone()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun ogrenciKitapAlBul(ogrId: String) {
        database= FirebaseDatabase.getInstance()
//        emanet=ArrayList()
        val ref=database.getReference("emanet")
        ref.addValueEventListener(@SuppressLint("SuspiciousIndentation")
        object :ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                emanet.clear()
                for (s in snapshot.children){
                    val emnt=s.getValue(Emanets::class.java)
                    if (emnt!=null) {
                        if (emnt.geriVermeTarihi == 0L && emnt.ogrId==ogrId) {
                            emnt.id=s.key
                            emanet.add(emnt)
                        }
                    }
                }

                Log.e("emanetÖğrenciBul",emanet.toString())
                adapter.notifyDataSetChanged()
//                binding.rvOgrenci.removeAllViews()
//                binding.rvOgrenci.setHasFixedSize(true)
//                binding.rvOgrenci.adapter=adapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//       menuInflater.inflate(R.menu.bul_menu,menu)
//
//        val item = menu.findItem(R.id.action_ara)
//        val searchView = item?.actionView as SearchView
//        searchView.setOnQueryTextListener(this)
//
//        return super.onCreateOptionsMenu(menu)
//
//    }

//    override fun onQueryTextSubmit(query: String): Boolean {
//        aramaYap(query)
//        Log.e("Gönderilen arama",query)
//        return false
//    }
//
//    override fun onQueryTextChange(newText: String): Boolean {
//        aramaYap(newText)
//        Log.e("Harf girdikçe",newText)
//        return false
//    }

//    @SuppressLint("NotifyDataSetChanged")
//    private fun aramaYap(newText: String) {
//        var kontrol=0
////        database= FirebaseDatabase.getInstance()
////        emanet=ArrayList()
////        val ref=database.getReference("emanet")
////        ref.addValueEventListener(@SuppressLint("SuspiciousIndentation")
////        object :ValueEventListener{
////            override fun onDataChange(snapshot: DataSnapshot) {
////                emanet.clear()
////                for (s in snapshot.children){
////                    val emnt=s.getValue(Emanets::class.java)
////                    if (emnt!=null) {
////                        emnt.id=s.key
////                        if (emnt.geriVermeTarihi == 0L) {
////                            /////////kitapId ile Kitap isimlerini bulma
////                            val refKitap=database.getReference("kitaplar").child(emnt.kitapId.toString())
////                            refKitap.addValueEventListener(object : ValueEventListener {
////                                override fun onDataChange(snapshot: DataSnapshot) {
////                                    val kitap=snapshot.getValue(Kitaplar2::class.java)
//////                                    for (s in snapshot.children){
//////                                        if (s!=null){
//////
//////                                            val kitap=s.getValue(Kitaplar2::class.java)
////                                            if (kitap!=null) {
////                                                if (kitap.kitapAdi!!.contains(newText) || kitap.yazarAdi.equals(newText)){
////                                                    kitap.id = s.key
////                                                    kontrol++
////          //                                          emanet.add(emnt)
////                                                }
////
//////                                                if (kitap.id==emanet[position].kitapId){
//////                                                    holder.kitapAdi.text=kitap.kitapAdi
//////                                                    holder.yazarAdi.text=kitap.yazarAdi
//////                                                }
//////                                            }
//////                                        }
////                                    }
////                                    adapter.notifyDataSetChanged()
////                                }
////
////                                override fun onCancelled(error: DatabaseError) {
////                                    TODO("Not yet implemented")
////                                }
////
////                            })
////
////                            //////////////////
////                            Log.e("newText1",newText)
////                            ////////ogrId ile Öğrenci bilgileri bulunuyor
////                            val refOgr=database.getReference("ogrenciler").child(emnt.ogrId.toString())
////                            refOgr.addValueEventListener(object :ValueEventListener{
////                                @SuppressLint("SetTextI18n")
////                                override fun onDataChange(snapshot: DataSnapshot) {
////                                    val ogrenci=snapshot.getValue(Ogrenciler2::class.java)
//////                                    for (s in snapshot.children){
//////                                        if (s!=null){
//////                                            val ogrenci=s.getValue(Ogrenciler2::class.java)
////                                            if (ogrenci!=null){
////                                                if (ogrenci.ad.equals(newText)||ogrenci.soyad.equals(newText)|| ogrenci.no!!.toString().equals(newText)
////                                                    ||ogrenci.telNo.equals(newText)){
////                                                    kontrol++
////                                                    ogrenci.Id=s.key
////                                                    emanet.add(emnt)
////                                                    Log.e("newText2",newText)
////                                                }
////
//////                                                if (ogrenci.Id==emanet[position].ogrId){
//////                                                    holder.ogrAdi.text=ogrenci.ad+" "+ogrenci.soyad+" "+ogrenci.no
//////                                                }
//////                                            }
//////                                        }
////                                    }
////                                    Log.e("emanetOGr",emanet.toString())
////                                    adapter.notifyDataSetChanged()
////                                }
////
////                                override fun onCancelled(error: DatabaseError) {
////                                    TODO("Not yet implemented")
////                                }
////
////                            })
////                            /////////////////////////////////
////
////
////                        }
////                    }
////                }
////                Log.e("emanet",emanet.toString())
////                adapter.notifyDataSetChanged()
////            }
////
////            override fun onCancelled(error: DatabaseError) {
////                TODO("Not yet implemented")
////            }
////
////        })
//
//
//        database= FirebaseDatabase.getInstance()
////        emanet=ArrayList()
//
////        val ktpAl=HashMap<String,Any>()
//
//        var sayac=0
//        val ref=database.getReference("emanet")
//        ref.addValueEventListener(@SuppressLint("SuspiciousIndentation")
//        object :ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                emanet.clear()
//                for (s in snapshot.children){
//                    var sayac2=0
//                    val emnt=s.getValue(Emanets::class.java)
//                    if (emnt!=null) {
//                        if (emnt.geriVermeTarihi == 0L) {
//                            emnt.id=s.key
////                            kitapAl[sayac].emanetId=s.key
////                            ktpAl.put("emanetId",s.key.toString())
//                            //Kitap Id ile Kitap bilgilerine ulaşılıyor
//
//
//                            //Ogrenci Id ile Öğrenci bilgilerine ulaşılıyor
//
////                            emanet.add(emnt)
////                            kitapAl.add(ktpAl as KitapAl)
////                            sayac++
//                            val refOgr=database.getReference("ogrenciler").child(emnt.ogrId.toString())
//                            refOgr.addValueEventListener(object :ValueEventListener{
//                                @SuppressLint("SetTextI18n")
//                                override fun onDataChange(snapshot: DataSnapshot) {
//
//                                    val ogr=snapshot.getValue(Ogrenciler2::class.java)
//
////                                    for (s in snapshot.children){
////                                        if (s!=null){
////                                            val ogr=s.getValue(Ogrenciler2::class.java)
//                                            if (ogr!=null) {
//                                                if (ogr.ad!!.contains(newText) || ogr.soyad!!.contains(
//                                                        newText
//                                                    ) || ogr.no!!.toString().contains(newText)
//                                                    || ogr.telNo!!.contains(newText)
//                                                ) {
//                                                    ogr.Id = s.key
////                                                if (ogr.Id==emnt.ogrId){
//                                                    ogrenci.add(ogr)
//                                                    sayac2++
//
////                                                    kitapAl[sayac].ad=ogrenci.ad
////                                                    kitapAl[sayac].soyad=ogrenci.soyad
////                                                    kitapAl[sayac].no=ogrenci.no
////                                                    kitapAl[sayac].okulId=ogrenci.okulId
////                                                    kitapAl[sayac].telNo=ogrenci.telNo
////                                                    ktpAl.put("ad",ogrenci.ad.toString())
////                                                    ktpAl.put("soyad",ogrenci.soyad.toString())
////                                                    ktpAl.put("no",ogrenci.no!!.toInt())
////                                                    ktpAl.put("okulId",ogrenci.okulId!!.toInt())
////                                                    ktpAl.put("telNo",ogrenci.telNo.toString())
//                                                }
//                                            }
////                                            }
////                                        }
////                                    }
//                                }
//
//                                override fun onCancelled(error: DatabaseError) {
//                                    TODO("Not yet implemented")
//                                }
//
//                            })
//
//                            val refKitap=database.getReference("kitaplar").child(emnt.kitapId.toString())
//                            refKitap.addValueEventListener(object :ValueEventListener{
//                                override fun onDataChange(snapshot: DataSnapshot) {
//
//                                    val ktp=snapshot.getValue(Kitaplar2::class.java)
//
////                                    for (s in snapshot.children){
////                                        if (s!=null){
////
////                                            val kitap=s.getValue(Kitaplar2::class.java)
//                                            if (ktp!=null) {
//                                                if (ktp.kitapAdi!!.contains(newText) || ktp.yazarAdi!!.contains(
//                                                        newText
//                                                    )
//                                                ) {
//                                                    ktp.id = s.key
//                                                    kitap.add(ktp)
//                                                    sayac2++
////                                                if (kitap.id==emnt.kitapId){
//
////                                                    kitapAl[sayac].kitapAdi=kitap.kitapAdi
////                                                    kitapAl[sayac].isbn=kitap.isbn
////                                                    kitapAl[sayac].yazarAdi=kitap.yazarAdi
////                                                    kitapAl[sayac].resimUrl=kitap.resimUrl
////                                                    ktpAl.put("isbn",kitap.isbn.toString())
////                                                    ktpAl.put("kitapAdi",kitap.kitapAdi.toString())
////                                                    ktpAl.put("yazarAdi",kitap.yazarAdi.toString())
////                                                    ktpAl.put("resimUrl",kitap.resimUrl.toString())
//
//                                                }
//                                            }
////                                            }
////                                        }
////                                    }
//                                }
//
//                                override fun onCancelled(error: DatabaseError) {
//                                    TODO("Not yet implemented")
//                                }
//
//                            })
//                        }
//                        if (sayac2!=0){
//                            emanet.add(emnt)
//                        }
//
//                    }
//
//                }
//                Log.e("emanetAra",emanet.toString())
//                Log.e("ogrenci",ogrenci.toString())
//                Log.e("kitap",kitap.toString())
////                adapter.notifyDataSetChanged()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//
//        })
//
//
//
//
//
//
//        adapter.notifyDataSetChanged()
//    }
    fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CALL_PHONE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    42)
            }
        } else {
            // Permission has already been granted
            callPhone()
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 42) {
            // If request is cancelled, the result arrays are empty.
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // permission was granted, yay!
                callPhone()
            } else {
                // permission denied, boo! Disable the
                // functionality
            }
            return
        }
    }

    private fun callPhone() {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + binding.tvTelNo.text))
        startActivity(intent)
    }
}