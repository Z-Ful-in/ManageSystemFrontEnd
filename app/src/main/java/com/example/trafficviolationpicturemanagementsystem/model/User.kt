package com.example.trafficviolationpicturemanagementsystem.model

data class User(
    val username: String,
    val password: String
)

data class Message(
    val code: Int,
    val message: String
)

data class AuthResponse(
    val success: Boolean,
    val message: Message
)