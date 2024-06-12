package com.example.planerpodrozy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PlanerAdapter(
    private val activityList: List<Planer>,
    private val onCheckedChangeListener: (Planer, Boolean) -> Unit,
    private val onDeleteClickListener: (Planer) -> Unit // Dodano ten parametr
) : RecyclerView.Adapter<PlanerAdapter.PlanerViewHolder>() {

    inner class PlanerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewGodzina: TextView = itemView.findViewById(R.id.textView_Godzina)
        val textViewNazwaAktywnosci: TextView = itemView.findViewById(R.id.textView_NazwaAktywnosci)
        val buttonDeletePlaner: Button = itemView.findViewById(R.id.buttonDeletePlaner) // Dodano ten element
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_planer, parent, false)
        return PlanerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PlanerViewHolder, position: Int) {
        val currentActivity = activityList[position]
        holder.textViewGodzina.text = currentActivity.godzina
        holder.textViewNazwaAktywnosci.text = currentActivity.nazwaAktywnosci

        holder.buttonDeletePlaner.setOnClickListener {
            onDeleteClickListener(currentActivity) // Wywołanie funkcji usuwania
        }
    }

    override fun getItemCount() = activityList.size
}
