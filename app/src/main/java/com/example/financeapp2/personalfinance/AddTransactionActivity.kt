package com.example.financeapp2.personalfinance

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.financeapp2.R
import com.google.firebase.firestore.FirebaseFirestore

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etAmount: EditText
    private lateinit var spinnerType: Spinner
    private lateinit var btnSave: Button

    private val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        etTitle = findViewById(R.id.etTitle)
        etAmount = findViewById(R.id.etAmount)
        spinnerType = findViewById(R.id.spinnerType)
        btnSave = findViewById(R.id.btnSave)

        // Spinner setup
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            listOf("Expense", "Income")
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = adapter

        btnSave.setOnClickListener {
            saveTransaction()
        }
    }

    private fun saveTransaction() {
        val title = etTitle.text.toString().trim()
        val amountStr = etAmount.text.toString().trim()
        val type = spinnerType.selectedItem.toString()

        if (title.isEmpty()) {
            etTitle.error = "Enter title"
            return
        }
        if (amountStr.isEmpty()) {
            etAmount.error = "Enter amount"
            return
        }

        val amount = amountStr.toDoubleOrNull()
        if (amount == null) {
            etAmount.error = "Invalid number"
            return
        }

        val doc = db.collection("transactions").document()
        val id = doc.id

        val data = Transaction(
           id=id,
            title = title,
            amount = amount,
            type = type
        )

        doc.set(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show()
                finish()
            }

    }
}
