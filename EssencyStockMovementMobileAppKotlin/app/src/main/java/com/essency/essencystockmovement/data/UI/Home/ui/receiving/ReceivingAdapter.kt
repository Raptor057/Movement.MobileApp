package com.essency.essencystockmovement.data.UI.Home.ui.receiving

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.essency.essencystockmovement.data.model.StockList
import com.essency.essencystockmovement.databinding.ItemReceivingBinding

class ReceivingAdapter(
    private val stockList: List<StockList>,
    private val onDeleteClick: (StockList) -> Unit
) : RecyclerView.Adapter<ReceivingAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemReceivingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(stock: StockList) {
            binding.textViewStockID.text = stock.id.toString()
            binding.textViewCompany.text = stock.company
            binding.textViewSource.text = stock.source
            binding.textViewSourceLoc.text = stock.sourceLoc ?: "-"
            binding.textViewDestination.text = stock.destination
            binding.textViewDestinationLoc.text = stock.destinationLoc ?: "-"
            binding.textViewPartNo.text = stock.partNo
            binding.textViewRev.text = stock.rev
            binding.textViewLot.text = stock.lot
            binding.textViewQty.text = stock.qty.toString()
            binding.textViewDate.text = stock.date
            binding.textViewTimeStamp.text = stock.timeStamp
            binding.textViewUser.text = stock.user
            binding.textViewContBolNum.text = stock.contBolNum ?: "-"

            // Acción de eliminar
            binding.buttonDelete.setOnClickListener {
                onDeleteClick(stock)  // Llamamos al callback con el item actual
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemReceivingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(stockList[position])
    }

    override fun getItemCount(): Int = stockList.size
}
