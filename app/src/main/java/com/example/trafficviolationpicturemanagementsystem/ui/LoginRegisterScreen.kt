package com.example.trafficviolationpicturemanagementsystem.ui

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.trafficviolationpicturemanagementsystem.data.repository.LoginRegisterResult
import com.example.trafficviolationpicturemanagementsystem.ui.login.LoginScreen
import com.example.trafficviolationpicturemanagementsystem.ui.register.RegisterScreen
import com.example.trafficviolationpicturemanagementsystem.viewmodel.AuthViewModel
import com.example.trafficviolationpicturemanagementsystem.viewmodel.MockAuthViewModel

@Composable
fun LoginRegisterScreen(
    viewModel: AuthViewModel,
    navController: NavController
) {
    var isRegistered by remember { mutableStateOf(true) }
    val loginRegisterResult by viewModel.loginRegisterResult.observeAsState()

    if(isRegistered){
        LoginScreen(
            onSwitchToRegister = {
                isRegistered = false
            },
            onLogin = { username, password ->
                viewModel.login(username, password){
                    navController.navigate("home"){
                        popUpTo("login") { inclusive = false }
                    }
                }
            },
            userNameError = if (loginRegisterResult is LoginRegisterResult.Error) (loginRegisterResult as LoginRegisterResult.Error).error else "",
            passwordError = if (loginRegisterResult is LoginRegisterResult.Error) (loginRegisterResult as LoginRegisterResult.Error).error else ""
        )
    } else {
        RegisterScreen(
            onSwitchToLogin = {
                isRegistered = true
            },
            onRegister = { username, password ->
                viewModel.register(username, password){
                    isRegistered = true
                }
            },
            usernameRegistered = if (loginRegisterResult is LoginRegisterResult.Error) (loginRegisterResult as LoginRegisterResult.Error).error else ""
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginRegisterScreenPreview() {
    val viewModel = MockAuthViewModel(Application())
    val navController = rememberNavController()
    LoginRegisterScreen(
        viewModel = viewModel,
        navController = navController
    )
}