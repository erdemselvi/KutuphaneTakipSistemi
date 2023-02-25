package com.erdemselvi.kutuphanetakipsistemi

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.erdemselvi.kutuphanetakipsistemi.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    private lateinit var binding: ActivityMainBinding
    //   var tarananDeger=""
//    private lateinit var bitmap: Bitmap
//    lateinit var qrgEncoder: QRGEncoder

//    lateinit var postService: PostService

//    private val barcodeLauncher = registerForActivityResult(
//        ScanContract()
//    ) { result: ScanIntentResult ->
//        if (result.contents == null) {
//            val originalIntent = result.originalIntent
//            if (originalIntent == null) {
//                Log.d("MainActivity", "Tarama İptal Edildi")
//                Toast.makeText(this@MainActivity, "İptal Edildi", Toast.LENGTH_LONG).show()
//            } else if (originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
//                Log.d(
//                    "MainActivity",
//                    "Eksik kamera izni nedeniyle iptal edilen tarama"
//                )
//                Toast.makeText(
//                    this@MainActivity,
//                    "Eksik kamera izni nedeniyle iptal edildi",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//        } else {
//            tarananDeger=result.contents
//            val intent = Intent(this, KitapEkleActivity::class.java)
//            intent.putExtra("tarananDeger",tarananDeger)
//            startActivity(intent)
//            Log.d("MainActivity", "Tarandı")
////            Toast.makeText(
////                this@MainActivity,
////                "Tarandı: " + result.contents,
////                Toast.LENGTH_LONG
////            ).show()
//        }
//    }


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        binding=ActivityMainBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)


//        if (tarananDeger!=null){
//
//        }
        binding.btBarcode.setOnClickListener {
            val intent = Intent(this, KitapEkleActivity::class.java)
//            intent.putExtra("tarananDeger",tarananDeger)
            startActivity(intent)
        //ekran yatay oluyor ancak barcode ve qrcode iyi okuyor.
//            barcodeLauncher.launch(ScanOptions())

        //           IntentIntegrator(this).initiateScan();

            //Dikey veya yatay istediğimiz gibi açılması için, ancak qr code okumadı
//            val options = ScanOptions()
//            options.captureActivity = AnyOrientationCaptureActivity::class.java
//            options.setDesiredBarcodeFormats(ScanOptions.ONE_D_CODE_TYPES)
//            options.setPrompt("Kitap Barkodunu Tarayın")
//            options.setOrientationLocked(false)
//            options.setBeepEnabled(false)
//            barcodeLauncher.launch(options)
        }

        binding.idBtnGenerateQR.setOnClickListener {
//            if (TextUtils.isEmpty(binding.idEdt.text.toString())){
//
//                //if the edittext inputs are empty then execute this method showing a toast message.
//                Toast.makeText(
//                    this@MainActivity,
//                    "QR Kodu oluşturmak için bir metin girin",
//                    Toast.LENGTH_SHORT
//                ).show()
//
//            } else {
//                val manager=getSystemService(WINDOW_SERVICE) as WindowManager
//                val display=manager.defaultDisplay
//                val point= Point()
//                display.getSize(point)
//                val width:Int=point.x
//                val height:Int=point.y
//                //generating dimension from width and height.
//                var dimen = if (width < height) width else height
//                dimen = dimen * 3 / 4
//                qrgEncoder= QRGEncoder(binding.idEdt.text.toString(),null,
//                    QRGContents.Type.TEXT,dimen)
//                try {
//                    bitmap=qrgEncoder.encodeAsBitmap()
//                    binding.idIVQrcode.setImageBitmap(bitmap)
//                }
//                catch (e:Exception){
//                    Toast.makeText(
//                        this@MainActivity,
//                        "QR Code oluşturulamadı. Hata:${e.toString()}",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
            val intent = Intent(this, BarcodeActivity::class.java)
            startActivity(intent)
        }
        mAuth=FirebaseAuth.getInstance()
        val currentUser=mAuth.currentUser
        binding.tvCikis.text="${currentUser?.email} (Çıkış Yap)"

        binding.tvCikis.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(this, GirisActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.btDashboard.setOnClickListener {
            val intent = Intent(this, DashBoardActivity::class.java)
            startActivity(intent)
        }
        binding.btKitapTeslimEt.setOnClickListener {
            val intent = Intent(this, KitapTeslimActivity::class.java)
            startActivity(intent)
        }
        binding.btKitapVer.setOnClickListener {
            val intent = Intent(this, KitapVerActivity::class.java)
            startActivity(intent)
        }
        binding.btKitapAl.setOnClickListener {
            val intent = Intent(this, KitapAlActivity::class.java)
            startActivity(intent)
        }
        binding.btOgrKaydet.setOnClickListener {
            val intent = Intent(this, OgrenciEkleActivity::class.java)
            startActivity(intent)
        }
        binding.btKitapListele.setOnClickListener {
            val intent = Intent(this, KitapListeleActivity::class.java)
            startActivity(intent)
        }
        binding.btOgrenciListele.setOnClickListener {
            val intent = Intent(this, OgrenciListeleActivity::class.java)
            startActivity(intent)
        }
        binding.btEnCokListele.setOnClickListener {
            val intent = Intent(this, EnCokActivity::class.java)
            startActivity(intent)
        }
        binding.btEnCokSayfa.setOnClickListener {
            val intent = Intent(this, EnCokSayfaActivity::class.java)
            startActivity(intent)
        }
        binding.btEnCokKitap.setOnClickListener {
            val intent = Intent(this, EnCokKitapActivity::class.java)
            startActivity(intent)
        }
//        postService = com.erdemselvi.kutuphanetakipsistemi.googleApi.ApiClient.getClient().create(PostService::class.java)
//        //ApiClient.getClient().create(com.erdemselvi.kutuphanetakipsistemi.googleApi.PostService::class.java)
//        val post =postService.listPost("9780140328721", Constants.apiKey)
//
//        post.enqueue(object : Callback<apiItems> {
//            override fun onFailure(call: Call<apiItems>, t: Throwable) {
//                Toast.makeText(applicationContext, t.message.toString(), Toast.LENGTH_LONG).show()
//                Log.e("gelenVeri",t.message.toString())
//            }
//
//            override fun onResponse(call: Call<apiItems>, response: Response<apiItems>) {
//
//                if (response.isSuccessful) {
//                    val rB=response.body()!!
//
//                    for (myData in rB.items){
//                        val listItems=myData.volumeInfo.title
//
//                            binding.idEdt.setText(listItems)
//
//
//                    }
//
////                    postList = (response.body() as MutableList<PostApi>?)!!
////                    Log.e("gelenVeri",postList.toString())
////                    binding.tvOkunanDeger.text=postList.toString()
//                }
//            }
//        })
//
    }
}