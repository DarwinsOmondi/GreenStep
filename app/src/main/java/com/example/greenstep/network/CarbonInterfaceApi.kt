package com.example.greenstep.network

import com.example.greenstep.Vehicle.VehicleModel
import com.example.greenstep.Vehicle.VehicleModelItem
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface CarbonInterfaceApi {

    @Headers("Content-Type: application/json")
    @POST("estimates")
    suspend fun getCarbonEstimateVehicle(@Body request: CarbonEstimateRequestVehicle): CarbonEstimateResponse

    @Headers("Content-Type: application/json")
    @POST("estimates")
    suspend fun getCarbonEstimateElectricity(@Body request: CarbonEstimateRequestElectricity): CarbonEstimateResponse

    @GET("vehicle_makes/{vehicle_make_id}/vehicle_models")
    suspend fun getVehicleModels(@Path("vehicle_make_id") vehicleMakeId: String): List<VehicleModelItem>
}
