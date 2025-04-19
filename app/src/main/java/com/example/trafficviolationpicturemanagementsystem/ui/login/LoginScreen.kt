package com.example.trafficviolationpicturemanagementsystem.ui.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trafficviolationpicturemanagementsystem.R

@Composable
fun LoginScreen(
    onSwitchToRegister: () -> Unit,
    onLogin: (String, String) -> Unit,
    passwordError: String = "",
    userNameError: String = "",
){
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var isUsernameEmpty by remember { mutableStateOf(false) }
    var isPasswordEmpty by remember { mutableStateOf(false) }
    var isPasswordVisible by remember { mutableStateOf(false) }

    var loading by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Login",
            modifier = Modifier.padding(bottom = 32.dp),
            fontSize = 24.sp,
        )
        TextField(
            value = username,
            onValueChange = {
                username = it
                isUsernameEmpty = false
            },
            label = { Text(stringResource(R.string.username_label)) },
            placeholder = { Text(stringResource(R.string.username_placeholder)) },
            modifier = Modifier.padding(bottom = 8.dp),
            isError = isUsernameEmpty,
            supportingText = {
                if(isUsernameEmpty){
                    Text(
                        text = stringResource(R.string.username_hint),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else if(userNameError.isNotBlank()){
                    Text(
                        text = userNameError,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )
        TextField(
            value = password,
            onValueChange = {
                password = it
                isPasswordEmpty = false
            },
            label = { Text(stringResource(R.string.password_label)) },
            placeholder = { Text(stringResource(R.string.password_placeholder)) },
            modifier = Modifier.padding(bottom = 8.dp),
            isError = isPasswordEmpty,
            supportingText = {
                if(isPasswordEmpty){
                    Text(
                        text = stringResource(R.string.password_hint),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else if (passwordError.isNotBlank()){
                    Text(
                        text = passwordError,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            visualTransformation = if (isPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        isPasswordVisible = !isPasswordVisible
                    }
                ) {
                    Icon(
                        imageVector = if(isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = stringResource(R.string.password_visibility)
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Button(
            onClick = {
                isUsernameEmpty = username.isBlank()
                isPasswordEmpty = password.isBlank()
                if (!isUsernameEmpty && !isPasswordEmpty) {
                    onLogin(username, password)
                    loading = true
                }
            },
            modifier = Modifier.padding(top = 8.dp),
            enabled = !loading
        ) {
            Text(text = "Login")
        }
        Text(
            text = stringResource(R.string.click_to_register),
            color = Color.Blue,
            modifier = Modifier
                .clickable {
                    onSwitchToRegister()
                }
                .padding(4.dp),
            fontSize = 10.sp,
            style = TextStyle(textDecoration = TextDecoration.Underline)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview(){
    LoginScreen(
        onSwitchToRegister = {},
        onLogin = { _, _ -> }
    )
}