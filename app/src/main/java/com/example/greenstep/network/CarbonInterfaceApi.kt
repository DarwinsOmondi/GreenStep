package com.example.greenstep.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface CarbonInterfaceApi {
    @Headers("Content-Type: application/json")
    @POST("estimates")
   suspend fun getCarbonEstimateVehicle(@Body request: CarbonEstimateRequestVehicle):CarbonEstimateResponse

    @Headers("Content-Type: application/json")
    @POST("estimates")
   suspend fun getCarbonEstimateElectricity(@Body request: CarbonEstimateRequestElectricity):CarbonEstimateResponse
}