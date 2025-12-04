package com.example.financeapp2.personalfinance



data class Transaction(
    var id: String = "",
    var title: String = "",
    var amount: Double = 0.0,
    var type: String = "Expense", // "Income" or "Expense"
    var timestamp: Long = System.currentTimeMillis()
)
