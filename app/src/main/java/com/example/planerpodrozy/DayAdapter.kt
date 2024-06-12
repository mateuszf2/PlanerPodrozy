package com.example.planerpodrozy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DayAdapter(
    private val dayList: List<PlanerDay>,
    private val onCheckedChangeListener: (Planer, Boolean) -> Unit,
    private val onDeleteClickListener: (Planer) -> Unit // Dodano ten parametr
) : RecyclerView.Adapter<DayAdapter.DayViewHolder>() {

    inner class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewDay: TextView = itemView.findViewById(R.id.textView_Day)
        val activitiesRecyclerView: RecyclerView = itemView.findViewById(R.id.recyclerView_Activities)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_day, parent, false)
        return DayViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val currentDay = dayList[position]
        holder.textViewDay.text = currentDay.data

        val planerAdapter = PlanerAdapter(currentDay.activities, onCheckedChangeListener, onDeleteClickListener) // Przekazano funkcję usuwania
        holder.activitiesRecyclerView.adapter = planerAdapter
        holder.activitiesRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
    }

    override fun getItemCount() = dayList.size
}
