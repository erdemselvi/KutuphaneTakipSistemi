package com.erdemselvi.kutuphanetakipsistemi

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.erdemselvi.kutuphanetakipsistemi.databinding.ActivityGirisBinding
import com.erdemselvi.kutuphanetakipsistemi.databinding.ActivityTelefonDogrulamaBinding
import com.erdemselvi.kutuphanetakipsistemi.model.Ogretmen
import com.erdemselvi.kutuphanetakipsistemi.model.Yonetici2
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@Suppress("DEPRECATION")
class GirisActivity : AppCompatActivity() {

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val Req_Code:Int=123
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mAuth: FirebaseAuth

    private lateinit var binding:ActivityGirisBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_giris)
        binding= ActivityGirisBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient= GoogleSignIn.getClient(this,gso)

        firebaseAuth= FirebaseAuth.getInstance()
        val currentUser=firebaseAuth.currentUser
        Log.e("kullanıcı",currentUser?.email.toString())
        val prefences = getSharedPreferences("users", Context.MODE_PRIVATE)
        val yetki = prefences.getString("yetki","yok")
//        val uid = prefences.getString("uid","ZmqEayAxqwahF6s3bIQ9yF3dhgC3")
        if (currentUser!=null && yetki=="öğrenci" ){
            startActivity(Intent(this, AnaActivity::class.java))
            finish()
        }
        if (currentUser!=null && (yetki=="öğretmen" || yetki=="yönetici")){
            Log.e("kullanıcı",currentUser.email.toString())
            startActivity(Intent(this, DashBoardActivity::class.java))
            finish()
        }

//        binding.signInButton.setOnClickListener{
//            signInGoogle()
//        }
        binding.cvOgretmenGirisi.setOnClickListener {
            signInGoogle()
        }

        binding.cvOgrenciGirisi.setOnClickListener {
            val intent=Intent(this,TelefonDogrulamaActivity::class.java)
            startActivity(intent)
            finish()

        }
    }

    private  fun signInGoogle(){

        val signInIntent: Intent =mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent,Req_Code)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==Req_Code){
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
//            firebaseAuthWithGoogle(account!!)
        }
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>){
        try {
            val account: GoogleSignInAccount? =completedTask.getResult(ApiException::class.java)
            val credential= GoogleAuthProvider.getCredential(account?.idToken,null)
            firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser=firebaseAuth.currentUser
                    val database= FirebaseDatabase.getInstance()
                    var sayac=0
                    val ref=database.getReference("ogretmenler")
                    ref.addValueEventListener(@SuppressLint("SuspiciousIndentation")
                    object : ValueEventListener {
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onDataChange(snapshot: DataSnapshot) {
                            var okulId=""
                            for (s in snapshot.children){
                                val yon=s.getValue(Ogretmen::class.java)

                                if (yon!=null) {
                                    if (currentUser!!.email==yon.email){
                                        sayac++
                                        okulId=yon.okulId.toString()
                                    }
                                }
                            }
                            if (sayac!=0) {
                                val preferences=getSharedPreferences("users", Context.MODE_PRIVATE)
                                val editor = preferences.edit()
                                editor.putString("uid",currentUser!!.uid)
                                editor.putString("okulId",okulId)

                                if (currentUser.uid=="JEzFglFF46RBUaIWdlavDjR9cNC2" /*|| currentUser.uid=="D5porFnIFmgGQ2xrEsc0qgqJnZx2"*/){
                                    editor.putString("yetki","yönetici")
                                }
                                else{
                                    editor.putString("yetki","öğretmen")
                                }
                                editor.apply()
                                val intent = Intent(this@GirisActivity, DashBoardActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            else{
                                val preferences=getSharedPreferences("users", Context.MODE_PRIVATE)
                                val editor = preferences.edit()
                                editor.putString("uid",currentUser!!.uid)

                                if (currentUser.uid=="JEzFglFF46RBUaIWdlavDjR9cNC2" /*|| currentUser.uid=="D5porFnIFmgGQ2xrEsc0qgqJnZx2"*/){
                                    editor.putString("yetki","yönetici")
                                }
                                else{
                                    editor.putString("yetki","öğretmen")
                                }
                                editor.apply()
                                val intent = Intent(this@GirisActivity, OgretmenKayitActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }

                    })

//                    mAuth = FirebaseAuth.getInstance()
//                    Log.e("kullanıcı",mAuth.currentUser?.email.toString())
//                    val intent = Intent(this, DashBoardActivity::class.java)
//                    startActivity(intent)
//                    finish()

                }
                /*

             */

            }
        } catch (e: ApiException){

            Toast.makeText(this,e.toString(), Toast.LENGTH_SHORT).show()
            Log.e("hata${FirebaseAuth.getInstance().currentUser?.displayName}",e.toString())
        }
    }
}