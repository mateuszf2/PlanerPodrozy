package com.example.planerpodrozy

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class FinanseAdapter : ListAdapter<Finanse, FinanseAdapter.FinanseViewHolder>(FinanseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FinanseViewHolder {
        val itemView= LayoutInflater.from(parent.context).inflate(R.layout.item_finanse, parent, false)
        return FinanseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FinanseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FinanseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val finanseName: TextView = itemView.findViewById(R.id.textView_finanseName)
        private val amountFinanse: TextView= itemView.findViewById(R.id.textView_amountFinanse)

        fun bind(finanse: Finanse) {
            finanseName.text= finanse.finanseName
            amountFinanse.text= finanse.amountFinanse.toString()
        }
    }

    class FinanseDiffCallback : DiffUtil.ItemCallback<Finanse>() {
        override fun areItemsTheSame(oldItem: Finanse, newItem:Finanse): Boolean {
            return oldItem.finanseName == newItem.finanseName
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Finanse, newItem: Finanse): Boolean {
            return oldItem==newItem
        }
    }
}