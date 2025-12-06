package com.example.financeapp2

import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.financeapp2.personalfinance.AddTransactionActivity
import com.example.financeapp2.personalfinance.Transaction
import com.example.financeapp2.personalfinance.TransactionAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class MainActivity : AppCompatActivity() {

    private lateinit var rv: RecyclerView
    private lateinit var tvBalance: TextView
    private lateinit var addBtn: FloatingActionButton
    private lateinit var downloadBtn: MaterialButton

    private lateinit var adapter: TransactionAdapter

    private val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private var listenerRegistration: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_main)

        // UI
        tvBalance = findViewById(R.id.tvBalance)
        rv = findViewById(R.id.rvTransactions)
        addBtn = findViewById(R.id.btnAdd)
        downloadBtn = findViewById(R.id.btnDownloadCSV)

        // Setup RecyclerView
        adapter = TransactionAdapter(mutableListOf())
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        // Add new transaction
        addBtn.setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java))
        }

        // Listen to Firestore for live updates
        listenForTransactions()

        // CSV download click
        downloadBtn.setOnClickListener {
            fetchTransactions { list ->
                val csv = generateCSV(list)
                saveCSVToDownloads(csv)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration?.remove()
    }

    private fun listenForTransactions() {
        listenerRegistration = db.collection("transactions")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->

                if (error != null) return@addSnapshotListener

                val tempList = mutableListOf<Transaction>()
                var balance = 0.0

                snapshot?.forEach { doc ->
                    val transaction = doc.toObject(Transaction::class.java)
                    transaction.id = doc.id
                    tempList.add(transaction)

                    if (transaction.type == "Income") {
                        balance += transaction.amount
                    } else {
                        balance -= transaction.amount
                    }
                }

                // Newest first
                adapter.setItems(tempList.reversed())

                // Balance UI
                tvBalance.text = "Balance: ₹%.2f".format(balance)
            }
    }

    private fun fetchTransactions(onResult: (List<Transaction>) -> Unit) {
        db.collection("transactions")
            .get()
            .addOnSuccessListener { result ->
                val list = result.toObjects(Transaction::class.java)
                onResult(list)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to fetch data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun generateCSV(transactions: List<Transaction>): String {
        val sb = StringBuilder()
        sb.append("Title,Amount,Type\n")

        for (t in transactions) {
            sb.append("${t.title},${t.amount},${t.type}\n")
        }
        return sb.toString()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveCSVToDownloads(csv: String) {
        val fileName = "transactions_${System.currentTimeMillis()}.csv"

        val resolver = contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, "text/csv")
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            resolver.openOutputStream(it)?.use { output ->
                output.write(csv.toByteArray())
                Toast.makeText(this, "CSV saved to Downloads", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
