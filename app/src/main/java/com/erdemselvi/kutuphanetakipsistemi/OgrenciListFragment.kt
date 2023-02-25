package com.erdemselvi.kutuphanetakipsistemi

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.erdemselvi.kutuphanetakipsistemi.model.Ogrenciler
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class OgrenciListFragment : DialogFragment() {

    lateinit var database: FirebaseDatabase
//    private lateinit var profill:ArrayList<Ogrenciler>
    lateinit var liste: ArrayList<String>

     interface onInputListener{
        fun sendInput(input:String)
    }

     lateinit var monInputListener:onInputListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView=inflater.inflate(R.layout.fragment_ogrenci_list,container,false)
        database= FirebaseDatabase.getInstance()
        liste=ArrayList()
//        profill= ArrayList()
        val myRef=database.getReference("ogrenciler")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                liste.clear()
                //           var profil=ArrayList<Ogrenciler>()
                for (c in snapshot.children) {
                    Log.e("snapshot", c.toString())
                    val profil = c.getValue(Ogrenciler::class.java)
                    Log.e("profil", profil.toString())
                    if (profil != null) {
 //                       profill.add(profil)
                        liste.add(profil.ad.toString()+"*"+ profil.soyad.toString()+"*"+profil.no.toString()+"*"+profil.telNo+"*"+c.key)
                    }
                }
//                Log.e("Liste",profill.toString())

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        val listView=rootView.findViewById(R.id.lvOgrList) as ListView
        listView.adapter=
            activity?.let { ArrayAdapter(it,android.R.layout.simple_list_item_1,liste) }

//        this.dialog!!.setTitle("Öğrenci Seçiniz")

//        val rootView2=inflater.inflate(R.layout.activity_kitap_teslim,container,false)
        listView.setOnItemClickListener { parent, view, position, id ->
//            val ogrAd=rootView2.findViewById(R.id.etOgrenciAdi) as EditText
//            ogrAd.setText(liste[position])
//            Toast.makeText(activity, liste[position], Toast.LENGTH_SHORT).show()

            monInputListener.sendInput(liste[position])

//            val bundle = Bundle()
//            bundle.putString("myValue", "MyValue")

//            val intent = Intent().putExtras(bundle)

//            targetFragment!!.onActivityResult(1, Activity.RESULT_OK, intent)


            this.dialog!!.dismiss()

        }
        // Inflate the layout for this fragment
        return rootView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            monInputListener = activity as onInputListener
        }catch (e:Exception){
            Log.e("hata",e.toString())
        }
    }


}