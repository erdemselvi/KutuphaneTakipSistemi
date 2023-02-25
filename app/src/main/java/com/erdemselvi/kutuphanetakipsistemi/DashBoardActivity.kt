package com.erdemselvi.kutuphanetakipsistemi

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.erdemselvi.kutuphanetakipsistemi.databinding.ActivityDashBoardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class DashBoardActivity : AppCompatActivity() {
    lateinit var binding:ActivityDashBoardBinding
    private lateinit var mAuth: FirebaseAuth
    var yetki:String?=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_dash_board)
        binding= ActivityDashBoardBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        supportActionBar!!.hide()
        val prefences = getSharedPreferences("users", Context.MODE_PRIVATE)
        yetki = prefences.getString("yetki","öğretmen")

        mAuth=FirebaseAuth.getInstance()
        val currentUser=mAuth.currentUser
        binding.tvAdSoyad.text=currentUser!!.displayName
        binding.tvEmail.text=currentUser.email
        Picasso.with(this)
            .load(currentUser.photoUrl)

            .resize(160, 160)         //optional
            .centerCrop()                        //optional
            .into(binding.ivProfil)

        binding.logOut.setOnClickListener{
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
        if(yetki=="öğretmen"){
            binding.cvOkulListele.visibility= View.GONE
        }
        binding.cvOkulListele.setOnClickListener {
            val intent = Intent(this, OkulActivity::class.java)
            startActivity(intent)
        }
        binding.cvGorevliListele.setOnClickListener {
            val intent = Intent(this, GorevliActivity::class.java)
            startActivity(intent)
        }
        if(yetki=="öğretmen"){
            binding.cvOgretmenListele.visibility= View.GONE
        }
        binding.cvOgretmenListele.setOnClickListener {
            val intent = Intent(this, YoneticiActivity::class.java)
            startActivity(intent)
        }
    }
}