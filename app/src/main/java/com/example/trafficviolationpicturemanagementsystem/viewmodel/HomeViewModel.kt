package com.example.trafficviolationpicturemanagementsystem.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.trafficviolationpicturemanagementsystem.data.repository.UserRepository
import com.example.trafficviolationpicturemanagementsystem.model.UserImage
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

open class HomeViewModel(application: Application): AndroidViewModel(application) {
    private val userRepository = UserRepository(application)

    private val _userName = MutableLiveData<String>()
    open val userName: LiveData<String> get() = _userName

    private val _isPageSelected = MutableLiveData<Int>()
    open val isPageSelected: LiveData<Int> get() = _isPageSelected

    private val _userImageList = MutableLiveData<List<UserImage>>()
    val userImageList: LiveData<List<UserImage>> get() = _userImageList

    init {
        _isPageSelected.value = 0
        getUserName()
    }
    fun getUserName(){
       viewModelScope.launch {
          _userName.value = userRepository.getUserName()
       }
    }

    fun loadData(){
        viewModelScope.launch {
            val name = userRepository.getUserName()
            _userName.value = name
            _userImageList.value = userRepository.getUserImages(name)
        }
    }

    fun onViewClick(){
        _isPageSelected.value = 0
    }
    fun onUploadClick(){
        _isPageSelected.value = 1
    }

    fun logout(
        navigateToLogin: () -> Unit
    ) {
        viewModelScope.launch {
            userName.value?.let { userRepository.logout(it) }
            _isPageSelected.value = 0
            navigateToLogin()
        }
    }

    fun getUserImages() {
        viewModelScope.launch {
            val images = userRepository.getUserImages(userName.value ?: "")
            _userImageList.value = images
        }
    }

    fun deleteImage(id: Int, onDeleteSuccess: () -> Unit){
        viewModelScope.launch {
            val success = userRepository.deleteImage(id)
            if (success) {
                onDeleteSuccess()
            }
        }
    }

    fun uploadImage(
        uri: Uri,
        description: String,
        username: String,
        onUploadSuccess: () -> Unit,
        onUploadFailed: () -> Unit
    ) {
        viewModelScope.launch {
           val contentResolver = getApplication<Application>().contentResolver
            val inputStream = contentResolver.openInputStream(uri)
            val file = File.createTempFile("upload", ".jpg")
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            outputStream.close()
            inputStream?.close()

            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)
            val descriptionPart = description.toRequestBody("text/plain".toMediaType())
            val usernamePart = username.toRequestBody("text/plain".toMediaType())

            val response = userRepository.uploadImage(imagePart, descriptionPart, usernamePart)
            if (response) {
                onUploadSuccess()
            } else {
                onUploadFailed()
            }
        }
    }
}