package com.example.financeapp2

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.financeapp2.personalfinance.AddTransactionActivity
import com.example.financeapp2.personalfinance.Transaction
import com.example.financeapp2.personalfinance.TransactionAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class MainActivity : AppCompatActivity() {

    private lateinit var rv: RecyclerView
    private lateinit var tvBalance: TextView
    private lateinit var addBtn: FloatingActionButton
    private lateinit var adapter: TransactionAdapter

    private val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private var listenerRegistration: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase (required)
        FirebaseApp.initializeApp(this)

        setContentView(R.layout.activity_main)

        // UI
        tvBalance = findViewById(R.id.tvBalance)
        rv = findViewById(R.id.rvTransactions)
        addBtn = findViewById(R.id.btnAdd)

        // Setup Adapter
        adapter = TransactionAdapter(mutableListOf())
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        // Add Transaction
        addBtn.setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java))
        }

        // Listen to Firestore live updates
        listenForTransactions()
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration?.remove()
    }

    private fun listenForTransactions() {
        listenerRegistration = db.collection("transactions")
            .orderBy("timestamp")  // make sure timestamp exists
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    return@addSnapshotListener
                }

                val tempList = mutableListOf<Transaction>()
                var balance = 0.0

                snapshot?.forEach { doc ->

                    val transaction = doc.toObject(Transaction::class.java)
                    transaction.id = doc.id // VERY IMPORTANT

                    tempList.add(transaction)

                    if (transaction.type == "Income") {
                        balance += transaction.amount
                    } else {
                        balance -= transaction.amount
                    }
                }

                // Show newest first
                adapter.setItems(tempList.reversed())

                // Display balance
                tvBalance.text = "Balance: ₹%.2f".format(balance)
            }
    }
}
