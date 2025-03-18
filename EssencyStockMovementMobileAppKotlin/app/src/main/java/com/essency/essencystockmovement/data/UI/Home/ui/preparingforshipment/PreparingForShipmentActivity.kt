package com.essency.essencystockmovement.data.UI.Home.ui.preparingforshipment

import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.essency.essencystockmovement.R
import com.essency.essencystockmovement.data.UI.BaseActivity
import com.google.android.material.tabs.TabLayoutMediator

class PreparingForShipmentActivity : BaseActivity()
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preparing_for_shipment)

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val tabLayout: com.google.android.material.tabs.TabLayout = findViewById(R.id.tabLayout)

        // Configura el adaptador del ViewPager
        val adapter = PreparingForShipmentPagerAdapter(this)
        viewPager.adapter = adapter

        // Configura las pestañas con títulos
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tab_preparing_for_shipment_data)
                1 -> getString(R.string.tab_preparing_for_shipment)
                else -> null
            }
        }.attach()
    }
}