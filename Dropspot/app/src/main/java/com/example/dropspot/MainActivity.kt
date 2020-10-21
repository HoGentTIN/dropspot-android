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
import androidx.navigation.ui.setupWithNavController
import com.example.dropspot.databinding.ActivityMainBinding
import com.example.dropspot.utils.Anims
import com.example.dropspot.viewmodels.UserViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val userViewModel: UserViewModel by viewModel()
    private lateinit var binding: ActivityMainBinding

    //nav
    private lateinit var toolbar: MaterialToolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        navController = this.findNavController(R.id.myNavHostFragment)
        setupNav()
        setupFab()
    }

    //floating action button
    private fun setupFab() {
        val fab: FloatingActionButton = binding.fab

        fab.setOnClickListener { view ->
            view as FloatingActionButton
            view.isExpanded = !view.isExpanded
            if (view.isExpanded) {
                rotateForward(view)
            } else {
                rotateBackward(view)
            }
        }

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

    }


    private fun setupNav() {
        drawerLayout = binding.drawerLayout
        toolbar = binding.toolbar
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
