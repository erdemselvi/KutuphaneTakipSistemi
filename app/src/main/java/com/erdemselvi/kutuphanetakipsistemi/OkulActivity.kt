package com.erdemselvi.kutuphanetakipsistemi

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.erdemselvi.kutuphanetakipsistemi.adapter.OkulAdapter
import com.erdemselvi.kutuphanetakipsistemi.databinding.ActivityOkulBinding
import com.erdemselvi.kutuphanetakipsistemi.model.Emanets
import com.erdemselvi.kutuphanetakipsistemi.model.Okul
import com.erdemselvi.kutuphanetakipsistemi.model.Okul2
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OkulActivity : AppCompatActivity() {

    lateinit var binding:ActivityOkulBinding
    lateinit var database: FirebaseDatabase
    lateinit var adapter: OkulAdapter
    lateinit var okul:ArrayList<Okul2>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_okul)
        binding = ActivityOkulBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.title="Okul Ekleme Ekranı"

        okul = ArrayList()
        okullariListele()

        adapter = OkulAdapter(this, okul)
        binding.rvOkullar.hasFixedSize()
        binding.rvOkullar.layoutManager =
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        binding.rvOkullar.adapter = adapter

        binding.btOkulEkle.setOnClickListener {
            val popupEkle = LayoutInflater.from(this).inflate(R.layout.okul_ekle, null)
            val btKaydet: Button = popupEkle.findViewById(R.id.btKaydet)
            val okulAdi: EditText = popupEkle.findViewById(R.id.etOkulAdi)
            val sehir: EditText = popupEkle.findViewById(R.id.etSehir)
            val builderEkle = AlertDialog.Builder(this)
                .setView(popupEkle)
                .setTitle("Okul Ekle")
            val dialogEkle = builderEkle.show()

            btKaydet.setOnClickListener {
                if (TextUtils.isEmpty(okulAdi.text.toString().trim { it <= ' ' })) {
                    Toast.makeText(this, "Lütfen okul adını boş geçmeyin!", Toast.LENGTH_LONG)
                        .show()
                } else if (TextUtils.isEmpty(sehir.text.toString().trim { it <= ' ' })) {
                    Toast.makeText(this, "Lütfen şehiri boş geçmeyin!", Toast.LENGTH_LONG).show()

                } else {
                    val dbEkle = FirebaseDatabase.getInstance()
                    val refOkul = dbEkle.getReference("okullar")
                    val okul = Okul(okulAdi.text.toString(), sehir.text.toString())
                    refOkul.push().setValue(okul)

                    dialogEkle.dismiss()
                }

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
                    okul.clear()
                    for (s in snapshot.children){
                        val ok=s.getValue(Okul2::class.java)

                        if (ok!=null) {
                            ok.id=s.key

                                okul.add(ok)

                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }
}