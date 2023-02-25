package com.erdemselvi.kutuphanetakipsistemi

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import com.erdemselvi.kutuphanetakipsistemi.databinding.ActivityKitapEkleBinding
import com.erdemselvi.kutuphanetakipsistemi.googleApi.ApiClient
import com.erdemselvi.kutuphanetakipsistemi.googleApi.PostService
import com.erdemselvi.kutuphanetakipsistemi.googleApi.apiItems
import com.erdemselvi.kutuphanetakipsistemi.model.Kitaplar
import com.google.firebase.database.FirebaseDatabase
import com.google.zxing.client.android.Intents
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.Exception

class KitapEkleActivity : AppCompatActivity() {
    var kitapAdi2=""
    var yazarAdi2=""
    var ozet2=""
    var yayinci2=""
    var sayfaSayisi2=0
    var yayinTarihi2=""
    var dil2="tr"
    var derecelendirme2=0
    var resimUrl2=""

    lateinit var database: FirebaseDatabase

    private lateinit var postService: PostService
//    lateinit var postList: MutableList<PostApi>

    var okunanDeger:String=""

    private val barcodeLauncher = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents == null) {
            val originalIntent = result.originalIntent
            if (originalIntent == null) {
                Log.d("MainActivity", "Tarama İptal Edildi")
                Toast.makeText(this, "İptal Edildi", Toast.LENGTH_LONG).show()
            } else if (originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                Log.d(
                    "MainActivity",
                    "Eksik kamera izni nedeniyle iptal edilen tarama"
                )
                Toast.makeText(
                    this,
                    "Eksik kamera izni nedeniyle iptal edildi",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            okunanDeger=result.contents
            if (okunanDeger!="") {
                Toast.makeText(applicationContext, "Barkod başarıyla okundu.", Toast.LENGTH_LONG).show()
                binding.etIsbn.setText(okunanDeger)
                kitapAra()
            }
//            val intent = Intent(this, KitapEkleActivity::class.java)
//            intent.putExtra("tarananDeger",tarananDeger)
//            startActivity(intent)
            Log.d("MainActivity", "Tarandı")
//            Toast.makeText(
//                this,
//                "Tarandı: " + result.contents,
//                Toast.LENGTH_LONG
//            ).show()
        }
    }

    lateinit var binding:ActivityKitapEkleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kitap_ekle)
        binding=ActivityKitapEkleBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        database= FirebaseDatabase.getInstance()
//        okunanDeger=intent.getStringExtra("tarananDeger").toString()
//        binding.etIsbn.setText(okunanDeger)



//        kitapAra()

        binding.btInternetteAra.setOnClickListener {
            if (TextUtils.isEmpty(binding.etIsbn.text.toString().trim { it <= ' ' })) {
                binding.etIsbn.error = "Lütfen ISBN No Giriniz"
            }
            okunanDeger=binding.etIsbn.text.toString()
            if (okunanDeger!="") {
                kitapAra()
//                kayitliKitapAra()
            }
        }

        binding.btBarcodeOku.setOnClickListener {
            barcodeLauncher.launch(ScanOptions())
        }

    }

    private fun kitapAra() {
        postService = ApiClient.getClient().create(PostService::class.java)
        //ApiClient.getClient().create(com.erdemselvi.kutuphanetakipsistemi.googleApi.PostService::class.java)
        val isbnDegeri= "isbn:"+okunanDeger
        val post =postService.listPost(isbnDegeri, resources.getString(R.string.GoogleBookAPIKey))
        post.enqueue(object : Callback<apiItems> {
            override fun onFailure(call: Call<apiItems>, t: Throwable) {
                Toast.makeText(applicationContext, t.message.toString(), Toast.LENGTH_LONG).show()
                Log.e("gelenVeri",t.message.toString())
            }

            override fun onResponse(call: Call<apiItems>, response: Response<apiItems>) {
                var kitapAdi:String?=""
                var yazarAdi:String?=""
                var ozet:String?=""
                var kategori:String?=""
                var sayfaSayisi=0
                var yayinTarihi:String?=""
                var dil:String?="tr"
                var derecelendirme=0
                var resimUrl=""
                try {
                    if (response.isSuccessful) {


                        val rB=response.body()!!
                        Log.e("responsebody",response.body().toString())
                        for (myData in rB.items){

//                           for (isbnn in myData.volumeInfo.industryIdentifiers){
//                               if (okunanDeger.toString()==isbnn.identifier){
                                kitapAdi = myData.volumeInfo.title
//                                for (yazar in myData.volumeInfo.authors) {
//                                    yazarAdi += yazar
//                                }
                                yazarAdi=myData.volumeInfo.authors[0]
                                ozet = myData.volumeInfo.description
                                sayfaSayisi = myData.volumeInfo.pageCount

                                yayinTarihi = myData.volumeInfo.publishedDate
                                dil = myData.volumeInfo.language

                            binding.etKitapAdi.setText(kitapAdi)
                            binding.etYazarAdi.setText(yazarAdi)
                            binding.etOzet.setText(ozet)

                            binding.etSayfaSayisi.setText(sayfaSayisi.toString())
                            binding.etYayinTarihi.setText(yayinTarihi)
                            binding.etDil.setText(dil)


                            kategori = myData.volumeInfo.categories[0]

                             derecelendirme = myData.volumeInfo.ratingsCount!!

                                //Google BookApi de bazen kitap resmi olmuyor. Burdan varmı yokmu diye kontrol ediyoruz.
                                //Resim yoksa null hatası vermesin diye kontrol ediyoruz.
//                            if (myData.volumeInfo.readingModes.image){


                                    resimUrl = myData.volumeInfo.imageLinks.smallThumbnail.toString()
                                    binding.etResimUrl.setText(resimUrl)
                                    Picasso.with(applicationContext)
                                        .load(resimUrl.toUri())

//                                .resize(400, 400)         //optional
//                                .centerCrop()                        //optional
                                        .into(binding.ivKitap)




//                            resimUrl=myData.volumeInfo.imageLinks.smallThumbnail

//                               }
//                           }

//                        val listItems=myData.volumeInfo.title
//
//                        binding.tvOkunanDeger.text=listItems
//                        binding.etIsbn.setText(kitapAdi)

                            binding.etDerecelendirme.setText(derecelendirme.toString())

                            binding.etKategori.setText(kategori)
                            binding.etResimUrl.setText(resimUrl)


                        }

//                    postList = (response.body() as MutableList<PostApi>?)!!
//                    Log.e("gelenVeri",postList.toString())
//                    binding.tvOkunanDeger.text=postList.toString()
                    }
                    else{
                        Toast.makeText(applicationContext, "İnternet veritabanında bu Kitap Bulunamadı!", Toast.LENGTH_LONG).show()
                    }
                }catch (e:Exception){

 //                   Toast.makeText(applicationContext, "Kitap Bulunamadı! Hata:${e.message.toString()}", Toast.LENGTH_LONG).show()
                    Log.e("çekilen veri hatası",e.message.toString())
                }

            }
        })

        binding.btKitapKaydet.setOnClickListener {
            val prefences = getSharedPreferences("users", Context.MODE_PRIVATE)
            val okulId = prefences.getString("okulId","yok")
            val kitaplar=Kitaplar(okunanDeger, binding.etKitapAdi.text.toString(),binding.etYazarAdi.text.toString(),
                binding.etOzet.text.toString(),binding.etKategori.text.toString(),binding.etSayfaSayisi.text.toString().toInt(),
                binding.etYayinTarihi.text.toString(),binding.etDil.text.toString(),
                binding.etDerecelendirme.text.toString().toInt(),binding.etResimUrl.text.toString(),okulId)
            if(binding.etIsbn.text.isNotEmpty() or binding.etKitapAdi.text.isNotEmpty()
                or binding.etOzet.text.isNotEmpty() or binding.etYazarAdi.text.isNotEmpty() or binding.etKategori.text.isNotEmpty()
                or binding.etSayfaSayisi.text.isNotEmpty() or binding.etDil.text.isNotEmpty()) {
                val myRef = database.getReference("kitaplar").push()
                myRef.setValue(kitaplar)
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            Toast.makeText(this@KitapEkleActivity,"Kayıt Başarılı",Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        else{
                            Toast.makeText(this@KitapEkleActivity,"Hata: Kayıt Başarısız!",Toast.LENGTH_SHORT).show()
                        }
                    }

            }
            else{
                Toast.makeText(applicationContext, "Önce kitap aramalısınız.", Toast.LENGTH_LONG).show()
            }
        }
    }

//    fun kayitliKitapAra(){
//        val myRef = database.getReference("kitaplar")
//        myRef.addValueEventListener(object :ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                for (s in snapshot.children) {
//                    val kitap=s.getValue(Kitaplar::class.java)
//                    if (kitap!!.isbn==okunanDeger){
//                        binding.etKitapAdi.setText(kitap.kitapAdi)
//                        binding.etYazarAdi.setText(kitap.yazarAdi)
//                        binding.etOzet.setText(kitap.kisaOzet)
//                        binding.etYayinci.setText(kitap.yayinEvi)
//                        binding.etSayfaSayisi.setText(kitap.sayfaSayisi)
//                        binding.etDil.setText(kitap.dil)
//
//                    }
//
//
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//
//            }
//
//        })
//
//    }

}