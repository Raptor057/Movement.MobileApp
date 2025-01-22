package com.essency.essencystockmovement.data.UI.Home.ui.settings.options.users

import android.os.Bundle

import com.essency.essencystockmovement.R
import com.google.android.material.tabs.TabLayoutMediator
import androidx.viewpager2.widget.ViewPager2
import com.essency.essencystockmovement.data.UI.BaseActivity

//class UsersActivity : AppCompatActivity() {
class UsersActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val tabLayout: com.google.android.material.tabs.TabLayout = findViewById(R.id.tabLayout)

        // Configura el adaptador del ViewPager
        val adapter = UsersPagerAdapter(this)
        viewPager.adapter = adapter

        // Configura las pestañas con títulos
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tab_add_user)
                1 -> getString(R.string.tab_get_user)
                2 -> getString(R.string.tab_update_user)
                3 -> getString(R.string.tab_delete_user)
                else -> null
            }
        }.attach()
    }
}
