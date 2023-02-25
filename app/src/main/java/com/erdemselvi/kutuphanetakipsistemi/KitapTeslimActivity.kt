package com.erdemselvi.kutuphanetakipsistemi

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.erdemselvi.kutuphanetakipsistemi.databinding.ActivityKitapTeslimBinding
import com.erdemselvi.kutuphanetakipsistemi.googleApi.ApiClient
import com.erdemselvi.kutuphanetakipsistemi.googleApi.PostService
import com.erdemselvi.kutuphanetakipsistemi.googleApi.apiItems
import com.erdemselvi.kutuphanetakipsistemi.model.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.zxing.client.android.Intents
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


@Suppress("LABEL_NAME_CLASH")
class KitapTeslimActivity : AppCompatActivity(),OgrenciListFragment.onInputListener {

    @SuppressLint("SetTextI18n", "SuspiciousIndentation")
    override fun sendInput(input:String){
        val inputs=input.split("*")
        binding.etOgrenciAdi.setText(inputs[0]+" "+inputs[1])
        binding.etOgrenciNo.setText(inputs[2])
        binding.etOgrenciTel.setText(inputs[3])
        binding.etOgrId.setText(inputs[4])
    }

    private lateinit var profill:ArrayList<Ogrenciler>
    lateinit var database: FirebaseDatabase

    lateinit var binding:ActivityKitapTeslimBinding
    lateinit var postService: PostService
    lateinit var emanet:ArrayList<Emanet2>
    var kitapDurumu=0

    var okunanDeger:String=""
    var qrDeger:String=""
    var qrDeger2:String=""
    lateinit var emanetDizi:ArrayList<Emanet>
    private val barcodeLauncher = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents == null) {
            val originalIntent = result.originalIntent
            if (originalIntent == null) {
                Log.d("KitapTeslimActivity", "Tarama İptal Edildi")
                Toast.makeText(this, "İptal Edildi", Toast.LENGTH_LONG).show()
            } else if (originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                Log.d(
                    "KitapTeslimActivity",
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
//                kitapAra()
                kayitliKitapAra()
            }
            Log.d("KitapTeslimActivity", "Tarandı")
        }
    }



    private val qrLauncher = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents == null) {
            val originalIntent = result.originalIntent
            if (originalIntent == null) {
                Log.d("KitapTeslimActivity", "Tarama İptal Edildi")
                Toast.makeText(this, "İptal Edildi", Toast.LENGTH_LONG).show()
            } else if (originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                Log.d(
                    "KitapTeslimActivity",
                    "Eksik kamera izni nedeniyle iptal edilen tarama"
                )
                Toast.makeText(
                    this,
                    "Eksik kamera izni nedeniyle iptal edildi",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            qrDeger=result.contents
            if (qrDeger!="") {
                emanetDizi=ArrayList()
                degiskenKaydet(qrDeger)
                Toast.makeText(applicationContext, "QRCode başarıyla okundu.", Toast.LENGTH_LONG).show()
                binding.etOgrenciAdi.setText(qrDeger)
                binding.etOgrId.setText(qrDeger)
                val myRef=database.getReference("ogrenciler")
                myRef.addValueEventListener(object : ValueEventListener{
                    @SuppressLint("SetTextI18n")
                    override fun onDataChange(snapshot: DataSnapshot) {
                        profill= ArrayList()
                        for (c in snapshot.children) {
                            val profil = c.getValue(Ogrenciler::class.java)
                            Log.e("snapshot", profil.toString())
                            if (profil!!.ogrId.toString() == qrDeger) {
                                binding.etOgrenciAdi.setText(profil.ad + " " + profil.soyad)
                                binding.etOgrenciNo.setText(profil.no.toString())
                                binding.etOgrenciTel.setText(profil.telNo)

                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })




            }
            Log.d("KitapTeslimActivity", "Tarandı")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kitap_teslim)

        binding=ActivityKitapTeslimBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        database= FirebaseDatabase.getInstance()

        binding.btBarcodeOku.setOnClickListener {
            barcodeLauncher.launch(ScanOptions())
        }
        binding.btInternetteAra.setOnClickListener {
            if (TextUtils.isEmpty(binding.etIsbn.text.toString().trim { it <= ' ' })) {
                binding.etIsbn.setError("Lütfen ISBN No Giriniz")
            }
            okunanDeger=binding.etIsbn.text.toString()
            if (okunanDeger!="") {
//                kitapAra()
//                kayitliKitapAra()
                ktpAra2()
            }
        }
        binding.btOgrenciQrCode.setOnClickListener{
            val options=ScanOptions()
            options.setOrientationLocked(true)
                .setTimeout(8000)

            qrDeger= qrLauncher.launch(options).toString()
//            qrLauncher.launch(ScanOptions())
//            val options = ScanOptions()
//            options.captureActivity = AnyOrientationCaptureActivity::class.java
//            options.setDesiredBarcodeFormats(ScanOptions.ONE_D_CODE_TYPES)
//            options.setPrompt("QRCode Tarayın")
//            options.setOrientationLocked(false)
//            options.setBeepEnabled(false)
//            qrLauncher.launch(options)

        }
        binding.btTeslimEt.setOnClickListener {
//                if (TextUtils.isEmpty(binding.etIsbn.text.toString())){
//                    return@setOnClickListener
//                }
//                if (TextUtils.isEmpty(binding.etKitapAdi.text.toString())){
//                    return@setOnClickListener
//                }
//                if (TextUtils.isEmpty(binding.etOgrId.text.toString())){
//                    return@setOnClickListener
//                }
//                if (TextUtils.isEmpty(binding.etOgrenciAdi.text.toString())){
//                    return@setOnClickListener
//                }
            Log.e("btteslimEt","tıklandı")
            kitapTeslimEt()
            return@setOnClickListener
        }
        binding.btTeslimAl.setOnClickListener {
//            if (TextUtils.isEmpty(binding.etIsbn.text.toString())){
//                return@setOnClickListener
//            }
//            if (TextUtils.isEmpty(binding.etKitapAdi.text.toString())){
//                return@setOnClickListener
//            }
//            if (TextUtils.isEmpty(binding.etOgrId.text.toString())){
//                return@setOnClickListener
//            }
//            if (TextUtils.isEmpty(binding.etOgrenciAdi.text.toString())){
//                return@setOnClickListener
//            }
            kitapTeslimAl()
            return@setOnClickListener
        }

//        val fm=supportFragmentManager
        val dialogFragment=OgrenciListFragment()
        binding.btOgrenciAra.setOnClickListener {
            dialogFragment.show(supportFragmentManager,"dialogFragment_TAG")
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//
//            if (resultCode == RESULT_OK) {
//                if (data!!.extras!!.containsKey("myValue")) {
//                    val myValue: String? = data.extras!!.getString("myValue")
//                    binding.etOgrenciAdi.setText(myValue)
//                    // Use the returned value
//
//            }
//        }
//    }



    private fun kitapAra() {
        Log.e("okunanDegerKitapAra",okunanDeger)
        postService = ApiClient.getClient().create(PostService::class.java)
        //ApiClient.getClient().create(com.erdemselvi.kutuphanetakipsistemi.googleApi.PostService::class.java)
        val isbnDegeri="isbn:"+okunanDeger
        val post =postService.listPost(isbnDegeri, resources.getString(R.string.GoogleBookAPIKey))
        post.enqueue(object : Callback<apiItems> {
            override fun onFailure(call: Call<apiItems>, t: Throwable) {
                Toast.makeText(applicationContext, t.message.toString(), Toast.LENGTH_LONG).show()
                Log.e("gelenVeri",t.message.toString())
            }

            override fun onResponse(call: Call<apiItems>, response: Response<apiItems>) {
                var kitapAdi=""
                var yazarAdi=""
//                var ozet:String=""
//                var yayinci:String=""
//                var sayfaSayisi:Int=0
//                var yayinTarihi:String=""
//                var dil:String="tr"
//                var derecelendirme:Int=0
//                var resimUrl:String=""
                try {
                    if (response.isSuccessful) {
                        val rB=response.body()!!

                        for (myData in rB.items){
//                           for (isbnn in myData.volumeInfo.industryIdentifiers){
//                               if (okunanDeger.toString()==isbnn.identifier){
                            kitapAdi=myData.volumeInfo.title
                            for (yazar in myData.volumeInfo.authors){
                                yazarAdi+=yazar
                            }
//                            ozet=myData.volumeInfo.description
//                            yayinci=myData.volumeInfo.publisher
//                            sayfaSayisi=myData.volumeInfo.pageCount
//                            yayinTarihi=myData.volumeInfo.publishedDate
//                            dil=myData.volumeInfo.language
//                            derecelendirme=myData.volumeInfo.ratingsCount
//                            resimUrl=myData.volumeInfo.imageLinks.smallThumbnail

//                               }
//                           }

//                        val listItems=myData.volumeInfo.title
//
//                        binding.tvOkunanDeger.text=listItems
//                        binding.etIsbn.setText(kitapAdi)
                            binding.etKitapAdi.setText(kitapAdi)
                            binding.etYazarAdi.setText(yazarAdi)
//                            binding.etOzet.setText(ozet)
//                            binding.etYayinci.setText(yayinci)
//                            binding.etSayfaSayisi.setText(sayfaSayisi.toString())
//                            binding.etYayinTarihi.setText(yayinTarihi)
//                            binding.etDil.setText(dil)
//                            binding.etDerecelendirme.setText(derecelendirme.toString())
//                            binding.etResimUrl.setText(resimUrl)

//                            Picasso.with(applicationContext)
//                                .load(resimUrl.toUri())
//
////                                .resize(400, 400)         //optional
////                                .centerCrop()                        //optional
//                                .into(binding.ivKitap)
                        }

//                    postList = (response.body() as MutableList<PostApi>?)!!
//                    Log.e("gelenVeri",postList.toString())
//                    binding.tvOkunanDeger.text=postList.toString()
                    }
                    else{
                        Toast.makeText(applicationContext, "İnternet veritabanında bu Kitap Bulunamadı!", Toast.LENGTH_LONG).show()
                    }
                }catch (e: Exception){
                    Toast.makeText(applicationContext, "Kitap Bulunamadı! Hata:${e.message.toString()}", Toast.LENGTH_LONG).show()
                    Log.e("çekilen veri",e.message.toString())
                }

            }
        })
    }


    //    private fun ogrenciAra(qrDeger:String) {
//
//    }
    fun degiskenKaydet(qr:String){
        qrDeger2=qr
        Log.e("qrdeger2",qrDeger2)
        Log.e("qrdeger",qrDeger)
    }

    fun kitapTeslimEt(){
//        kayitliKitapAra()
        sonTeslim()

    }
    fun sonTeslim(){
        var pushId=""
        Log.e("kitapteslimet","giriş yapıldı")
        val refA=database.getReference("emanet") //.orderByChild("ogrId").equalTo(binding.etOgrId.text.toString())
        refA.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (s in snapshot.children) {
                    if (TextUtils.isEmpty(binding.etIsbn.text.toString())){
                        return
                    }
                    val isbn = s.child("isbn").getValue(String::class.java).toString()
                    val ogrId = s.child("ogrId").getValue(String::class.java).toString()
                    val teslimTarihi = s.child("geriVermeTarihi").getValue(Long::class.java)
                    if (isbn == binding.etIsbn.text.toString() && ogrId == binding.etOgrId.text.toString()) {

                        binding.etPushId.setText(s.key.toString())
                        binding.etgeriVermeTarihi.setText(teslimTarihi.toString())
                        pushId = s.key.toString()
                        Log.e("pushId", pushId)
                        Log.e("teslimtarihi", teslimTarihi.toString())
                        Log.e("etgeriVermeTarihi1",binding.etgeriVermeTarihi.text.toString())

                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("Hata",error.message)
            }

        })
        Log.e("etgeriVermeTarihi2",binding.etgeriVermeTarihi.text.toString())

        if (binding.etgeriVermeTarihi.text.toString()!="0") {
            val refB = database.getReference("emanet").push()
            Log.e("ogrId", binding.etOgrId.text.toString())
            val emanet = Emanet2(binding.etKitapId.text.toString(),
                binding.etIsbn.text.toString(),
                binding.etOgrId.text.toString(),
                Date().time,
                0
            )
            refB.setValue(emanet)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(applicationContext, "Kitabı öğrenciye verebilirsiniz.", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(applicationContext, "Kayıt Başarısız!!", Toast.LENGTH_LONG).show()
                    }
                }
        }
        else{
            Toast.makeText(applicationContext, "Kitabı daha önce almış zaten!!", Toast.LENGTH_LONG).show()
        }
    }
    private fun kitapTeslimAl() {
        var pushId=""

        val ref=database.getReference("emanet") //.orderByChild("ogrId").equalTo(binding.etOgrId.text.toString())
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (s in snapshot.children) {
                    val isbn = s.child("isbn").getValue(String::class.java).toString()
                    val ogrId = s.child("ogrId").getValue(String::class.java).toString()
                    val teslimTarihi = s.child("geriVermeTarihi").getValue(Long::class.java)
                    if (isbn == binding.etIsbn.text.toString() && ogrId == binding.etOgrId.text.toString()) {

                        binding.etPushId.setText(s.key.toString())
                        binding.etgeriVermeTarihi.setText(teslimTarihi.toString())
                        pushId = s.key.toString()
                        Log.e("pushId", pushId)
                        Log.e("teslimtarihi", teslimTarihi.toString())

                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }

        })
        if (binding.etPushId.text.toString()!="" && binding.etgeriVermeTarihi.text.toString().toLong()==0L) {
            val update = mapOf("geriVermeTarihi" to Date().time) //
            val myRef2 = database.getReference("emanet").child(binding.etPushId.text.toString())
            myRef2.updateChildren(update)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(applicationContext,"Kitap Başarı ile teslim alındı",Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(applicationContext,"Kitap Teslim Alınamadı!!",Toast.LENGTH_LONG).show()
                    }
                }
        }
        else {
            if (binding.etgeriVermeTarihi.text.toString()!="0") {
                Toast.makeText(applicationContext,"Bu kitap daha önce teslim alınmış!!!",Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(applicationContext,"Kitap Teslim Alınamadı!!",Toast.LENGTH_LONG).show()
            }
        }
    }

    fun kayitliKitapAra(){
        emanet=ArrayList()
        val refEmanet=FirebaseDatabase.getInstance().getReference("emanet")
        refEmanet.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                emanet.clear()
                var a=0
                for (e in snapshot.children){
                    val emnt=e.getValue(Emanet2::class.java)
                    if (emnt!=null && emnt.geriVermeTarihi==0L && emnt.isbn==binding.etIsbn.text.toString()){
                        emanet.add(emnt)
                        a+=1

                    }
                }
                if (a!=0){
                      ktpAra()
                }
                else{
                    ktpAra2()
                }


                Log.e("emanet",emanet.toString())
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }
    fun ktpAra(){
        var sayac=0
        val myRef = database.getReference("kitaplar")
        myRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (s in snapshot.children) {
                    val kitap=s.getValue(Kitaplar::class.java)
                    Log.e("kitap",kitap.toString())

                    if (kitap!!.isbn.toString()==binding.etIsbn.text.toString()){
                        for (e in emanet){
                            if (s.key==e.kitapId){
                                kitapDurumu+=1
                                Log.e("kitapdurumu",kitapDurumu.toString())
                            }

                        }
                        if (kitapDurumu!=0){
                            val ref2=database.getReference("kitaplar").push()
                            val kitaps=Kitaplar(kitap.isbn,kitap.kitapAdi,kitap.yazarAdi,kitap.kisaOzet,kitap.kategori,kitap.sayfaSayisi,kitap.yayinTarihi,kitap.dil,kitap.derece,kitap.resimUrl)


                            ref2.setValue(kitaps).addOnCompleteListener {
                                if (it.isSuccessful){
                                    sonTeslim()
                                }
                            }
                        }
                        else{
                            binding.etKitapAdi.setText(kitap.kitapAdi)
                            binding.etYazarAdi.setText(kitap.yazarAdi)
                            sayac+=1
                            binding.etKitapId.setText(s.key)
                        }

                    }
//                    else{
//                        Toast.makeText(applicationContext,"Kitap Veritabanımızda Bulunamadı!! Kitap Ekleyerek devam edin.",Toast.LENGTH_LONG).show()
////                        binding.etKitapAdi.setText("")
////                        binding.etYazarAdi.setText("")
//                    }


                }
                if (sayac==0){
                    Toast.makeText(applicationContext,"Kitap Veritabanımızda Bulunamadı!! Kitap Ekleyerek devam edin.",Toast.LENGTH_LONG).show()
                    binding.etKitapAdi.setText("")
                    binding.etYazarAdi.setText("")
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
    fun ktpAra2(){
        var sayac=0
        val myRef = database.getReference("kitaplar")
        myRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (s in snapshot.children) {
                    val kitap=s.getValue(Kitaplar::class.java)
                    Log.e("kitap",kitap.toString())

                    if (kitap!!.isbn.toString()==binding.etIsbn.text.toString()){

                            binding.etKitapAdi.setText(kitap.kitapAdi)
                            binding.etYazarAdi.setText(kitap.yazarAdi)
                            sayac+=1
                            binding.etKitapId.setText(s.key)

                    }


                }
                if (sayac==0){
                    Toast.makeText(applicationContext,"Kitap Veritabanımızda Bulunamadı!! Kitap Ekleyerek devam edin.",Toast.LENGTH_LONG).show()
                    binding.etKitapAdi.setText("")
                    binding.etYazarAdi.setText("")
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}