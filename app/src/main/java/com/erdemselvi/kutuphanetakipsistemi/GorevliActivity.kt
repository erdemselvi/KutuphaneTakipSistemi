package com.erdemselvi.kutuphanetakipsistemi

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.erdemselvi.kutuphanetakipsistemi.adapter.GorevliAdapter
import com.erdemselvi.kutuphanetakipsistemi.databinding.ActivityGorevliBinding
import com.erdemselvi.kutuphanetakipsistemi.model.Gorevli
import com.erdemselvi.kutuphanetakipsistemi.model.Gorevli2
import com.erdemselvi.kutuphanetakipsistemi.model.Ogrenciler
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.zxing.client.android.Intents
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import java.util.*
import kotlin.collections.ArrayList

class GorevliActivity : AppCompatActivity() {

    lateinit var binding: ActivityGorevliBinding
    lateinit var database: FirebaseDatabase
    lateinit var adapter: GorevliAdapter
    lateinit var gorevli:ArrayList<Gorevli2>
    lateinit var gorev:ArrayList<Gorevli>
    var ogrId="boş"
    ///////////////QR Kod okuma Kodları
    var qrDeger:String=""

    @SuppressLint("SuspiciousIndentation")
    private val qrLauncher = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents == null) {
            val originalIntent = result.originalIntent
            if (originalIntent == null) {
                Log.d("GorevliActivity", "Tarama İptal Edildi")
                Toast.makeText(this, "İptal Edildi", Toast.LENGTH_LONG).show()
            } else if (originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                Log.d("GorevliActivity","Eksik kamera izni nedeniyle iptal edilen tarama")
                Toast.makeText(this,"Eksik kamera izni nedeniyle iptal edildi", Toast.LENGTH_LONG).show()
            }
        } else {
            qrDeger=result.contents

            Toast.makeText(applicationContext, "QRCode başarıyla okundu.", Toast.LENGTH_LONG).show()
            database= FirebaseDatabase.getInstance()
            val myRef=database.getReference("ogrenciler")
            myRef.addValueEventListener(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(s in snapshot.children){
                        val profil = s.getValue(Ogrenciler::class.java)
                        if (profil!=null && profil.ogrId.toString()==qrDeger){
                            binding.tvOgrId.text=s.key
                            binding.tvOgrAdi.text=profil.ad
                            binding.tvOgrSoyad.text=profil.soyad
                            binding.tvOkulNo.text=profil.no.toString()
                            binding.tvTelNo.text=profil.telNo

                        }
                    }
                    binding.tvTelNo.setOnClickListener {

                        checkPermission()
                        callPhone()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
        Log.d("GorevliActivity", "Tarandı")
    }

    ///////////////////////////QR Kod Okuma Kodları

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_gorevli),
        binding=ActivityGorevliBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        supportActionBar?.title="Görevli Ekranı"
        gorevli= ArrayList()
        gorev= ArrayList()

        gorevlileriListele()
        adapter= GorevliAdapter(this,gorevli)
        binding.rvGorevli.hasFixedSize()
        binding.rvGorevli.layoutManager=StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL)
        binding.rvGorevli.adapter=adapter

        binding.btOgrenciBul.setOnClickListener {
            val prefences = getSharedPreferences("users", Context.MODE_PRIVATE)
            val yetki = prefences.getString("yetki","öğretmen")
            val intent = Intent(this, OgrenciBulActivity::class.java)
            if (yetki=="öğretmen"){
                intent.putExtra("gorevli","okulgörevlisi")
            }

            startActivityForResult(intent, 1)
        }
        binding.btQrCodeTara.setOnClickListener {
            val options= ScanOptions()
            options.setOrientationLocked(false)
                .setTimeout(8000)

            qrDeger= qrLauncher.launch(options).toString()
        }
        binding.btGorevVer.setOnClickListener {
            if(binding.tvOgrId.text!="boş") {
                var sayac=0
                val ref = FirebaseDatabase.getInstance().getReference("gorevliler")
                ref.addValueEventListener(object:ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (s in snapshot.children){
                            val gorev=s.getValue(Gorevli::class.java)
                            if (gorev!=null && gorev.gorevliId==binding.tvOgrId.text.toString() && gorev.bitisTarihi==0L){
                                sayac++
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
                if (sayac==0){
                    val prefences = getSharedPreferences("users", Context.MODE_PRIVATE)
                    var okulId = prefences.getString("okulId","yok")
                    val gorevTarihi = Date().time
                    val gorev = Gorevli(binding.tvOgrId.text.toString(), gorevTarihi, 0L,okulId)
                    val dbEkle = FirebaseDatabase.getInstance()
                    val refEkle = dbEkle.getReference("gorevliler")
                    refEkle.push().setValue(gorev)
                        .addOnCompleteListener {
                            if (it.isSuccessful){
                                binding.tvOgrId.text="boş"
                                binding.tvOgrAdi.text="Ad"
                                binding.tvOgrSoyad.text="Soyad"
                                binding.tvOkulNo.text="No"
                                binding.tvTelNo.text="Telefon Numarası"
                            }
                        }
                }
                else{
                    Toast.makeText(this,"Bu öğrenci zaten daha önce görevli olarak eklenmiş",Toast.LENGTH_LONG).show()
                }

            }
        }
    }

    private fun gorevlileriListele() {
        val prefences = getSharedPreferences("users", Context.MODE_PRIVATE)
        var yetki = prefences.getString("yetki","öğretmen")
        var okulId = prefences.getString("okulId","yok")

        database= FirebaseDatabase.getInstance()
        val ref=database.getReference("gorevliler")
        ref.addValueEventListener(@SuppressLint("SuspiciousIndentation")
        object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                gorevli.clear()
                for (s in snapshot.children){
                    val grv=s.getValue(Gorevli2::class.java)

                    if (grv!=null && grv.bitisTarihi==0L) {
                        if (yetki=="öğretmen"){
                            if (grv.okulId==okulId){
                                grv.id=s.key

                                gorevli.add(grv)
                            }
                        }
                        else{
                            grv.id=s.key

                            gorevli.add(grv)
                        }


                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }
    @Deprecated("Deprecated in Java")
    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode==1){
            binding.tvOgrId.text=data?.getStringExtra("id").toString()
            ogrId= data?.getStringExtra("id").toString()
            binding.tvOgrAdi.text=data?.getStringExtra("ad")
            binding.tvOgrSoyad.text=data?.getStringExtra("soyad")
            binding.tvOkulNo.text=data?.getIntExtra("no",1).toString()
            binding.tvTelNo.text=data?.getStringExtra("telNo")
            binding.tvTelNo.setOnClickListener {

                checkPermission()
                callPhone()
            }

        }

        super.onActivityResult(requestCode, resultCode, data)
    }
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