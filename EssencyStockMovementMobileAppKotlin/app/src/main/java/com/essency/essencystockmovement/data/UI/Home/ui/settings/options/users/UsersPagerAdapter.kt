package com.essency.essencystockmovement.data.UI.Home.ui.settings.options.users

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class UsersPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 4 // Número de pestañas/fragments

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AddUserFragment()
            1 -> GetUsersFragment()
            2 -> UpdateUserFragment()
            3 -> DeleteUserFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}
