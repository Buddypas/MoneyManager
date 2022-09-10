package com.inFlow.moneyManager.presentation

import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.databinding.ActivityMainBinding
import com.inFlow.moneyManager.shared.extension.autoHideKeyboard
import com.inFlow.moneyManager.shared.extension.hideWithAnimation
import com.inFlow.moneyManager.shared.extension.showWithAnimation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getNavController()?.let { navController ->
            binding.bottomNav.setupWithNavController(navController)

            navController.addOnDestinationChangedListener { _, destination, _ ->
                binding.bottomNav.handleBottomNavDisplay(destination)
            }
        }
    }

    private fun BottomNavigationView.handleBottomNavDisplay(destination: NavDestination) {
        if (destination.id in detailFragmentIds) hideWithAnimation()
        else showWithAnimation()
    }

    private fun getNavController(): NavController? =
        (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment)?.navController

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        autoHideKeyboard(ev)
        return super.dispatchTouchEvent(ev)
    }

    private companion object {
        val detailFragmentIds = listOf(R.id.addTransactionFragment, R.id.addCategoryFragment)
    }
}
