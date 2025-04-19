package com.example.trafficviolationpicturemanagementsystem.model

data class UserImage(
    val id: Int,
    val url: String,
    val description: String,
)

data class UserImageResponse(
    val success: Boolean,
    val data: List<UserImage>
)