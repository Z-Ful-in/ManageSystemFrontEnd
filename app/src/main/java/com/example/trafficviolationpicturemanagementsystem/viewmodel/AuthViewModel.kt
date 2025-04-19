package com.example.trafficviolationpicturemanagementsystem.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.trafficviolationpicturemanagementsystem.data.repository.LoginRegisterResult
import com.example.trafficviolationpicturemanagementsystem.data.repository.UserRepository
import kotlinx.coroutines.launch

open class AuthViewModel(application: Application): AndroidViewModel(application) {
    private val userRepository = UserRepository(application)

    private val _isLoggedIn = MutableLiveData<Boolean>()
    open val isLoggedIn: LiveData<Boolean> get() = _isLoggedIn

    private val _loginRegisterResult = MutableLiveData<LoginRegisterResult>()
    open val loginRegisterResult: LiveData<LoginRegisterResult> get() = _loginRegisterResult

    init {
        checkLoginStatus()
    }

    fun checkLoginStatus(){
       viewModelScope.launch {
           _isLoggedIn.value = userRepository.isLoggedIn()
       }
    }

    fun login(
        username: String,
        password: String,
        navigateToHome: () -> Unit,
    ){
        viewModelScope.launch {
            _loginRegisterResult.value = userRepository.login(username, password)
            when(loginRegisterResult.value){
                is LoginRegisterResult.Success -> {
                    _isLoggedIn.value = true
                    navigateToHome()
                }
                is LoginRegisterResult.Error -> {
                    _isLoggedIn.value = false
                }
                null -> {
                    _isLoggedIn.value = false
                }
            }
        }
    }
    fun register(
        username: String,
        password: String,
        navigateToLogin: () -> Unit
    ){
        viewModelScope.launch {
            _loginRegisterResult.value = userRepository.register(username, password)
            when(_loginRegisterResult.value){
                is LoginRegisterResult.Success -> {
                    _isLoggedIn.value = false
                    navigateToLogin()
                }
                is LoginRegisterResult.Error -> {
                    _isLoggedIn.value = false
                }
                null -> {
                    _isLoggedIn.value = false
                }
            }
        }
    }
}