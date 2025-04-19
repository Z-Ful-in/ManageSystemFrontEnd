package com.example.trafficviolationpicturemanagementsystem.data.repository

import android.content.Context
import com.example.trafficviolationpicturemanagementsystem.data.datastore.PreferencesManager
import com.example.trafficviolationpicturemanagementsystem.model.User
import com.example.trafficviolationpicturemanagementsystem.model.UserImage
import com.example.trafficviolationpicturemanagementsystem.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UserRepository(private val context: Context) {
    private val preferencesManager = PreferencesManager(context)

    fun isLoggedIn(): Boolean {
        return preferencesManager.getLoginStatus()
    }

    fun getUserName(): String {
        return preferencesManager.getUserName()
    }

    suspend fun login(userName: String, password: String): LoginRegisterResult{
        return withContext(Dispatchers.IO){
            try{
                val response = RetrofitClient.apiService.login(User(userName, password))
                if(response.isSuccessful){
                    val authResponse = response.body()
                    if(authResponse!!.success){ // 后端保证返回的authResponse不为null
                        preferencesManager.saveLoginStatus(true, userName)
                        LoginRegisterResult.Success(User(userName, password))
                    }else{
                        LoginRegisterResult.Error(authResponse.message.code, authResponse.message.message ?: "Login failed")
                    }
                }
                else
                    LoginRegisterResult.Error(response.code(), response.message())
            }catch (e: Exception){
                LoginRegisterResult.Error(500, e.message ?: "Network error")
            }
        }
    }

    suspend fun register(userName: String, password: String): LoginRegisterResult {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.register(User(userName, password))
                if (response.isSuccessful) {
                    val authResponse = response.body()
                    if (authResponse!!.success) {
                        preferencesManager.saveLoginStatus(false, userName)
                        LoginRegisterResult.Success(User(userName, password))
                    } else
                        LoginRegisterResult.Error(
                            authResponse.message.code,
                            authResponse.message.message ?: "Register failed"
                        )
                } else
                    LoginRegisterResult.Error(response.code(), response.message())
            } catch (e: Exception) {
                LoginRegisterResult.Error(500, e.message ?: "Network error")
            }
        }
    }

    suspend fun logout(userName: String) {
        withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.logout(userName)
                if (response.isSuccessful) {
                    preferencesManager.saveLoginStatus(false, userName)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun getUserImages(userName: String): List<UserImage>{
        return withContext(Dispatchers.IO) {
            if(isLoggedIn()){
                try {
                    val response = RetrofitClient.apiService.getUserImages(userName)
                    if (response.success){
                        response.data.map {
                            UserImage(it.id, it.url, it.description)
                        }
                    }
                    else
                        emptyList()
                } catch (e: Exception) {
                    emptyList()
                }
                emptyList()
            }else
                emptyList()
        }
    }

    suspend fun deleteImage(id: Int): Boolean {
        return try {
            RetrofitClient.apiService.deleteImage(id)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun uploadImage(imagePart: MultipartBody.Part, descriptionPart: RequestBody):Boolean {
        return try {
            val response = RetrofitClient.apiService.uploadImage(imagePart, descriptionPart)
            response
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}