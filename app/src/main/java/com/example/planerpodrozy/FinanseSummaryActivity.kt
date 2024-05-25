package com.example.planerpodrozy

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.planerpodrozy.databinding.ActivityFinanseSummaryBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import java.text.DecimalFormat
import java.util.ArrayList

class FinanseSummaryActivity :AppCompatActivity() {
    private lateinit var binding: ActivityFinanseSummaryBinding
    private lateinit var finanseSummaryRecyclerView: RecyclerView
    private lateinit var finanseSummaryAdapter: FinanseSummaryAdapter

    class MyValueFormatter : ValueFormatter() {
        private val format = DecimalFormat("###,###,##0.00") // Adjust the pattern as needed

        override fun getFormattedValue(value: Float): String {
            return format.format(value)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFinanseSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val db= Firebase.firestore
        val currentUser= FirebaseAuth.getInstance().currentUser
        val userId= currentUser?.uid
        val eventId= intent.getStringExtra("eventId")
        val chart = findViewById<BarChart>(R.id.barChart)


        finanseSummaryRecyclerView= binding.recyclerViewFinanse
        finanseSummaryAdapter= FinanseSummaryAdapter()
        finanseSummaryRecyclerView.adapter= finanseSummaryAdapter
        finanseSummaryRecyclerView.layoutManager= LinearLayoutManager(this)

        var friendsList =  mutableListOf<Bilans>()
        var index = 0
        val friends = ArrayList<BarEntry>()
        val labels = ArrayList<String>()
        val colors = ArrayList<Int>()


        fun populateChart(friends: List<BarEntry>, labels: List<String>, colors: List<Int>) {
            val dataSet = BarDataSet(friends, "Total Bilans")
            dataSet.colors=colors
            dataSet.label=""
            val dataBar = BarData(dataSet)

            chart.data = dataBar

            chart.setFitBars(true)
            chart.description.isEnabled = false
            chart.animateY(2000)

            val xAxis = chart.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            xAxis.granularity = 1f
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return labels.getOrNull(value.toInt()) ?: ""
                }
            }


            val yAxisLeft: YAxis = chart.axisLeft
            yAxisLeft.valueFormatter = MyValueFormatter()

            val yAxisRight: YAxis = chart.axisRight
            yAxisRight.valueFormatter = MyValueFormatter()
            chart.invalidate() // Refresh the chart
        }
        fun getColorForIndex(index: Int): Int {
            // Define a color palette, adjust as needed
            val colors = listOf(
                android.graphics.Color.RED,
                android.graphics.Color.GREEN,
                android.graphics.Color.BLUE,
                android.graphics.Color.YELLOW,
                android.graphics.Color.MAGENTA
            )
            // Return color for the given index
            return colors[index % colors.size]
        }

        fun makeChart(){
            for (bilans in friendsList) {
                db.collection("idEmail")
                    .whereEqualTo("userId", bilans.userId)
                    .get()
                    .addOnSuccessListener { userEmails ->
                        for (userEmail in userEmails) {//jeden dokument
                            friends.add(BarEntry(index.toFloat(),bilans.totalBilans.toFloat()))
                            var monkeyIndex = userEmail.getString("userEmail")?.indexOf('@')
                            if (monkeyIndex!! >=10){
                                monkeyIndex = 10
                            }
                            labels.add(userEmail.getString("userEmail")?.substring(0, monkeyIndex!!).toString())
                            colors.add(getColorForIndex(index))
                            index++
                            if (index == friendsList.size) {
                                populateChart(friends, labels,colors)
                            }
                        }
                    }
            }
        }

        fun fetchFriends() {
            if(eventId!=null){
                db.collection("bilans").document(eventId).collection("bilansPairs")
                    .where(Filter.equalTo("friendId",userId))
                    .get()
                    .addOnSuccessListener(){ documents->
                        if(documents!=null) {
                            for (document in documents) {
                                val friend =Bilans(
                                    document.get("friendId").toString(),
                                    String.format("%.10f",document.get("totalBilans")),
                                    document.get("userId").toString()
                                )
                                if (friend != null) {
                                    friendsList.add(friend)
                                }
                            }

                        }
                        finanseSummaryAdapter.submitList(friendsList)
                        makeChart()
                    }
            }
        }

        fetchFriends()









        binding.buttonBack.setOnClickListener{
            val intent = Intent(this, FinanseActivity::class.java)
            intent.putExtra("eventId", eventId)
            startActivity(intent)
        }

        binding.buttonMakePayment.setOnClickListener {
            val intent= Intent(this, PayFriendActivity::class.java)
            intent.putExtra("eventId", eventId)
            startActivity(intent)
        }

    }

}
