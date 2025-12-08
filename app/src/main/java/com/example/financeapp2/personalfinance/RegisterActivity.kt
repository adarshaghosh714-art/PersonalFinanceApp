package com.example.financeapp2.personalfinance

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.financeapp2.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var registerBtn : MaterialButton
    private lateinit var regEmail : EditText
    private lateinit var regPassword : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        registerBtn=findViewById(R.id.registerBtn)
        regEmail=findViewById(R.id.regEmail)
        regPassword=findViewById(R.id.regPassword)
        registerBtn.setOnClickListener {
            val email=regEmail.text.toString().trim()
            val pwd= regPassword.text.toString().trim()

            auth.createUserWithEmailAndPassword(email,pwd)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }else{
                        Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }

        }
        }
    }
