package com.erdemselvi.kutuphanetakipsistemi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.get
import com.erdemselvi.kutuphanetakipsistemi.databinding.ActivityOgrenciEkleBinding
import com.erdemselvi.kutuphanetakipsistemi.model.Ogrenciler
import com.google.firebase.database.FirebaseDatabase

class OgrenciEkleActivity : AppCompatActivity() {

    lateinit var binding:ActivityOgrenciEkleBinding
    lateinit var database: FirebaseDatabase
    lateinit var okullar:ArrayList<String>
    var okulId:String="-NKcfUoIORRUZiwxD8ps"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ogrenci_ekle)
        binding= ActivityOgrenciEkleBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        okullar= ArrayList()
        okullar.add("Diyarbakır Mesleki ve teknik Meslek Lisesi")
        val adapter= ArrayAdapter(this,android.R.layout.simple_list_item_1,okullar)
        binding.spOkul.adapter=adapter

        binding.spOkul.onItemSelectedListener=object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                okulId= parent?.get(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }


        database= FirebaseDatabase.getInstance()
        val myRef=database.getReference("ogrenciler").push()
        binding.btKaydet.setOnClickListener {

            if (TextUtils.isEmpty(binding.etAd.text.toString())){
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(binding.etSoyad.text.toString())){
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(binding.etNo.text.toString())){
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(binding.etTelNo.text.toString()) || !Patterns.PHONE.matcher(binding.etTelNo.text.toString()).matches()){
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(binding.etEmail.text.toString()) || !Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text.toString()).matches()){
                return@setOnClickListener
            }
            val ad=binding.etAd.text.toString()
            val soyad=binding.etSoyad.text.toString()
            val no=binding.etNo.text.toString().toInt()
            val telNo="+90"+binding.etTelNo.text.toString()
            val email=binding.etEmail.text.toString()
            val ogrenciler= Ogrenciler(ad,no,okulId,soyad,telNo,email,"")

            myRef.setValue(ogrenciler)
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(this,"Kayıt Başarılı", Toast.LENGTH_SHORT).show()
                        val intent=Intent(this,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else{
                        Toast.makeText(this,"Hata: Kayıt Başarısız!", Toast.LENGTH_SHORT).show()
                    }
                }

        }
    }
}