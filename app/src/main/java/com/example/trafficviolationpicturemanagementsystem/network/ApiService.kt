package com.example.trafficviolationpicturemanagementsystem.network

import com.example.trafficviolationpicturemanagementsystem.model.AuthResponse
import com.example.trafficviolationpicturemanagementsystem.model.User
import com.example.trafficviolationpicturemanagementsystem.model.UserImageResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @POST("/api/login")
    suspend fun login(@Body user: User): Response<AuthResponse>

    @POST("/api/register")
    suspend fun register(@Body user: User): Response<AuthResponse>

    @POST("/api/logout")
    suspend fun logout(@Body userName: String): Response<AuthResponse>

    @GET("/images/user/{userName}")
    suspend fun getUserImages(
        @Path("username") userName: String
    ): UserImageResponse

    @DELETE("/images/{id}")
    suspend fun deleteImage(
        @Path("id") id: Int
    ): Boolean

    @Multipart
    @POST("/images/upload_image")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("userName") userName: RequestBody
    ): Boolean
}