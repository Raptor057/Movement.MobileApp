package com.essency.essencystockmovement.data.UI.Home.ui.preparingforshipment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.essency.essencystockmovement.data.UI.BaseFragment
import com.essency.essencystockmovement.databinding.FragmentPreparingForShipmentBinding

class PreparingForShipmentFragment : BaseFragment() {

    private var _binding: FragmentPreparingForShipmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val preparingForShipmentViewModel =
            ViewModelProvider(this).get(PreparingForShipmentViewModel::class.java)

        _binding = FragmentPreparingForShipmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textPreparingForShipment
        preparingForShipmentViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}