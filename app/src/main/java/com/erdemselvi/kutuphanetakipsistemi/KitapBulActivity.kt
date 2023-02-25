package com.erdemselvi.kutuphanetakipsistemi

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.erdemselvi.kutuphanetakipsistemi.adapter.KitapBulAdapter
import com.erdemselvi.kutuphanetakipsistemi.databinding.ActivityKitapBulBinding
import com.erdemselvi.kutuphanetakipsistemi.model.Kitaplar2
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class KitapBulActivity : AppCompatActivity() , SearchView.OnQueryTextListener{

    lateinit var binding:ActivityKitapBulBinding
    lateinit var kitaplar:ArrayList<Kitaplar2>
    private lateinit var database: FirebaseDatabase
    lateinit var adapter: KitapBulAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_kitap_bul)
        binding= ActivityKitapBulBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        supportActionBar?.title="Kitap Bulma Ekranı"

        database= FirebaseDatabase.getInstance()


        if (intent.getStringExtra("gorevli")=="okulgörevlisi"){
            okuldakiKitaplariBul()
        }else{
            kitaplariBul()
        }

        val activity: Activity =this
        binding.rvKitapBul.layoutManager= StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        binding.rvKitapBul.setHasFixedSize(true)
        adapter= KitapBulAdapter(activity,this,kitaplar)
        binding.rvKitapBul.adapter=adapter

    }

    private fun kitaplariBul() {
        kitaplar= ArrayList()

        val ref=database.getReference("kitaplar")
        ref.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                kitaplar.clear()

                for (k in snapshot.children){
                    val kitap=k.getValue(Kitaplar2::class.java)
                    if (kitap!=null){
                        kitap.id=k.key
                        kitaplar.add(kitap)

                    }
                }
                Log.e("kitaplar",kitaplar.toString())
                adapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
    private fun okuldakiKitaplariBul() {
        kitaplar= ArrayList()
        val prefences = getSharedPreferences("users", Context.MODE_PRIVATE)
        val okulId = prefences.getString("okulId","yok")
        val ref=database.getReference("kitaplar")
        ref.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                kitaplar.clear()

                for (k in snapshot.children){
                    val kitap=k.getValue(Kitaplar2::class.java)
                    if (kitap!=null){
                        if (okulId==kitap.okulId) {
                            kitap.id = k.key
                            kitaplar.add(kitap)
                        }
                    }
                }
                Log.e("kitaplar",kitaplar.toString())
                adapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.bul_menu,menu)

        val item = menu.findItem(R.id.action_ara)
        val searchView = item?.actionView as SearchView
        searchView.setOnQueryTextListener(this)

        return super.onCreateOptionsMenu(menu)

    }

    override fun onQueryTextSubmit(query: String): Boolean {
        if (intent.getStringExtra("gorevli")=="okulgörevlisi"){
            okuldakiKitaplariAra(query)
        }else{
            aramaYap(query)
        }

        Log.e("Gönderilen arama",query)
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
//        if (newText.isEmpty()){
//            kitaplariBul()
//        }
        if (intent.getStringExtra("gorevli")=="okulgörevlisi"){
            okuldakiKitaplariAra(newText)
        }else{
            aramaYap(newText)
        }
        Log.e("Harf girdikçe",newText)

        return true
    }

    private fun aramaYap(newText: String) {
//        kitaplar= ArrayList()

        val ref=database.getReference("kitaplar")

        ref.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                kitaplar.clear()
                for (k in snapshot.children){
                    val kitap=k.getValue(Kitaplar2::class.java)
                    if (kitap!=null){
                        if (kitap.kitapAdi!!.lowercase().contains(newText.lowercase()) || kitap.yazarAdi!!.lowercase().contains(newText.lowercase()) ||
                                kitap.isbn!!.contains(newText)){
                            kitap.id=k.key
                            kitaplar.add(kitap)
                        }


                    }
                }
                Log.e("kitaplar",kitaplar.toString())
                adapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
    private fun okuldakiKitaplariAra(newText: String) {
//        kitaplar= ArrayList()
        val prefences = getSharedPreferences("users", Context.MODE_PRIVATE)
        val okulId = prefences.getString("okulId","yok")
        val ref=database.getReference("kitaplar")

        ref.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                kitaplar.clear()
                for (k in snapshot.children){
                    val kitap=k.getValue(Kitaplar2::class.java)
                    if (kitap!=null){
                        if (okulId==kitap.okulId) {
                            if (kitap.kitapAdi!!.lowercase()
                                    .contains(newText.lowercase()) || kitap.yazarAdi!!.lowercase()
                                    .contains(newText.lowercase()) ||
                                kitap.isbn!!.contains(newText)
                            ) {
                                kitap.id = k.key
                                kitaplar.add(kitap)
                            }

                        }
                    }
                }
                Log.e("kitaplar",kitaplar.toString())
                adapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}
