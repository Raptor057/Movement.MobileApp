package com.essency.essencystockmovement.data.UI.Home.ui.preparingforshipment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class PreparingForShipmentPagerAdapter (activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2 // Solo hay 2 pestañas

    companion object {
        const val TAB_RECEIVING_DATA = 0
        const val TAB_RECEIVING_LIST = 1 // Corregir el nombre
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            TAB_RECEIVING_DATA -> PreparingForShipmentDataFragment()
            TAB_RECEIVING_LIST -> PreparingForShipmentFragment() // Cambio el nombre correcto
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}
