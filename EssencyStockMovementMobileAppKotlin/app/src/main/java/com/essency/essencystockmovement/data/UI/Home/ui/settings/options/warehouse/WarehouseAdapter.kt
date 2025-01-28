package com.essency.essencystockmovement.data.UI.Home.ui.settings.options.warehouse

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.essency.essencystockmovement.R
import com.essency.essencystockmovement.data.model.WarehouseList

class WarehouseAdapter(
    private val warehouses: MutableList<WarehouseList>,
    private val onEditClicked: (WarehouseList) -> Unit,
    //private val onDeleteClicked: (WarehouseList) -> Unit
) : RecyclerView.Adapter<WarehouseAdapter.WarehouseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WarehouseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_warehouse, parent, false)
        return WarehouseViewHolder(view)
    }

    override fun onBindViewHolder(holder: WarehouseViewHolder, position: Int) {
        val warehouse = warehouses[position]
        holder.bind(warehouse)
    }

    override fun getItemCount(): Int = warehouses.size

    /**
     * Actualiza la lista de almacenes y notifica a RecyclerView
     */
    fun updateData(newWarehouses: List<WarehouseList>) {
        warehouses.clear()
        warehouses.addAll(newWarehouses)
        notifyDataSetChanged()
    }

    /**
     * Agrega un nuevo almacén a la lista y notifica a RecyclerView
     */
    fun addWarehouse(warehouse: WarehouseList) {
        warehouses.add(warehouse)
        notifyItemInserted(warehouses.size - 1)
    }

    /**
     * Actualiza un almacén existente y notifica a RecyclerView
     */
    fun updateWarehouse(updatedWarehouse: WarehouseList) {
        val index = warehouses.indexOfFirst { it.id == updatedWarehouse.id }
        if (index != -1) {
            warehouses[index] = updatedWarehouse
            notifyItemChanged(index)
        }
    }

    /**
     * Elimina un almacén y notifica a RecyclerView
     */
    fun removeWarehouse(warehouse: WarehouseList) {
        val index = warehouses.indexOfFirst { it.id == warehouse.id }
        if (index != -1) {
            warehouses.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    inner class WarehouseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textWarehouseName: TextView = itemView.findViewById(R.id.textWarehouseName)
        private val buttonEdit: ImageButton = itemView.findViewById(R.id.buttonEditWarehouse)

        fun bind(warehouse: WarehouseList) {
            textWarehouseName.text = warehouse.warehouse

            // Botón para editar
            buttonEdit.setOnClickListener {
                onEditClicked(warehouse)
            }

        }
    }
}
