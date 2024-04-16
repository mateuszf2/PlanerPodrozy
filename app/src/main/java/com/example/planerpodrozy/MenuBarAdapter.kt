package com.example.planerpodrozy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MenuBarAdapter(private val options: Array<String>, private val listener: (String) -> Unit) : RecyclerView.Adapter<MenuBarAdapter.ViewHolder>(){
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val optionTextView: TextView= view.findViewById(R.id.textView_menuItem)

        fun bind(option: String) {
            optionTextView.text= option
            itemView.setOnClickListener { listener(option) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu_event, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(options[position])
    }

    override fun getItemCount() = options.size
}