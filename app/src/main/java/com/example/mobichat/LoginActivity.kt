package com.example.mobichat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {

    private lateinit var loginEmail : EditText
    private lateinit var loginPassword : EditText
    private lateinit var loginButton: Button
    private lateinit var signUp : TextView
    private lateinit var mAuth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        loginButton = findViewById(R.id.btn_login);
        signUp = findViewById(R.id.btn_signup);
        signUp.setOnClickListener{
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
        mAuth = FirebaseAuth.getInstance()
        loginButton.setOnClickListener{
            val email = loginEmail.text.toString()
            val password = loginPassword.text.toString()

            login(email, password)
        }
        signUp.setOnClickListener {
            val intent  =  Intent(this@LoginActivity, SignUp::class.java)
            finish()
            startActivity(intent)
        }
    }

    private fun login(email :String, password : String)
    {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this@LoginActivity,MainActivity::class.java )
                    finish()
                    startActivity(intent)

                } else {
                    // If sign in fails, display a message to the user.
                 Toast.makeText(this@LoginActivity,"User Does Not Exist", Toast.LENGTH_SHORT).show()

                }
            }
    }
}