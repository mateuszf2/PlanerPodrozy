package com.example.planerpodrozy

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class FinanseSummaryAdapter() : ListAdapter<Bilans, FinanseSummaryAdapter.FinanseSummaryViewHolder>(FinanseSummaryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FinanseSummaryViewHolder {
        val itemView= LayoutInflater.from(parent.context).inflate(R.layout.item_finanse_summary, parent, false)
        return FinanseSummaryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FinanseSummaryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FinanseSummaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val friendName: TextView = itemView.findViewById(R.id.textView_friendName)
        private val money: TextView = itemView.findViewById(R.id.textView_money)

        private val db= Firebase.firestore

        fun bind(bilans: Bilans) {
            db.collection("idEmail")
                .whereEqualTo("userId", bilans.userId)
                .get()
                .addOnSuccessListener { userEmails ->
                    for(userEmail in userEmails){
                        friendName.text= userEmail.getString("userEmail")
                    }

                    if (bilans.totalBilans.contains(',')){
                        if (bilans.totalBilans.substring(0,5)=="-0,00") money.text="0.00 zł"
                        else if (bilans.totalBilans.substring(bilans.totalBilans.indexOf(',',0)+1).length>2){
                            var sub1 = bilans.totalBilans.substring(0,bilans.totalBilans.indexOf(',',0)+1)
                            var sub2 = bilans.totalBilans.substring(bilans.totalBilans.indexOf(',',0)+1).take(2)
                            money.text=sub1+sub2+" zł"

                            //magicznym trafem na telefonie pobiera z bazy z przecinkiem anizeli z kropka;))
                            Log.d("TOTAL",bilans.totalBilans)

                        }
                        else{
                            money.text=bilans.totalBilans
                        }
                    }
                    else{
                        if (bilans.totalBilans.substring(0,5)=="-0.00") money.text="0.00 zł"
                        else if (bilans.totalBilans.substring(bilans.totalBilans.indexOf('.',0)+1).length>2){
                            var sub1 = bilans.totalBilans.substring(0,bilans.totalBilans.indexOf('.',0)+1)
                            var sub2 = bilans.totalBilans.substring(bilans.totalBilans.indexOf('.',0)+1).take(2)
                            money.text=sub1+sub2+" zł"

                            //magicznym trafem na telefonie pobiera z bazy z przecinkiem anizeli z kropka;))
                            Log.d("TOTAL",bilans.totalBilans)

                        }
                        else{
                            money.text=bilans.totalBilans
                        }
                    }

                }
        }
    }

    class FinanseSummaryDiffCallback : DiffUtil.ItemCallback<Bilans>() {
        override fun areItemsTheSame(oldItem: Bilans, newItem:Bilans): Boolean {
            return oldItem.userId == newItem.userId
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Bilans, newItem: Bilans): Boolean {
            return oldItem==newItem
        }
    }
}