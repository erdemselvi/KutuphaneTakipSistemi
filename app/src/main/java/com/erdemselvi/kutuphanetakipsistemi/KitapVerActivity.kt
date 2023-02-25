package com.erdemselvi.kutuphanetakipsistemi

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.erdemselvi.kutuphanetakipsistemi.databinding.ActivityKitapVerBinding
import com.erdemselvi.kutuphanetakipsistemi.model.Emanet2
import com.erdemselvi.kutuphanetakipsistemi.model.Emanet3
import com.erdemselvi.kutuphanetakipsistemi.model.Ogrenciler
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.zxing.client.android.Intents
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.squareup.picasso.Picasso
import java.util.*

class KitapVerActivity : AppCompatActivity() {

    var ogrId="boş"
    var kitapId="boş"
    var teslimSuresi=30
    var sayfaSayisi=0
    lateinit var binding:ActivityKitapVerBinding
    lateinit var database: FirebaseDatabase
    lateinit var okulId:String
   ///////////////QR Kod okuma Kodları
    var qrDeger:String=""

    @SuppressLint("SuspiciousIndentation")
    private val qrLauncher = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents == null) {
            val originalIntent = result.originalIntent
            if (originalIntent == null) {
                Log.d("KitapVerActivity", "Tarama İptal Edildi")
                Toast.makeText(this, "İptal Edildi", Toast.LENGTH_LONG).show()
            } else if (originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                Log.d("KitapVerActivity","Eksik kamera izni nedeniyle iptal edilen tarama")
                Toast.makeText(this,"Eksik kamera izni nedeniyle iptal edildi",Toast.LENGTH_LONG).show()
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
            Log.d("KitapVerActivity", "Tarandı")
        }

    ///////////////////////////QR Kod Okuma Kodları

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_kitap_ver)
        binding= ActivityKitapVerBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        supportActionBar?.title="Kitap Verme Ekranı"
        val prefences = getSharedPreferences("users", Context.MODE_PRIVATE)
        okulId = prefences.getString("okulId","yok").toString()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.sbSure.min=1
        }
        binding.sbSure.setOnSeekBarChangeListener(object :OnSeekBarChangeListener{
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvSeekBar.text="Teslim Süresi: $progress Gün"
                teslimSuresi=progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })


        binding.btQrCodeTara.setOnClickListener {
            val options= ScanOptions()
            options.setOrientationLocked(false)
                .setTimeout(8000)

            qrDeger= qrLauncher.launch(options).toString()
        }
        binding.btOgrenciBul.setOnClickListener {
            val intent = Intent(this, OgrenciBulActivity::class.java)
            startActivityForResult(intent, 1)
        }
        binding.btKitapBul.setOnClickListener {
            val prefences = getSharedPreferences("users", Context.MODE_PRIVATE)
            val yetki = prefences.getString("yetki","öğretmen")
            val intent = Intent(this, KitapBulActivity::class.java)
            if (yetki=="öğretmen" || yetki=="öğrenci"){
                intent.putExtra("gorevli","okulgörevlisi")
            }
            startActivityForResult(intent, 2)
        }
        binding.btKitapVer.setOnClickListener {
//            if (this::ogrId.isInitialized && this::kitapId.isInitialized){
            if(binding.tvOgrId.text!="boş" && kitapId!="boş"){
                val emanetTarihi = Date().time
                val emanet= Emanet3(kitapId,binding.tvIsbn.text.toString(),binding.tvOgrId.text.toString(),emanetTarihi,0L,teslimSuresi,sayfaSayisi,okulId)
                database= FirebaseDatabase.getInstance()
                val ref=database.getReference("emanet")

                val alertVer= AlertDialog.Builder(this)
                alertVer.setTitle("Kitap Teslim Verme İşlemi")
                    .setMessage("Kitabı Teslim Etmek İstiyormusunuz?")
                    .setIcon(R.drawable.ic_baseline_menu_book_24)
                    .setPositiveButton("Ver") { _, _ ->
                        ref.push().setValue(emanet)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Toast.makeText(this,"Kitap Başarı ile Verildi. Öğrenciye Teslim edebilirsiniz.", Toast.LENGTH_LONG).show()
                                   kitapId ="boş"


                                    binding.tvKitapAdi.text="Kitap Adı"
                                    binding.tvYazar.text="Yazar"
                                    binding.tvOzet.text="Özet"
                                    binding.tvIsbn.text="ISBN"
                                    val resimUrl=""

                                } else {
                                    Toast.makeText(this,"HATA!! Kitap Verilemedi.!!", Toast.LENGTH_LONG).show()
                                }
                            }
                    }
                    .setNegativeButton("iptal"){_, _ -> }
                    .create().show()
            }
            else{
                Toast.makeText(this,"Lütfen Öğrenci veya Kitap seçiniz",Toast.LENGTH_SHORT).show()
            }
        }

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
        if (resultCode==Activity.RESULT_OK && requestCode==2){

            kitapId= data?.getStringExtra("id").toString()
            binding.tvKitapAdi.text=data?.getStringExtra("kitapAdi")
            binding.tvYazar.text=data?.getStringExtra("yazarAdi")
            binding.tvOzet.text=data?.getStringExtra("ozet")
            binding.tvIsbn.text=data?.getStringExtra("ISBN")
            val resimUrl=data?.getStringExtra("resimUrl")
            sayfaSayisi= data?.getIntExtra("sayfaSayisi",0)!!
            Picasso.with(this)
                .load(resimUrl)

                .resize(128, 187)         //optional
                .centerCrop()                        //optional
                .into(binding.ivResim)

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