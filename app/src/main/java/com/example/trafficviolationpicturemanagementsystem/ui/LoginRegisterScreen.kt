package com.example.trafficviolationpicturemanagementsystem.ui

import android.app.Application
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.trafficviolationpicturemanagementsystem.R
import com.example.trafficviolationpicturemanagementsystem.data.repository.LoginRegisterResult
import com.example.trafficviolationpicturemanagementsystem.ui.login.LoginScreen
import com.example.trafficviolationpicturemanagementsystem.ui.register.RegisterScreen
import com.example.trafficviolationpicturemanagementsystem.viewmodel.AuthViewModel
import com.example.trafficviolationpicturemanagementsystem.viewmodel.HomeViewModel
import com.example.trafficviolationpicturemanagementsystem.viewmodel.MockAuthViewModel

@Composable
fun LoginRegisterScreen(
    authViewModel: AuthViewModel,
    navController: NavController,
    homeViewModel: HomeViewModel
) {
    var isRegistered by remember { mutableStateOf(true) }
    val loginRegisterResult by authViewModel.loginRegisterResult.observeAsState()

    if(isRegistered){
        AuthContainer(
            title = stringResource(R.string.App_title)
        ){
            LoginScreen(
                onSwitchToRegister = {
                    isRegistered = false
                    authViewModel.clearResult()
                },
                onLogin = { username, password ->
                    authViewModel.login(username, password) {
                        homeViewModel.loadData()
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = false }
                        }
                    }
                },
                authViewModel,
                userNameError = if (loginRegisterResult is LoginRegisterResult.Error) (loginRegisterResult as LoginRegisterResult.Error).error else "",
                passwordError = if (loginRegisterResult is LoginRegisterResult.Error) (loginRegisterResult as LoginRegisterResult.Error).error else ""
            )
        }
    } else {
        AuthContainer(
            title = stringResource(R.string.App_title)
        ){
            RegisterScreen(
                onSwitchToLogin = {
                    isRegistered = true
                    authViewModel.clearResult()
                },
                onRegister = { username, password ->
                    authViewModel.register(username, password) {
                        isRegistered = true
                    }
                },
                authViewModel,
                usernameRegistered = if (loginRegisterResult is LoginRegisterResult.Error) (loginRegisterResult as LoginRegisterResult.Error).error else ""
            )
        }
    }
}

@Composable
fun AuthContainer(
    title: String,
    content: @Composable () -> Unit
){
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            Text(
                text = title,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 50.dp)
            )
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginRegisterScreenPreview() {
    val viewModel = MockAuthViewModel(Application())
    val homeViewModel = HomeViewModel(Application())
    val navController = rememberNavController()
    LoginRegisterScreen(
        authViewModel = viewModel,
        navController = navController,
        homeViewModel = homeViewModel
    )
}