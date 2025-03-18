package com.essency.essencystockmovement.data.UI.Home

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.essency.essencystockmovement.R
import com.essency.essencystockmovement.data.UI.BaseActivity
import com.essency.essencystockmovement.data.UI.Home.ui.preparingforshipment.PreparingForShipmentActivity
import com.essency.essencystockmovement.data.UI.Home.ui.receiving.ReceivingActivity
import com.essency.essencystockmovement.data.UI.Home.ui.settings.SettingsActivity
import com.essency.essencystockmovement.data.UI.LoginActivity
import com.essency.essencystockmovement.databinding.ActivityHomeBinding

class HomeActivity : BaseActivity() { //AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarHome.toolbar)

        binding.appBarHome.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_Home,
                R.id.nav_Receiving,
                R.id.nav_Preparing_for_shipment,
                R.id.nav_Inventory,
                R.id.nav_Reporting,
                R.id.nav_Setting,
                R.id.nav_Info,
                R.id.nav_Help
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Agrega un listener para manejar el clic en Logout y Settings
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_Receiving -> {
                    val intent = Intent(this, ReceivingActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawers()
                    true}
                R.id.nav_Preparing_for_shipment -> {
                    val intent = Intent(this, PreparingForShipmentActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawers()
                    true}
                R.id.nav_Logout -> {
                    handleLogout() // Llama al método para manejar el logout
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_Setting -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawers()
                    true
                }
                else -> {
                    menuItem.isChecked = true
                    navController.navigate(menuItem.itemId)
                    drawerLayout.closeDrawers()
                    true
                }
            }
        }
    }


    private fun handleLogout() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                logoutUser() // Llamamos a la función que borra SharedPreferences
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun logoutUser() {
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            clear() // Elimina todos los datos guardados
            apply()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.

        //menuInflater.inflate(R.menu.activity_main_drawer, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}