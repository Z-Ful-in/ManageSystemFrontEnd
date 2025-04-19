package com.example.trafficviolationpicturemanagementsystem.data.repository

import com.example.trafficviolationpicturemanagementsystem.model.User

sealed class LoginRegisterResult {
    data class Success(val user: User) : LoginRegisterResult()
    data class Error(val errorCode: Int, val error: String) : LoginRegisterResult()
}