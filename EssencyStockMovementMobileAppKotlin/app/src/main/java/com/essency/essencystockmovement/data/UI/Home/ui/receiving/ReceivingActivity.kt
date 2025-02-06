package com.essency.essencystockmovement.data.UI.Home.ui.receiving

import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.essency.essencystockmovement.R
import com.essency.essencystockmovement.data.UI.BaseActivity
import com.google.android.material.tabs.TabLayoutMediator

class ReceivingActivity : BaseActivity()
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receiving)

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val tabLayout: com.google.android.material.tabs.TabLayout = findViewById(R.id.tabLayout)

        // Configura el adaptador del ViewPager
        val adapter = ReceivingPagerAdapter(this)
        viewPager.adapter = adapter

        // Configura las pestañas con títulos
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tab_receiving_data)
                1 -> getString(R.string.tab_receiving)
                else -> null
            }
        }.attach()
    }
}