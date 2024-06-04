package com.example.planerpodrozy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PlanerAdapter(
    private val activityList: List<Planer>,
    private val onCheckedChangeListener: (Planer, Boolean) -> Unit
) : RecyclerView.Adapter<PlanerAdapter.PlanerViewHolder>() {

    inner class PlanerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewGodzina: TextView = itemView.findViewById(R.id.textView_Godzina)
        val textViewNazwaAktywnosci: TextView = itemView.findViewById(R.id.textView_NazwaAktywnosci)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkbox_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_planer, parent, false)
        return PlanerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PlanerViewHolder, position: Int) {
        val currentActivity = activityList[position]
        holder.textViewGodzina.text = currentActivity.godzina
        holder.textViewNazwaAktywnosci.text = currentActivity.nazwaAktywnosci
        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = false
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            onCheckedChangeListener(currentActivity, isChecked)
        }
    }

    override fun getItemCount() = activityList.size
}
