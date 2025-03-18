package com.essency.essencystockmovement.data.UI.Home.ui.preparingforshipment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.essency.essencystockmovement.data.UI.BaseFragment
import com.essency.essencystockmovement.data.local.MyDatabaseHelper
import com.essency.essencystockmovement.data.repository.AppUserRepository
import com.essency.essencystockmovement.data.repository.TraceabilityStockListRepository
import com.essency.essencystockmovement.databinding.FragmentPreparingForShipmentDataBinding
import com.essency.essencystockmovement.databinding.FragmentReceivingDataBinding

class PreparingForShipmentDataFragment : BaseFragment()
{
    private var _binding: FragmentPreparingForShipmentDataBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var repository: TraceabilityStockListRepository
    private lateinit var users: AppUserRepository
    private val defaultMovementType: String = "PREPARATION SHIPMENT"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding =  FragmentPreparingForShipmentDataBinding.inflate(inflater, container, false)


        sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        users = AppUserRepository(MyDatabaseHelper(requireContext()))
        repository = TraceabilityStockListRepository(MyDatabaseHelper(requireContext()))

        //loadLastTraceabilityStock() // Auto-rellenar (si hay) o limpiar campos
        setupListeners()
        return binding.root
    }

    private fun setupListeners() {
        binding.buttonSave.setOnClickListener {
            //saveTraceabilityStock()
        }

        binding.buttonEdit.setOnClickListener {
            //enableFields()
        }
    }

}