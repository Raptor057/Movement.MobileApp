package com.essency.essencystockmovement.data.UI.Home.ui.settings.options.changesendingemail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

import com.essency.essencystockmovement.databinding.FragmentSettingsEmailBinding

class ChangeSendingEmailFragment : Fragment() {

    private var _binding: FragmentSettingsEmailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inventoryViewModel =
            ViewModelProvider(this)[ChangeSendingEmailViewModel::class.java]

        _binding = FragmentSettingsEmailBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        inventoryViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}