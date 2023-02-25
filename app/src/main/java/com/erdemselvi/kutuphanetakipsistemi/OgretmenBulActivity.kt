package com.erdemselvi.kutuphanetakipsistemi

import android.annotation.SuppressLint
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.erdemselvi.kutuphanetakipsistemi.adapter.OgretmenBulAdaptor
import com.erdemselvi.kutuphanetakipsistemi.databinding.ActivityOgretmenBulBinding
import com.erdemselvi.kutuphanetakipsistemi.model.Ogretmen2
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OgretmenBulActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    lateinit var binding:ActivityOgretmenBulBinding
    lateinit var database:FirebaseDatabase
    lateinit var adapter: OgretmenBulAdaptor
    lateinit var ogretmenler:ArrayList<Ogretmen2>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_ogretmen_bul)
        binding=ActivityOgretmenBulBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        database= FirebaseDatabase.getInstance()
        ogretmenler= ArrayList()

        ogretmenleriListele()
        val activity: Activity =this
        binding.rvOgretmenBul.setHasFixedSize(true)
        binding.rvOgretmenBul.layoutManager= StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        adapter= OgretmenBulAdaptor(activity ,this,ogretmenler)
        binding.rvOgretmenBul.adapter=adapter


    }

    private fun ogretmenleriListele() {
        val myRef=database.getReference("ogretmenler")

        myRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                ogretmenler.clear()
                for (c in snapshot.children) {
                    val profil = c.getValue(Ogretmen2::class.java)

                    Log.e("snapshot", profil.toString())

                    profil!!.Id=c.key
                    ogretmenler.add(profil)


                }
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
        aramaYap(query)
        Log.e("Gönderilen arama",query)
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        aramaYap(newText)
        Log.e("Harf girdikçe",newText)
        return true
    }
    private fun aramaYap(newText: String) {
        val myRef=database.getReference("ogretmenler")
        myRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                ogretmenler.clear()
                for (c in snapshot.children) {
                    val profil = c.getValue(Ogretmen2::class.java)

                    Log.e("ara", profil.toString())
                    if (profil!=null) {
                        if (profil.ad!!.lowercase().contains(newText.lowercase()) ||
                            profil.soyad!!.lowercase().contains(newText.lowercase())) {
                            profil.Id = c.key
                            ogretmenler.add(profil)
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