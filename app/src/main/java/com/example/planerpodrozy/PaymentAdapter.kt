package com.example.planerpodrozy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PaymentAdapter(private val payments: List<String>, private val listener: (String) -> Unit) : RecyclerView.Adapter<PaymentAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val paymentTextView: TextView = view.findViewById(R.id.textView_nameAmount)

        fun bind(payment: String) {
            paymentTextView.text= payments.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_payment, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(payments[position])
    }

    override fun getItemCount() = payments.size
}