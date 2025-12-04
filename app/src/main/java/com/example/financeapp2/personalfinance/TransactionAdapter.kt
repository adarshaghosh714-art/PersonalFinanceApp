package com.example.financeapp2.personalfinance

import com.example.financeapp2.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class TransactionAdapter(
    private var items: MutableList<Transaction>
) : RecyclerView.Adapter<TransactionAdapter.VH>() {

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitleItem)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmountItem)
        val tvType: TextView = itemView.findViewById(R.id.tvTypeItem)

        val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]

        holder.tvTitle.text = item.title
        holder.tvAmount.text = "₹%.2f".format(item.amount)
        holder.tvType.text = item.type


        holder.btnDelete.setOnClickListener {
            deleteTransaction(item, position, holder)
        }
    }

    override fun getItemCount(): Int = items.size


    private fun deleteTransaction(
        item: Transaction,
        position: Int,
        holder: VH
    ) {
        val db = FirebaseFirestore.getInstance()

        if (item.id.isEmpty()) {
            Toast.makeText(holder.itemView.context,
                "Error: Document ID missing", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("transactions")
            .document(item.id)
            .delete()
            .addOnSuccessListener {

                val pos = holder.adapterPosition
                if (pos == RecyclerView.NO_POSITION || pos >= items.size) return@addOnSuccessListener

                items.removeAt(pos)

                notifyItemRemoved(pos)
                notifyItemRangeChanged(pos, items.size)

                Toast.makeText(holder.itemView.context,
                    "Deleted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(holder.itemView.context,
                    "Delete Failed", Toast.LENGTH_SHORT).show()
            }
    }


    fun setItems(newItems: List<Transaction>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
