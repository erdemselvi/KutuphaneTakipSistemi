package com.erdemselvi.kutuphanetakipsistemi

import android.graphics.Bitmap
import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import com.erdemselvi.kutuphanetakipsistemi.databinding.ActivityQrKodBinding
import com.google.firebase.auth.FirebaseAuth

class QrKodActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    private lateinit var binding: ActivityQrKodBinding
    lateinit var qrgEncoder: QRGEncoder
    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_qr_kod)
        binding= ActivityQrKodBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        mAuth=FirebaseAuth.getInstance()
        val currentUser=mAuth.currentUser

        val donusenKelime=currentUser?.uid

        val manager=getSystemService(WINDOW_SERVICE) as WindowManager
        val display=manager.defaultDisplay
        val point= Point()
        display.getSize(point)
        val width:Int=point.x
        val height:Int=point.y
        //generating dimension from width and height.
        var dimen = if (width < height) width else height
        dimen = dimen * 3 / 4
        qrgEncoder= QRGEncoder(donusenKelime,null,
            QRGContents.Type.TEXT,dimen)
        try {
            bitmap=qrgEncoder.encodeAsBitmap()
            binding.idIVQrcode.setImageBitmap(bitmap)
        }
        catch (e:Exception){
            Toast.makeText(
                this,
                "QR Code oluşturulamadı. Hata:${e.toString()}",
                Toast.LENGTH_SHORT
            ).show()
        }

    }
}