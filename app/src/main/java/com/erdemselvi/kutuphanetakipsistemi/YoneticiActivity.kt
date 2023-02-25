package com.erdemselvi.kutuphanetakipsistemi

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.Adapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.erdemselvi.kutuphanetakipsistemi.adapter.OkulAdapter
import com.erdemselvi.kutuphanetakipsistemi.adapter.YoneticiAdapter
import com.erdemselvi.kutuphanetakipsistemi.databinding.ActivityYoneticiBinding
import com.erdemselvi.kutuphanetakipsistemi.model.Okul
import com.erdemselvi.kutuphanetakipsistemi.model.Okul2
import com.erdemselvi.kutuphanetakipsistemi.model.Yonetici
import com.erdemselvi.kutuphanetakipsistemi.model.Yonetici2
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class YoneticiActivity : AppCompatActivity() {

    lateinit var binding:ActivityYoneticiBinding
    lateinit var adapter: YoneticiAdapter
    lateinit var database: FirebaseDatabase
    lateinit var yoneticiler:ArrayList<Yonetici2>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_yonetici)
        binding= ActivityYoneticiBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        yoneticiler=ArrayList()
        yoneticileriListele()

        adapter = YoneticiAdapter(this, yoneticiler)
        binding.rvYonetici.hasFixedSize()
        binding.rvYonetici.layoutManager =
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        binding.rvYonetici.adapter = adapter


        binding.btOgretmenBul.setOnClickListener {
            val intent = Intent(this, OgretmenBulActivity::class.java)
            startActivityForResult(intent, KitapAlActivity.NEW_ITEM_ACTIVITY_REQUEST_CODE)
        }
        binding.btOgretmenEkle.setOnClickListener {
            val popupEkle = LayoutInflater.from(this).inflate(R.layout.yonetici_ekle, null)
            val btKaydet: Button = popupEkle.findViewById(R.id.btKaydet)
            val email: EditText = popupEkle.findViewById(R.id.etEmail)
            val adSoyad: EditText = popupEkle.findViewById(R.id.etAdSoyad)
            val builderEkle = AlertDialog.Builder(this)
                .setView(popupEkle)
                .setTitle("Yönetici Ekle")
            val dialogEkle = builderEkle.show()

            btKaydet.setOnClickListener {
                if (TextUtils.isEmpty(email.text.toString().trim { it <= ' ' })) {
                    Toast.makeText(this, "Lütfen email boş geçmeyin!", Toast.LENGTH_LONG)
                        .show()
                } else if (TextUtils.isEmpty(adSoyad.text.toString().trim { it <= ' ' })) {
                    Toast.makeText(this, "Lütfen ad soyad boş geçmeyin!", Toast.LENGTH_LONG).show()

                } else {
                    val dbEkle = FirebaseDatabase.getInstance()
                    val refOkul = dbEkle.getReference("yoneticiler")
                    val okul = Yonetici(email.text.toString(), adSoyad.text.toString())
                    refOkul.push().setValue(okul)

                    dialogEkle.dismiss()
                }

            }
        }
    }

    private fun yoneticileriListele() {
        database= FirebaseDatabase.getInstance()
        val ref=database.getReference("yoneticiler")
        ref.addValueEventListener(@SuppressLint("SuspiciousIndentation")
        object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                yoneticiler.clear()
                for (s in snapshot.children){
                    val yon=s.getValue(Yonetici2::class.java)

                    if (yon!=null) {
                        yon.id=s.key

                        yoneticiler.add(yon)

                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK){

            binding.tvOgrAdi.text=data?.getStringExtra("ad")+" "+data?.getStringExtra("soyad")
            binding.tvTelNo.text=data?.getStringExtra("telNo")
            binding.tvOkulAdi.text=data?.getIntExtra("no",1).toString()
//            binding.tvOgrenciBilgisi.text=data?.getStringExtra("id")+" "+data?.getStringExtra("ad")+" "+data?.getStringExtra("soyad")

//            adapter.notifyDataSetChanged()

            binding.tvTelNo.setOnClickListener {
//                checkPermission()
//                callPhone()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }
}