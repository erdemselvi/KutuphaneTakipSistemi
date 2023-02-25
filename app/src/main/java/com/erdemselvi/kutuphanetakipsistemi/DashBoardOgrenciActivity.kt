package com.erdemselvi.kutuphanetakipsistemi

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.erdemselvi.kutuphanetakipsistemi.databinding.ActivityDashBoardOgrenciBinding
import com.erdemselvi.kutuphanetakipsistemi.model.Ogrenciler
import com.erdemselvi.kutuphanetakipsistemi.model.Ogrenciler2
import com.erdemselvi.kutuphanetakipsistemi.model.Okul2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class DashBoardOgrenciActivity : AppCompatActivity() {
    lateinit var binding: ActivityDashBoardOgrenciBinding
    private lateinit var mAuth: FirebaseAuth
    lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_dash_board)

        binding= ActivityDashBoardOgrenciBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        supportActionBar!!.hide()

        mAuth=FirebaseAuth.getInstance()
        val currentUser=mAuth.currentUser
        database= FirebaseDatabase.getInstance()
        ogrenciBilgileriBul()
//        binding.tvAdSoyad.text=currentUser!!.displayName
//        binding.tvEmail.text=currentUser.email
//        Picasso.with(this)
//            .load(R.drawable.gorevli)
//
//            .resize(160, 160)         //optional
//            .centerCrop()                        //optional
//            .into(binding.ivProfil)


        binding.logOutOgr.setOnClickListener{
            val prefences = getSharedPreferences("users", Context.MODE_PRIVATE)
            val editor = prefences.edit().clear().apply()
            Firebase.auth.signOut()
            val intent = Intent(this, GirisActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.btIstatistik.setOnClickListener {
            val intent = Intent(this, IstatistikActivity::class.java)
            startActivity(intent)
        }
        binding.cvKitapVer.setOnClickListener {
            val intent = Intent(this, KitapVerActivity::class.java)
            startActivity(intent)
        }
        binding.cvKitapAl.setOnClickListener {
            val intent = Intent(this, KitapAlActivity::class.java)
            startActivity(intent)
        }
        binding.cvKitapEkle.setOnClickListener {
            val intent = Intent(this, KitapEkleActivity::class.java)
            startActivity(intent)
        }
        binding.cvOgrenciEkle.setOnClickListener {
            val intent = Intent(this, OgrenciEkleActivity::class.java)
            startActivity(intent)
        }
        binding.cvKitapListele.setOnClickListener {
            val intent = Intent(this, KitapListeleActivity::class.java)
            startActivity(intent)
        }
        binding.cvOgrenciListele.setOnClickListener {
            val intent = Intent(this, OgrenciListeleActivity::class.java)
            startActivity(intent)
        }
    }

    private fun ogrenciBilgileriBul() {
        var okulId:String?=""
        var ogrUid:String?=""
        val currentUser=mAuth.currentUser

        val donusenKelime=currentUser?.uid

        val myRef=database.getReference("ogrenciler")
        myRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                for(s in snapshot.children){
                    val profil = s.getValue(Ogrenciler::class.java)
                    if (profil!=null && profil.ogrId.toString()==donusenKelime){
 //                       okulId=profil.okulId
                        ogrUid= s.key!!
                        //binding.tvOgrId.text=s.key
                        binding.tvAdSoyad.text="Merhaba "+profil.ad+" "+profil.soyad
                        binding.tvTelNo.text=profil.telNo
//                        Log.e("öğrenciId",binding.tvOgrId.text.toString())
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        val prefences2 = getSharedPreferences("users", Context.MODE_PRIVATE)
        val okullId = prefences2.getString("okulId","yok")
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
                    binding.tvOkul.text=ok.okulAdi
//                        }

//                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


}