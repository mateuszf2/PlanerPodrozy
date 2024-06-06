import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.planerpodrozy.Planer
import com.example.planerpodrozy.PlanerDay
import com.example.planerpodrozy.R
import com.example.planerpodrozy.databinding.ItemPlanerDayBinding

class DayAdapter(
    private val planerDays: List<PlanerDay>,
    private val onPlanerCheckedChanged: (Planer, Boolean) -> Unit
) : RecyclerView.Adapter<DayAdapter.DayViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val binding = ItemPlanerDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val planerDay = planerDays[position]
        holder.bind(planerDay)
    }

    override fun getItemCount(): Int {
        return planerDays.size
    }

    inner class DayViewHolder(private val binding: ItemPlanerDayBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(planerDay: PlanerDay) {
            binding.textViewDay.text = planerDay.data

            val activityAdapter = ActivityAdapter(planerDay.planerActivities, onPlanerCheckedChanged)
            binding.recyclerViewActivities.adapter = activityAdapter
            binding.recyclerViewActivities.layoutManager = LinearLayoutManager(binding.root.context)
        }
    }
}

class ActivityAdapter(
    private val planerActivities: List<Planer>,
    private val onPlanerCheckedChanged: (Planer, Boolean) -> Unit
) : RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_planer, parent, false)
        return ActivityViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val planer = planerActivities[position]
        holder.bind(planer)
    }

    override fun getItemCount(): Int {
        return planerActivities.size
    }

    inner class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkbox_delete)
        private val textViewGodzina: TextView = itemView.findViewById(R.id.textView_Godzina)
        private val textViewNazwaAktywnosci: TextView = itemView.findViewById(R.id.textView_NazwaAktywnosci)

        fun bind(planer: Planer) {
            checkBox.isChecked = false
            textViewGodzina.text = planer.godzina
            textViewNazwaAktywnosci.text = planer.nazwaAktywnosci

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                onPlanerCheckedChanged(planer, isChecked)
            }
        }
    }
}
