package com.example.dropspot


import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.dropspot.databinding.ActivityMainBinding
import com.example.dropspot.utils.Anims
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        navController = this.findNavController(R.id.myNavHostFragment)
        setupNavigation()
        setupFab()

    }

    //floating action button
    private fun setupFab() {
        val fab: FloatingActionButton = binding.fab

        fab.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                view as FloatingActionButton
                view.isExpanded = !view.isExpanded
                if (view.isExpanded) {
                    rotateForward(view)
                } else {
                    rotateBackward(view)
                }
            }
        })

        navController.addOnDestinationChangedListener { _, dest, _ ->
            if (dest.id == R.id.homeFragment) {
                fab.visibility = View.VISIBLE
            } else {
                fab.visibility = View.GONE
                if (fab.isExpanded) {
                    fab.isExpanded = false
                    rotateBackward(fab)
                }
            }
        }

        val addSpot: FloatingActionButton = binding.fabaddspot
        addSpot.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                navController.navigate(R.id.action_homeFragment_to_addSpotFragment)
            }
        })
    }

    //navigation
    private fun setupNavigation() {
        initDrawer()
        initBottomNav()
    }

    private fun initBottomNav() {
        val navBottom = binding.bottomNavigation
        navBottom.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }
                R.id.meFragment -> {
                    navController.navigate(R.id.meFragment)
                    true
                }
                else -> false
            }
        }
        navController.addOnDestinationChangedListener { _, dest, _ ->
            if (dest.id == R.id.homeFragment ||
                    dest.id == R.id.meFragment) {
                navBottom.visibility = View.VISIBLE
            } else {
                navBottom.visibility = View.GONE
            }
        }
    }

    private fun initDrawer() {
        drawerLayout = binding.drawerLayout
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)
        // prevent nav gesture if not on start destination
        navController.addOnDestinationChangedListener { nc: NavController, nd: NavDestination,_->
            if (nd.id == nc.graph.startDestination) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            } else {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

    private fun rotateForward(view: View) {
        Anims.rotateForward(view)
    }

    private fun rotateBackward(view: View) {
        Anims.rotateBackward(view)
    }

}
