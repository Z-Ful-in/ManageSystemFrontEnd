package com.example.trafficviolationpicturemanagementsystem.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MockAuthViewModel(application: Application): AuthViewModel(application) {
    private val _isLoggedIn = MutableLiveData(false)
    override val isLoggedIn: LiveData<Boolean> = _isLoggedIn
}