package com.erdemselvi.kutuphanetakipsistemi

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.erdemselvi.kutuphanetakipsistemi.adapter.OgrenciBulAdapter
import com.erdemselvi.kutuphanetakipsistemi.databinding.ActivityOgrenciBulBinding
import com.erdemselvi.kutuphanetakipsistemi.model.Ogrenciler2
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList


class OgrenciBulActivity : AppCompatActivity(), SearchView.OnQueryTextListener {
    lateinit var adapter: OgrenciBulAdapter
    lateinit var database: FirebaseDatabase
    private lateinit var profill:ArrayList<Ogrenciler2>
    lateinit var binding:ActivityOgrenciBulBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ogrenci_bul)
        binding=ActivityOgrenciBulBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        supportActionBar?.title="Öğrenci Bulma Ekranı"

        database=FirebaseDatabase.getInstance()
        profill= ArrayList()

        if (intent.getStringExtra("gorevli")=="okulgörevlisi"){
            okuldakiOgrencileriBul()
        }else{
            ogrenciBul()
        }

        val activity:Activity=this

        binding.rvOgrenciBul.setHasFixedSize(true)
        binding.rvOgrenciBul.layoutManager= StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        adapter=OgrenciBulAdapter(activity ,this,profill)
        binding.rvOgrenciBul.adapter=adapter
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK){

        }

        super.onActivityResult(requestCode, resultCode, data)
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
            okuldakiOgrencileriAra(query)
        }else{
            aramaYap(query)
        }

        Log.e("Gönderilen arama",query)
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        aramaYap(newText)
        Log.e("Harf girdikçe",newText)
        return true
    }

    private fun aramaYap(newText: String) {
        val myRef=database.getReference("ogrenciler")
        myRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
              profill.clear()
                for (c in snapshot.children) {
                    val profil = c.getValue(Ogrenciler2::class.java)

                    Log.e("ara", profil.toString())
                    if (profil!=null) {
                        if (profil.ad!!.lowercase().contains(newText.lowercase()) || profil.soyad!!.lowercase().contains(newText.lowercase())
                            || profil.no!!.toString().lowercase().contains(newText.lowercase())  ) {
                            profil.Id = c.key
                            profill.add(profil)
                        }
                    }

                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
    private fun okuldakiOgrencileriAra(newText: String) {
        val prefences = getSharedPreferences("users", Context.MODE_PRIVATE)
        val okulId = prefences.getString("okulId","yok")
        val myRef=database.getReference("ogrenciler")
        myRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                profill.clear()
                for (c in snapshot.children) {
                    val profil = c.getValue(Ogrenciler2::class.java)

                    Log.e("ara", profil.toString())
                    if (profil!=null) {
                        if (okulId==profil.okulId) {
                            if (profil.ad!!.lowercase()
                                    .contains(newText.lowercase()) || profil.soyad!!.lowercase()
                                    .contains(newText.lowercase())
                                || profil.no!!.toString().lowercase().contains(newText.lowercase())
                            ) {
                                profil.Id = c.key
                                profill.add(profil)
                            }
                        }
                    }

                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
    private fun ogrenciBul() {
        val myRef=database.getReference("ogrenciler")
        profill= ArrayList()
        myRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
               profill.clear()
                for (c in snapshot.children) {
                    val profil = c.getValue(Ogrenciler2::class.java)

                    Log.e("snapshot", profil.toString())

                        profil!!.Id=c.key
                        profill.add(profil)


                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
    private fun okuldakiOgrencileriBul() {
        val prefences = getSharedPreferences("users", Context.MODE_PRIVATE)
        val okulId = prefences.getString("okulId","yok")
        val myRef=database.getReference("ogrenciler")
        profill= ArrayList()
        myRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                profill.clear()
                for (c in snapshot.children) {

                    val profil = c.getValue(Ogrenciler2::class.java)
                    if (profil!=null){
                        if (okulId==profil.okulId){
                            Log.e("snapshot", profil.toString())

                            profil.Id=c.key
                            profill.add(profil)
                        }

                    }

                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}