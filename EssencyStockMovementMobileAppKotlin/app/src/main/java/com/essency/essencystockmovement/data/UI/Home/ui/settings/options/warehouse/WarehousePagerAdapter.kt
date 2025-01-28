package com.essency.essencystockmovement.data.UI.Home.ui.settings.options.warehouse

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class WarehousePagerAdapter (activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 3 // Número de pestañas/fragments

    companion object {
        const val TAB_GET_WAREHOUSE = 0
        const val TAB_ADD_WAREHOUSE = 1
        const val TAB_UPDATE_WAREHOUSE = 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            TAB_GET_WAREHOUSE -> GetWarehouseFragment()
            TAB_ADD_WAREHOUSE -> AddWarehouseFragment()
            TAB_UPDATE_WAREHOUSE -> UpdateWarehouseFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}
