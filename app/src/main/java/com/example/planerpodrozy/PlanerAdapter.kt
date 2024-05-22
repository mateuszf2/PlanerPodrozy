
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.planerpodrozy.Planer
import com.example.planerpodrozy.R

class PlanerAdapter : ListAdapter<Planer, PlanerAdapter.PlanerViewHolder>(PlanerDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_planer, parent, false)
        return PlanerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PlanerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PlanerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewData: TextView = itemView.findViewById(R.id.textView_Data)
        private val textViewGodzina: TextView = itemView.findViewById(R.id.textView_Godzina)
        private val textViewNazwaAktywnosci: TextView = itemView.findViewById(R.id.textView_NazwaAktywnosci)

        fun bind(planer: Planer) {
            textViewData.text = planer.data
            textViewGodzina.text = planer.godzina
            textViewNazwaAktywnosci.text = planer.nazwaAktywnosci
        }
    }

    class PlanerDiffCallback : DiffUtil.ItemCallback<Planer>() {
        override fun areItemsTheSame(oldItem: Planer, newItem: Planer): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Planer, newItem: Planer): Boolean {
            return oldItem == newItem
        }
    }

    fun submitSortedData(data: List<Planer>) {
        val sortedData = data.sortedWith(compareBy({ it.data }, { it.godzina }))
        super.submitList(sortedData)
    }
}

