package com.example.greenstep

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.greenstep.network.ApiClient
import com.example.greenstep.network.CarbonEstimateRequestElectricity
import com.example.greenstep.network.CarbonEstimateRequestVehicle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class CarbonInterfaceViewModel : ViewModel() {
    private val _carbonEstimateVehicle = MutableLiveData<Float>()
    val carbonEstimateVehicle: LiveData<Float> = _carbonEstimateVehicle

    private val _carbonEstimateElectricity = MutableLiveData<Float>()
    val carbonEstimateElectricity: LiveData<Float> = _carbonEstimateElectricity

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error


    fun updateCarbonEstimateVehicle(dataVehicle: Float, dataElectricity: Float) {
        _carbonEstimateVehicle.value = dataVehicle
        _carbonEstimateElectricity.value = dataElectricity
    }


    fun calculateCarbonFootprint(
        milesDriven: Float,
        electricityUsed: Float,
        onSuccess: (Float, Float) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val requestVehicle = CarbonEstimateRequestVehicle(
                    type = "vehicle",
                    distance_unit = "mi",
                    distance_value = milesDriven,
                    vehicle_model_id = "7268a9b7-17e8-4c8d-acca-57059252afe9"
                )
                val requestElectricity = CarbonEstimateRequestElectricity(
                    type = "electricity",
                    electricity_unit = "mwh",
                    electricity_value = electricityUsed,
                    country = "us",
                    state = "fl"
                )

                val responseVehicle =
                    ApiClient.carbonInterfaceApi.getCarbonEstimateVehicle(requestVehicle)
                val responseElectricity =
                    ApiClient.carbonInterfaceApi.getCarbonEstimateElectricity(requestElectricity)

                val carbonEstimateVehicle = responseVehicle.data.attributes.carbon_kg
                val carbonEstimateElectricity = responseElectricity.data.attributes.carbon_kg
                onSuccess(carbonEstimateVehicle, carbonEstimateElectricity)
            } catch (e: Exception) {
                onError("Failed to calculate carbon footprint: ${e.message}")
            }
        }
    }

    fun saveCarbonFootprint(milesDriven: Float, electricityUsed: Float, carbonEmission: Float) {
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userDocRef = firestore.collection("users")
                .document(currentUser.uid)
                .collection("carbon_footprints")

            val carbonData = hashMapOf(
                "milesDriven" to milesDriven,
                "electricityUsed" to electricityUsed,
                "carbonEmission" to carbonEmission,
                "timestamp" to System.currentTimeMillis()
            )

            userDocRef.add(carbonData)
                .addOnSuccessListener {
                    Log.d("Firebase", "Data successfully saved for user: ${currentUser.uid}")
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Error saving data", e)
                }
        } else {
            Log.e("Firebase", "No authenticated user found.")
        }
    }

    fun fetchCarbonFootprintData(onResult: (List<Map<String, Any>>) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userDocRef = firestore.collection("users")
                .document(currentUser.uid)
                .collection("carbon_footprints")
                .orderBy("timestamp")

            userDocRef.get()
                .addOnSuccessListener { documents ->
                    val dataList = documents.map { it.data }
                    onResult(dataList)
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Error fetching data", e)
                }
        } else {
            Log.e("Firebase", "No authenticated user found.")
        }
    }
}