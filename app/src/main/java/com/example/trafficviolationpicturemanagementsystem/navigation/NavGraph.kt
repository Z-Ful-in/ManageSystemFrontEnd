package com.example.trafficviolationpicturemanagementsystem.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.trafficviolationpicturemanagementsystem.ui.home.ManagementAppPortrait
import com.example.trafficviolationpicturemanagementsystem.ui.LoginRegisterScreen
import com.example.trafficviolationpicturemanagementsystem.viewmodel.AuthViewModel
import com.example.trafficviolationpicturemanagementsystem.viewmodel.HomeViewModel
import com.example.trafficviolationpicturemanagementsystem.viewmodel.MockAuthViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String,
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel
){
    NavHost(
        navController = navController,
        startDestination = startDestination
    ){
        composable("login"){ LoginRegisterScreen(authViewModel, navController) }
        composable("home"){ ManagementAppPortrait(homeViewModel, navController) }
    }
}

@Preview(showBackground = true)
@Composable
fun AppNavGraphPreview() {
    val navController = rememberNavController()
    val mockAuthViewModel = MockAuthViewModel(Application())
    val mockHomeViewModel = HomeViewModel(Application())
    AppNavGraph(navController = navController, startDestination = "home", mockAuthViewModel, mockHomeViewModel)
}