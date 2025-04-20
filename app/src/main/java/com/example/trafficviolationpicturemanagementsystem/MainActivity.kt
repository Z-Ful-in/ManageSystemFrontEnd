package com.example.trafficviolationpicturemanagementsystem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.compose.rememberNavController
import com.example.trafficviolationpicturemanagementsystem.navigation.AppNavGraph
import com.example.trafficviolationpicturemanagementsystem.ui.theme.TrafficViolationPictureManagementSystemTheme
import com.example.trafficviolationpicturemanagementsystem.viewmodel.AuthViewModel
import com.example.trafficviolationpicturemanagementsystem.viewmodel.HomeViewModel

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrafficViolationPictureManagementSystemTheme {
                val navController = rememberNavController()
                val isLoggedIn by authViewModel.isLoggedIn.observeAsState(false)
                AppNavGraph(
                    navController = navController,
                    startDestination = if (!isLoggedIn) "login" else "home",
                    authViewModel = authViewModel,
                    homeViewModel = homeViewModel
                )
            }
        }
    }
}

