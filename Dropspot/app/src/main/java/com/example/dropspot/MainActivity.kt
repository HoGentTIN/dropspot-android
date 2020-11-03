package com.example.dropspot


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.dropspot.controllers.HomeFragmentDirections
import com.example.dropspot.databinding.ActivityMainBinding
import com.example.dropspot.utils.Variables
import com.example.dropspot.viewmodels.UserViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val userViewModel: UserViewModel by viewModel()
    private lateinit var binding: ActivityMainBinding

    //nav
    private lateinit var toolbar: MaterialToolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        setupNav()
        setupListeners()
    }

    private fun setupListeners() {
        // update side nav username
        userViewModel.currentUser.observe(this, Observer {
            navView.getHeaderView(0).findViewById<TextView>(R.id.username).text = it.username
        })

        // logs out if token is expired
        userViewModel.isTokenExpired.observe(this, Observer {
            if (it) logout()
        })

        // handles connection response
        Variables.isNetworkConnected.observe(this, Observer {
            if (it) {
                setTokenInReqHeader()
                binding.animNoConnection.visibility = View.INVISIBLE
            } else {
                binding.animNoConnection.visibility = View.VISIBLE
                binding.animNoConnection.bringToFront()
            }
        })
    }

    private fun setTokenInReqHeader() {
        userViewModel.setCurrentUser(this.intent.getStringExtra("TOKEN")!!)
    }

    private fun setupNav() {
        navController = this.findNavController(R.id.myNavHostFragment)
        navController.setGraph(R.navigation.navigation)
        drawerLayout = binding.drawerLayout
        toolbar = binding.toolbar
        navView = binding.navView
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        toolbar.setupWithNavController(navController, appBarConfiguration)
        // prevent nav gesture if not on start destination
        navController.addOnDestinationChangedListener { nc: NavController, nd: NavDestination, _ ->
            if (nd.id == nc.graph.startDestination) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

            } else {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }

        // set this activity as the NavigationView.OnNavigationItemSelectedListener for navView
        navView.setNavigationItemSelectedListener(this)

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

    private fun logout() {
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)
        val sharedPreferences = EncryptedSharedPreferences.create(
            "AUTH_ENCRYPT",
            masterKeyAlias,
            this.applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        ).edit()
        sharedPreferences.remove("TOKEN")
        sharedPreferences.remove("PASSWORD")
        sharedPreferences.apply()
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                Log.i("nav", "logout")
                logout()
            }
            R.id.meFragment -> {
                navController.navigate(HomeFragmentDirections.actionHomeFragmentToMeFragment())
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


}
