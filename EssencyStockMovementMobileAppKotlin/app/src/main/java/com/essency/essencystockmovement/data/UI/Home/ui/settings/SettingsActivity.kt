package com.essency.essencystockmovement.data.UI.Home.ui.settings


import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.essency.essencystockmovement.R
import com.essency.essencystockmovement.data.UI.BaseActivity
import com.essency.essencystockmovement.data.UI.Home.HomeActivity
import com.essency.essencystockmovement.data.UI.Home.ui.settings.options.changelanguage.ChangeLanguageActivity
import com.essency.essencystockmovement.data.UI.Home.ui.settings.options.changeregex.ChangeRegexActivity
import com.essency.essencystockmovement.data.UI.Home.ui.settings.options.changesenderemail.ChangeSenderEmailActivity
import com.essency.essencystockmovement.data.UI.Home.ui.settings.options.changesendingemail.ChangeSendingEmailActivity
import com.essency.essencystockmovement.data.UI.Home.ui.settings.options.users.UsersActivity
import com.essency.essencystockmovement.data.UI.Home.ui.settings.options.warehouse.WarehouseActivity
import com.essency.essencystockmovement.databinding.ActivitySettingsBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar

//class SettingsActivity : AppCompatActivity() {
class SettingsActivity : BaseActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarSettings.toolbar)

        binding.appBarSettings.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_settings)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_settings_main,
                R.id.nav_settings_users,
                R.id.nav_settings_change_sending_email,
                R.id.nav_settings_change_sender_email,
                R.id.nav_settings_change_regex,
                R.id.nav_settings_warehouse,
                R.id.nav_settings_change_language
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Agrega un listener para manejar el clic en Logout y Settings
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId)
            {
                R.id.nav_settings_main->{
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_settings_users -> {
                    val intent = Intent(this, UsersActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_settings_change_sending_email -> {
                    val intent = Intent(this, ChangeSendingEmailActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawers()
                    true}
                R.id.nav_settings_change_sender_email -> {
                    val intent = Intent(this, ChangeSenderEmailActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawers()
                    true}
                R.id.nav_settings_change_regex -> {
                    val intent = Intent(this, ChangeRegexActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawers()
                    true}
                R.id.nav_settings_warehouse -> {
                    val intent = Intent(this, WarehouseActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawers()
                    true}
                R.id.nav_settings_change_language -> {
                    val intent = Intent(this, ChangeLanguageActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawers()
                    true}
                else -> {
                    menuItem.isChecked = true
                    navController.navigate(menuItem.itemId)
                    drawerLayout.closeDrawers()
                    true
                }
            }
        }
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.

        //menuInflater.inflate(R.menu.activity_main_drawer, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_settings)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}