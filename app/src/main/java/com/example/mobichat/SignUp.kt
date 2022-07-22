package com.example.mobichat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class SignUp : AppCompatActivity() {


    private lateinit var name : EditText
    private lateinit var loginEmail : EditText
    private lateinit var loginPassword : EditText
    private lateinit var signUp : Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()

        name = findViewById(R.id.loginName);
        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        signUp = findViewById(R.id.btn_signup);

        signUp.setOnClickListener{
            val nameEntered = name.text.toString()
            val email = loginEmail.text.toString()
            val password = loginPassword.text.toString()

            signUpUser(nameEntered,email, password)
        }
    }
    private fun signUpUser(nameEntered:String, email : String, password:String){
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //code for jump in to the home
                    AddUserToDataBase(nameEntered,email,mAuth.currentUser?.uid!!)
                    val intent = Intent(this@SignUp,MainActivity::class.java )
                    finish()
                    startActivity(intent)

                } else {
                        Toast.makeText(this@SignUp, "Error Occurred", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun AddUserToDataBase(nameEntered:String,email:String,uid:String)
    {
            mDbRef = FirebaseDatabase.getInstance().getReference()
            mDbRef.child("User").child(uid).setValue(User(nameEntered, email,uid))

    }


}