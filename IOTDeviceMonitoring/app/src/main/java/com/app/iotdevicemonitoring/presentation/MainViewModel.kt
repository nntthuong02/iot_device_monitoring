package com.app.iotdevicemonitoring.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.iotdevicemonitoring.data.models.ToggleRequest
import com.app.iotdevicemonitoring.network.RetrofitInstance
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    fun toggleDevice(lightId: String, state: Int, onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.toggleDevice(ToggleRequest(lightId, state))
                if (response.success) {
                    onSuccess()
                }
            } catch (e: Exception) {
                onError(e)
            }
        }
    }


}
