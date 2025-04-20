package com.example.trafficviolationpicturemanagementsystem.model

val BASE_URL = "http://10.0.2.2:8000"

data class UserImage(
    val id: Int,
    val url: String,
    val description: String,
){
    fun fullUrl() = BASE_URL + url
}

data class UserImageResponse(
    val success: Boolean,
    val data: List<UserImage>
)