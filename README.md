# PersonalFinanceApp
This app usually tracks the income and expenses of the user 
Personal Finance App

A simple and clean Personal Finance Management Android application built using Kotlin, RecyclerView, and Firebase Firestore.
It allows users to add, view, and delete transactions such as expenses and income, helping them track where their money goes.

🚀 Features
✔ Add Transactions

Users can add a transaction with:

Title

Amount

Type (Income / Expense)

✔ View Transactions

Displays all transactions in a RecyclerView with a custom item layout.

✔ Delete Transactions

Each transaction can be deleted using a delete icon.
The item is removed from:

Firebase Firestore

RecyclerView list (real-time UI update)

✔ Firebase Firestore Integration

All transactions are stored securely in Firestore under the collection transactions.

🧩 Tech Stack

Kotlin

Android Jetpack

RecyclerView

LiveData / ViewModel (optional depending on your project structure)

Firebase Firestore

Material UI Components

📂 Project Structure
/personalfinance
│── Transaction.kt          # Data class for transactions
│── AddTransactionActivity  # UI to add new transactions
│── TransactionAdapter.kt   # RecyclerView adapter
│── item_transaction.xml    # Item layout for list
│── activity_add_transaction.xml
│── Firebase setup configs

🔧 How Deletion Works

When a user taps delete:

The app removes the document from Firestore using the transaction ID.

On success, it removes the item from the adapter list.

RecyclerView is updated with:

notifyItemRemoved(position)

notifyItemRangeChanged(position, newSize)

🛑 Common Issue (Solved)

Crash: IndexOutOfBoundsException: Index 0 out of bounds for length 0
This happens if the adapter tries to remove an item using a stale position after the list changes.
Handled by ensuring:

Valid position

Safe remove

UI updated correctly



📥 Setup Instructions

Clone the repo

Add your Firebase google-services.json

Enable Firestore in Firebase Console

Run the app on Android Studio
