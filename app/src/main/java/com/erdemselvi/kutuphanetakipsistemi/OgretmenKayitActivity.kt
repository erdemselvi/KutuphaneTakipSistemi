package com.erdemselvi.kutuphanetakipsistemi


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.erdemselvi.kutuphanetakipsistemi.databinding.ActivityOgretmenKayitBinding
import com.erdemselvi.kutuphanetakipsistemi.model.Ogretmen
import com.erdemselvi.kutuphanetakipsistemi.model.Okul2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList

class OgretmenKayitActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var mAuth: FirebaseAuth
    lateinit var currentUser: FirebaseUser
    lateinit var okullId:ArrayList<String>
    var okulId:String?=""
    private lateinit var okullar:ArrayList<Okul2>
    lateinit var okulAdi:ArrayList<String>
    lateinit var binding:ActivityOgretmenKayitBinding
    lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_ogretmen_kayit)
        binding=ActivityOgretmenKayitBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        mAuth=FirebaseAuth.getInstance()
        currentUser = mAuth.currentUser!!
        database= FirebaseDatabase.getInstance()

        okullar= ArrayList()
        okulAdi=ArrayList()
        okullId=ArrayList()
        okullariListele()
        adapter=ArrayAdapter(this,R.layout.spinner_item,okulAdi)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spOkul.adapter=adapter

//        okulId=okullId[binding.spOkul.selectedItemPosition]

        binding.spOkul.onItemSelectedListener=object: AdapterView.OnItemSelectedListener{
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
        binding.btKaydol.setOnClickListener {
            if (TextUtils.isEmpty(binding.etAd.text.toString())){
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(binding.etSoyad.text.toString())){
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(binding.etTelefonNo.text.toString())){
                return@setOnClickListener
            }

            val ad=binding.etAd.text.toString()
            val soyad=binding.etSoyad.text.toString()
            val telNo=binding.etTelefonNo.text.toString()

            val ogretmen= Ogretmen(ad,okulId,soyad,telNo,currentUser.email,currentUser.uid, Date().time,1)
            val myRef=database.getReference("ogretmenler").push()
            myRef.setValue(ogretmen)
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        val preferences=getSharedPreferences("users", Context.MODE_PRIVATE)
                        val editor = preferences.edit()
                        editor.putString("okulId",okulId)
                        editor.apply()
                        Toast.makeText(this,"Kayıt Başarılı", Toast.LENGTH_SHORT).show()
                        val intent= Intent(this,DashBoardActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else{
                        Toast.makeText(this,"Hata: Kayıt Başarısız!", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this,"hata:${it}", Toast.LENGTH_SHORT).show()
                }
                .addOnCanceledListener {
                    Toast.makeText(this,"İptal", Toast.LENGTH_SHORT).show()
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