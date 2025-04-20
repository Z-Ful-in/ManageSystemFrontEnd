package com.example.trafficviolationpicturemanagementsystem.ui.register

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trafficviolationpicturemanagementsystem.R

@Composable
fun RegisterScreen(
    onSwitchToLogin: () -> Unit,
    onRegister: (String, String) -> Unit,
    usernameRegistered: String = "",
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var usernameError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var passwordNotConfirmed by remember { mutableStateOf(false) }

    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Register",
            modifier = Modifier.padding(bottom = 32.dp),
            fontSize = 24.sp,
        )
        TextField(
            value = username,
            onValueChange = {
                username = it
                usernameError = ""
            },
            label = { Text(stringResource(R.string.username_label)) },
            placeholder = { Text(stringResource(R.string.username_placeholder)) },
            modifier = Modifier.padding(bottom = 8.dp),
            isError = usernameError.isNotBlank(),
            supportingText = {
                if(usernameError.isNotBlank()){
                    Text(usernameError)
                }
                else if (usernameRegistered.isNotBlank()){
                    Text(usernameRegistered)
                }
            }
        )
        TextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = ""
            },
            label = { Text(stringResource(R.string.password_label)) },
            placeholder = { Text(stringResource(R.string.password_placeholder)) },
            modifier = Modifier.padding(bottom = 8.dp),
            isError = passwordError.isNotBlank(),
            supportingText = {
                if(passwordError.isNotBlank()){
                    Text(passwordError)
                }
            },
            visualTransformation = PasswordVisualTransformation(),
        )
        TextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                passwordNotConfirmed = false
            },
            label = { Text(stringResource(R.string.confirm_label)) },
            placeholder = { Text(stringResource(R.string.confirm_placeholder)) },
            modifier = Modifier.padding(bottom = 8.dp),
            isError = passwordNotConfirmed,
            supportingText = {
                if(passwordNotConfirmed){
                    Text(stringResource(R.string.confirm_password_hint))
                }
            },
            visualTransformation = PasswordVisualTransformation(),
        )
        Button(
            onClick = {
                usernameError = checkUsername(username)
                passwordError = checkPassword(password)
                passwordNotConfirmed = password != confirmPassword
                if (usernameError.isBlank() && passwordError.isBlank() && !passwordNotConfirmed) {
                    onRegister(username, password)
                    loading = true
                }
            },
            modifier = Modifier.padding(top = 8.dp),
            enabled = !loading,
        ) {
            Text(text = "Register")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Click to Login",
            color = Color.Blue,
            modifier = Modifier
                .clickable {
                    onSwitchToLogin()
                }
                .padding(4.dp),
            fontSize = 16.sp,
            style = TextStyle(textDecoration = TextDecoration.Underline)
        )
    }

}

fun checkUsername(username: String): String {
    return when {
        username.isBlank() -> "Username cannot be empty"
        username.length < 3 -> "Username must be at least 3 characters"
        else -> ""
    }
}

fun checkPassword(password: String): String {
    return when {
        password.isBlank() -> "Password cannot be empty"
        password.length < 6 -> "Password must be at least 6 characters"
        else -> ""
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(
        onSwitchToLogin = {},
        onRegister = { _, _ -> }
    )
}