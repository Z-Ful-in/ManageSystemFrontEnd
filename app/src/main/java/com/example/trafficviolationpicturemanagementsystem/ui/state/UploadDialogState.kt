package com.example.trafficviolationpicturemanagementsystem.ui.state

sealed class UploadDialogState {
    object None: UploadDialogState()
    data class UploadSuccess(val message: String): UploadDialogState()
    data class NetworkError(val message: String): UploadDialogState()
    data class FieldMissingError(val message: String): UploadDialogState()
}