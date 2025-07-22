package com.essency.essencystockmovement.data.UI.Home.ui.settings.options.updatedestinations

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.essency.essencystockmovement.R
import com.essency.essencystockmovement.data.model.MovementTypeDestination

class DestinationAdapter(
    private var destinations: List<MovementTypeDestination>,
    private val onEditClick: (MovementTypeDestination) -> Unit
) : RecyclerView.Adapter<DestinationAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textDestination: TextView = itemView.findViewById(R.id.textDestination)
        val buttonEdit: ImageButton = itemView.findViewById(R.id.buttonEditDestination)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_destination_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = destinations.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val destination = destinations[position]
        holder.textDestination.text = destination.destination

        // CLIC AL BOTÓN DE LAPIZ
        holder.buttonEdit.setOnClickListener {
            onEditClick(destination)
        }
    }

    // Actualiza los datos si se modifican desde el fragmento
    fun updateData(newList: List<MovementTypeDestination>) {
        destinations = newList
        notifyDataSetChanged()
    }
}
