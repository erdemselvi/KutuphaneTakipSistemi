package com.erdemselvi.kutuphanetakipsistemi

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import com.erdemselvi.kutuphanetakipsistemi.databinding.ActivityTelefonDogrulamaBinding
import com.erdemselvi.kutuphanetakipsistemi.model.Ogrenciler
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.concurrent.TimeUnit


class TelefonDogrulamaActivity : AppCompatActivity() {

    private lateinit var binding:ActivityTelefonDogrulamaBinding
    private lateinit var auth:FirebaseAuth
    lateinit var storedVerificationId:String

    lateinit var database: FirebaseDatabase
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding=ActivityTelefonDogrulamaBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        binding.pbGiris.visibility=View.VISIBLE
        auth=FirebaseAuth.getInstance()
        database= FirebaseDatabase.getInstance()
        val currentUser=auth.currentUser
        if (auth.currentUser!=null){

            //Burda; daha önce kayıtlı ise bir daha profil sayfasını görmeden direk anaActivite ye gidecek
//            val myRef=database.getReference("ogrenciler")
//            myRef.addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    //           var profil=ArrayList<Ogrenciler>()
//                    for (c in snapshot.children) {
//                        Log.e("snapshot", c.toString())
//                        val profil = c.getValue(Ogrenciler::class.java)
//                        Log.e("profil", profil.toString())
//                        if (profil != null) {
//                            Log.e("currentUser", currentUser!!.uid)
//                            if (profil.ogrId.toString() == currentUser.uid) {
//                                val intent = Intent(this@MainActivity, AnaActivity::class.java)
//                                startActivity(intent)
//                                finish()
//                            }
//                        }
//                    }
//
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//
//                }
//
//            })
/////////


//            val myRef=database.getReference("0").child("ogrenciler").child(currentUser!!.uid)
////            if (myRef!=null){
////                val intent=Intent(this,AnaActivity::class.java)
////                startActivity(intent)
////                finish()
////            }else{
////                val intent=Intent(this,ProfilActivity::class.java)
////                startActivity(intent)
////                finish()
////            }
//
//                myRef.addValueEventListener(object :ValueEventListener{
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        var profil=ArrayList<Ogrenciler>()
//                        for (c in snapshot.children){
//                            Log.e("snapshot",c.toString())
////                            profil=c.getValue(Ogrenciler::class.java)
////                            Log.e("profil",profil.toString())
//                            if (c!=null){
//                                binding.pbGiris.visibility=View.GONE
//                                val intent=Intent(this@MainActivity,AnaActivity::class.java)
//                                startActivity(intent)
//                                finish()
//                            }else{
//                                binding.pbGiris.visibility=View.GONE
//                                val intent=Intent(this@MainActivity,ProfilActivity::class.java)
//                                startActivity(intent)
//                                finish()
//                            }
//                        }
//
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//
//                    }
//
//                })
            binding.pbGiris.visibility=View.GONE
        }
        binding.btDogrula.setOnClickListener {
            if (TextUtils.isEmpty(binding.etTelefon.text.toString())) {
                Toast.makeText(this,"Boş Bırakmayın",Toast.LENGTH_SHORT).show()
            }
            else if (binding.etTelefon.text.toString().length<10){
                Toast.makeText(this,"Numaranızı eksik girmeyin",Toast.LENGTH_SHORT).show()
            }
            else{
                binding.btOnay.isClickable=true
                binding.btOnay.isEnabled=true

                val telNo=binding.etTelefon.text.toString()
                dogrula(telNo)
            }
        }
        binding.btOnay.setOnClickListener {
            if (TextUtils.isEmpty(binding.etKod.text.toString())) {
                Toast.makeText(this,"Boş Bırakmayın",Toast.LENGTH_SHORT).show()
            }
            else{
                val kod=binding.etKod.text.toString()
                verifyCode(kod)
            }
        }
        binding.pbGiris.visibility=View.GONE
    }

    private fun dogrula(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+90"+phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
 val   callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(@NonNull credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
//            Log.d(TAG, "onVerificationCompleted:$credential")
//            signInWithPhoneAuthCredential(credential)
            val code=credential.smsCode
            if (code!=null){

                verifyCode(code)
            }
        }

        override fun onVerificationFailed(@NonNull e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(TAG, "onVerificationFailed", e)
            Log.e("hata",e.toString())
            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Log.e("hata1",e.toString())
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Log.e("hata2",e.toString())
            }
//
//            // Show a message and update the UI
            Toast.makeText(applicationContext,"Doğrulama Hatalı",Toast.LENGTH_SHORT).show()

        }

        override fun onCodeSent(@NonNull
            verificationId: String, @NonNull
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(TAG, "onCodeSent:$verificationId")

            // Save verification ID and resending token so we can use them later
            super.onCodeSent(verificationId,token)
            storedVerificationId = verificationId
 //           resendToken = token
        }
    }

    private fun verifyCode(code: String) {
        val credential=PhoneAuthProvider.getCredential(storedVerificationId,code)
        binding.etKod.setText(code)
        girisYap(credential)

    }

    private fun girisYap(credential: PhoneAuthCredential) {
        val firebaseAuth=FirebaseAuth.getInstance()
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(applicationContext,"Doğrulama Başarılı",Toast.LENGTH_SHORT).show()
                database= FirebaseDatabase.getInstance()
                val currentUser=auth.currentUser
                //Burda; daha önce kayıtlı ise bir daha profil sayfasını görmeden direk anaActivite ye gidecek
//                val myRef=database.getReference("ogrenciler")
//                myRef.addValueEventListener(object : ValueEventListener {
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        //           var profil=ArrayList<Ogrenciler>()
//                        for (c in snapshot.children){
//                            Log.e("snapshot",c.toString())
//                            val  profil=c.getValue(Ogrenciler::class.java)
//                            Log.e("profil",profil.toString())
//                            if (profil!=null){
//                                Log.e("currentUser",currentUser!!.uid)
//                                if (profil.ogrId.toString()==currentUser.uid || profil.telNo.toString()==("+90"+binding.etTelefon.text.toString())) {
//                                    val preferences=getSharedPreferences("users", Context.MODE_PRIVATE)
//                                    val editor = preferences.edit()
//                                    editor.putString("uid",currentUser.uid)
//                                    editor.putString("okulId",profil.okulId)
//                                    editor.putString("yetki","öğrenci")
//                                    editor.apply()
//                                    val intent = Intent(this@TelefonDogrulamaActivity, AnaActivity::class.java)
//                                    startActivity(intent)
//                                    finish()
//                                }
//                                else{
//                                    val preferences=getSharedPreferences("users", Context.MODE_PRIVATE)
//                                    val editor = preferences.edit()
//                                    editor.putString("uid",currentUser.uid)
//                                    editor.putString("yetki","öğrenci")
//                                    editor.apply()
//                                    val intent= Intent(this@TelefonDogrulamaActivity,ProfilActivity::class.java)
//                                    startActivity(intent)
//                                    finish()
//                                }
//                            }
//                        }
//
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//
//                    }
//
//                })

                var sayac=0
                var okulId=""
                //Burda; daha önce kayıtlı ise bir daha profil sayfasını görmeden direk anaActivite ye gidecek
                val myRef=database.getReference("ogrenciler")
                myRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        //           var profil=ArrayList<Ogrenciler>()
                        for (c in snapshot.children) {
                            Log.e("snapshot", c.toString())
                            val profil = c.getValue(Ogrenciler::class.java)
                            Log.e("profil", profil.toString())
                            if (profil != null) {
                                Log.e("currentUser", currentUser!!.uid)
                                if (profil.ogrId.toString() == currentUser.uid) {
                                    okulId=profil.okulId.toString()
                                    sayac++
                                }
                            }
                        }
                        if (sayac!=0) {
                                    val preferences=getSharedPreferences("users", Context.MODE_PRIVATE)
                                    val editor = preferences.edit()
                                    editor.putString("uid",currentUser!!.uid)
                                    editor.putString("okulId",okulId)
                                    editor.putString("yetki","öğrenci")
                                    editor.apply()
                            val intent = Intent(this@TelefonDogrulamaActivity, AnaActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else{
                            val preferences=getSharedPreferences("users", Context.MODE_PRIVATE)
                            val editor = preferences.edit()
                            editor.putString("uid",currentUser!!.uid)
                            editor.putString("yetki","öğrenci")
                            editor.apply()
                            val intent= Intent(this@TelefonDogrulamaActivity,ProfilActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })

/////////
//                val myRef=database.getReference("0").child("ogrenciler").child(currentUser!!.uid)
//                myRef.addValueEventListener(object : ValueEventListener {
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        for (c in snapshot.children){
//                            Log.e("snapshot",c.toString())
////                            profil=c.getValue(Ogrenciler::class.java)
////                            Log.e("profil",profil.toString())
//                            if (c!=null){
//                                val intent= Intent(this@MainActivity,AnaActivity::class.java)
//                                startActivity(intent)
//                                finish()
//                            }else{
//                                val intent= Intent(this@MainActivity,ProfilActivity::class.java)
//                                startActivity(intent)
//                                finish()
//                            }
//                        }
//
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                        val intent= Intent(this@MainActivity,ProfilActivity::class.java)
//                        startActivity(intent)
//                        finish()
//                    }
//
//
//                })
//                val intent=Intent(this,ProfilActivity::class.java)
//                startActivity(intent)
//                finish()
            }
            else{
                Toast.makeText(applicationContext,"Son anda hata oluştu",Toast.LENGTH_SHORT).show()
            }
        }
    }

}