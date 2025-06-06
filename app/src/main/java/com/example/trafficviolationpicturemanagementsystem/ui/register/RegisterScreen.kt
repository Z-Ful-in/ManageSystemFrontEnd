package com.example.trafficviolationpicturemanagementsystem.ui.register

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trafficviolationpicturemanagementsystem.R
import com.example.trafficviolationpicturemanagementsystem.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    onSwitchToLogin: () -> Unit,
    onRegister: (String, String) -> Unit,
    viewModel: AuthViewModel,
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
            text = "注册",
            modifier = Modifier.padding(bottom = 32.dp),
            fontSize = 24.sp,
        )
        TextField(
            value = username,
            onValueChange = {
                username = it
                usernameError = ""
                viewModel.clearResult()
            },
            label = { Text(stringResource(R.string.username_label)) },
            placeholder = { Text(stringResource(R.string.username_placeholder)) },
            modifier = Modifier.padding(bottom = 8.dp),
            isError = usernameError.isNotBlank(),
            supportingText = {
                if(usernameError.isNotBlank()){
                    Text(
                        text = usernameError,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else if (usernameRegistered.isNotBlank()){
                    Text(
                        text = usernameRegistered,
                        color = MaterialTheme.colorScheme.error
                    )
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
            Text(text = "注册")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "点击登录",
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
        username.isBlank() -> "用户名不能为空"
        username.length < 3 -> "用户名长度至少为3个字符"
        else -> ""
    }
}

fun checkPassword(password: String): String {
    return when {
        password.isBlank() -> "密码不能为空"
        password.length < 6 -> "密码长度至少为6个字符"
        else -> ""
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    val application = Application()
    RegisterScreen(
        onSwitchToLogin = {},
        onRegister = { _, _ -> },
        viewModel = AuthViewModel(application) // MockAuthViewModel(application
    )
}