package com.erdemselvi.kutuphanetakipsistemi

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.erdemselvi.kutuphanetakipsistemi.databinding.ActivityProfilBinding
import com.erdemselvi.kutuphanetakipsistemi.model.Ogrenciler
import com.erdemselvi.kutuphanetakipsistemi.model.Okul2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList

@Suppress("NAME_SHADOWING")
class ProfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfilBinding
    private lateinit var mAuth:FirebaseAuth
    lateinit var currentUser:FirebaseUser
    var okulId:String?=""
    private lateinit var database:FirebaseDatabase

    private lateinit var okullar:ArrayList<Okul2>
    lateinit var okulAdi:ArrayList<String>
    lateinit var okullId:ArrayList<String>
    lateinit var adapter: ArrayAdapter<String>

//    lateinit var ogrenciler:ArrayList<Ogrenciler>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)
        binding=ActivityProfilBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)


    mAuth=FirebaseAuth.getInstance()
        currentUser = mAuth.currentUser!!
        database= FirebaseDatabase.getInstance()

//Burda; daha önce kayıtlı ise bir daha profil sayfasını görmeden direk anaActivite ye gidecek
//    val myRef=database.getReference("ogrenciler")
//    myRef.addValueEventListener(object : ValueEventListener {
//        @SuppressLint("SuspiciousIndentation")
//        override fun onDataChange(snapshot: DataSnapshot) {
// //           var profil=ArrayList<Ogrenciler>()
//            for (c in snapshot.children){
//                Log.e("snapshot",c.toString())
//                      val  profil=c.getValue(Ogrenciler::class.java)
//                            Log.e("profil",profil.toString())
//                if (profil!=null){
//                    Log.e("currentUser",currentUser.uid)
//                if (profil.ogrId.toString()==currentUser.uid) {
//                    val intent = Intent(this@ProfilActivity, AnaActivity::class.java)
//                    startActivity(intent)
//                    finish()
//                }
//                }
//            }
//
//        }
//
//        override fun onCancelled(error: DatabaseError) {
//
//        }
//
//    })
/////////

        okullar= ArrayList()
        okulAdi=ArrayList()
        okullId=ArrayList()
        okullariListele()
//        okullar.add("Diyarbakır Mesleki ve teknik Meslek Lisesi")
        adapter=ArrayAdapter(this,R.layout.spinner_item,okulAdi)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spOkul.adapter=adapter
//        okulId=okullId[binding.spOkul.selectedItemPosition]
        binding.spOkul.onItemSelectedListener=object:AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                okulId=okullar[position].id!!
                Log.e("okulId=",okulId.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
        binding.tvCikis.setOnClickListener {
            val prefences = getSharedPreferences("users", Context.MODE_PRIVATE)
            val editor = prefences.edit().clear().apply()

            mAuth.signOut()
            val intent= Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btKaydol.setOnClickListener {
            if (TextUtils.isEmpty(binding.etAd.text.toString())){
                return@setOnClickListener
                }
            if (TextUtils.isEmpty(binding.etSoyad.text.toString())){
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(binding.etNo.text.toString())){
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(binding.etEmail.text.toString()) || !Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text.toString()).matches()){
                return@setOnClickListener
            }
            val ad=binding.etAd.text.toString()
            val soyad=binding.etSoyad.text.toString()
            val no=binding.etNo.text.toString().toInt()
            val telNo=currentUser.phoneNumber
            val email=binding.etEmail.text.toString()
            val ogrenciler= Ogrenciler(ad,no,okulId,soyad,telNo.toString(),email,currentUser.uid,
                Date().time)
            Log.e("okulId",okulId.toString())
            Log.e("ogrenciler",ogrenciler.toString())
            val myRef=database.getReference("ogrenciler").push()
            myRef.setValue(ogrenciler)
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        val preferences=getSharedPreferences("users", Context.MODE_PRIVATE)
                        val editor = preferences.edit()
                        editor.putString("okulId",okulId.toString())
                        editor.apply()
                        Log.e("okulId=",okulId.toString())
                        Toast.makeText(this,"Kayıt Başarılı",Toast.LENGTH_SHORT).show()
                        val intent=Intent(this,AnaActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else{
                        Toast.makeText(this,"Hata: Kayıt Başarısız!",Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this,"hata:${it}",Toast.LENGTH_SHORT).show()
                }
                .addOnCanceledListener {
                    Toast.makeText(this,"İptal",Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun okullariListele() {
        database= FirebaseDatabase.getInstance()
        val ref=database.getReference("okullar")
        ref.addValueEventListener(@SuppressLint("SuspiciousIndentation")
        object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                okullar.clear()
                for (s in snapshot.children){
                    val ok=s.getValue(Okul2::class.java)

                    if (ok!=null) {
                        ok.id=s.key

                        okullar.add(ok)
                        okulAdi.add(ok.okulAdi.toString())
                        okullId.add(ok.id.toString())
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

}