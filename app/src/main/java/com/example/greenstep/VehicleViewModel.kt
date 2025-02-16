package com.example.greenstep

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.greenstep.Vehicle.VehicleModel
import com.example.greenstep.Vehicle.VehicleModelItem
import com.example.greenstep.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VehicleViewModel : ViewModel() {
    private val _vehicleModels = MutableStateFlow<List<VehicleModelItem>>(emptyList())
    val vehicleModels: StateFlow<List<VehicleModelItem>> = _vehicleModels

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun fetchVehicleModels(vehicleMakeId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response: List<VehicleModelItem> = ApiClient.carbonInterfaceApi.getVehicleModels(vehicleMakeId)
                _vehicleModels.value = response // ✅ Now returning List<VehicleModelItem>
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
