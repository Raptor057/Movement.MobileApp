package com.essency.essencystockmovement.data.UI.Home.ui.preparingforshipment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.essency.essencystockmovement.data.model.StockList
import com.essency.essencystockmovement.databinding.ItemReceivingPreparingForShipmentBinding

class PreparingForShipmentAdapter (
    private val stockList: List<StockList>,
    private val onDeleteClick: (StockList) -> Unit
) : RecyclerView.Adapter<PreparingForShipmentAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemReceivingPreparingForShipmentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(stock: StockList) {
            binding.textViewStockID.text = stock.id.toString()
            binding.textViewIDTraceabilityStockList.text = stock.idTraceabilityStockList.toString()
            binding.textViewCompany.text = stock.company
            binding.textViewSource.text = stock.source
            binding.textViewSourceLoc.text = stock.sourceLoc ?: "-"
            binding.textViewDestination.text = stock.destination
            binding.textViewDestinationLoc.text = stock.destinationLoc ?: "-"
            binding.textViewPallet.text = stock.pallet
            binding.textViewPartNo.text = stock.partNo
            binding.textViewRev.text = stock.rev
            binding.textViewLot.text = stock.lot
            binding.textViewQty.text = stock.qty.toString()
            binding.textViewProductionDate.text = stock.productionDate
            binding.textViewCountryOfProduction.text = stock.countryOfProduction
            binding.textViewSerialNumber.text = stock.serialNumber
            binding.textViewDate.text = stock.date
            binding.textViewTimeStamp.text = stock.timeStamp
            binding.textViewUser.text = stock.user
            binding.textViewContBolNum.text =
                "${stock.contBolNum} - ${stock.idTraceabilityStockList}" ?: "-"
            // Acción de eliminar
            binding.buttonDelete.setOnClickListener {
                onDeleteClick(stock)  // Llamamos al callback con el item actual
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemReceivingPreparingForShipmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = stockList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(stockList[position])
    }
}