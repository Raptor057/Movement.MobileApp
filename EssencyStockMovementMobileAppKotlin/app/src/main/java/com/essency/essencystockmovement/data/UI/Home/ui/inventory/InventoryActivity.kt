package com.essency.essencystockmovement.data.UI.Home.ui.inventory

import android.os.Bundle
import com.essency.essencystockmovement.R
import com.essency.essencystockmovement.data.UI.BaseActivity

class InventoryActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)

        // Si no hay un estado previo (rotación, etc.), carga el fragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, InventoryFragment())
                .commit()
        }
    }
}
