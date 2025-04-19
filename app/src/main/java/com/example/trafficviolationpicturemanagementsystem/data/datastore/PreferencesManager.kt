package com.example.trafficviolationpicturemanagementsystem.data.datastore

import android.content.Context
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class PreferencesManager(private val context:Context){
    suspend fun saveLoginStatus(isLoggedIn: Boolean, userName: String){
        UserPreferences.saveLoginStatus(context, isLoggedIn, userName)
    }
    fun getLoginStatus(): Boolean{
        return runBlocking {
            UserPreferences.readLoginStatus(context).first()
        }
    }
    fun getUserName(): String{
        return runBlocking {
            UserPreferences.readUserName(context).first()
        }
    }

    suspend fun getLoginStatusAsync(): Boolean {
        return UserPreferences.readLoginStatus(context).first()
    }
    suspend fun getUserNameAsync(): String {
        return UserPreferences.readUserName(context).first()
    }
}

