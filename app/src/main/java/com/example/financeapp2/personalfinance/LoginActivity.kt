package com.example.financeapp2.personalfinance

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.financeapp2.MainActivity
import com.example.financeapp2.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var loginBtn: MaterialButton
    private lateinit var goToRegister: MaterialButton
    private lateinit var email: EditText
    private lateinit var password: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        loginBtn = findViewById(R.id.loginBtn)
        goToRegister = findViewById(R.id.goToRegister)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)

        loginBtn.setOnClickListener {
            val emailText = email.text.toString().trim()
            val pwdText = password.text.toString().trim()

            if(emailText.isEmpty()){
                email.error="Email is required"
                return@setOnClickListener
            }
            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()){
               email.error="Enter a valid email"
               return@setOnClickListener
            }
            if(pwdText.isEmpty()){
                password.error="Password is required"
                return@setOnClickListener
            }
            auth.signInWithEmailAndPassword(emailText, pwdText)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }

        }

        goToRegister.setOnClickListener {
        startActivity(Intent(this,RegisterActivity::class.java))
        }
    }
}
