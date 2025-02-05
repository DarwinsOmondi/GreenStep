package com.example.greenstep.network

data class CarbonEstimateRequestVehicle (
    val type: String,
    val distance_unit: String,
    val distance_value: Float,
    val vehicle_model_id: String
)

data class CarbonEstimateRequestElectricity(
    val type:String,
    val electricity_unit:String,
    val electricity_value: Float,
    val country: String,
    val state: String,
)