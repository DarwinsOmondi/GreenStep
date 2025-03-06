package com.example.greenstep

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.greenstep.network.Constants
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import kotlinx.coroutines.launch

class CarbonAnalysisViewModel : ViewModel() {
    var analysisResult = mutableStateOf<String?>(null)  // Holds AI response
    var isLoading = mutableStateOf(false)               // Tracks loading state
    var errorMessage = mutableStateOf<String?>(null)    // Holds error messages

    private val model = GenerativeModel(
        modelName = "gemini-1.5-flash-001",
        apiKey = Constants.geminiApiKey,
        safetySettings = listOf(
            SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE)
        )
    )

    fun analyzeCarbonEmissions(totalVehicleEmissions: Float, milesDrivenPerDay: Float, electricalConsumptionPerDay: Float,vehicleModel:String, selectedFuelType:String, selectedElectricitySource:String) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            try {
                val query = """
                               Carbon Footprint Analysis:
                               - Total Emissions: $totalVehicleEmissions kg CO₂ (calculated via Carbon Interface API)
                               - Electricity Consumption: $electricalConsumptionPerDay kWh from $selectedElectricitySource
                               - Vehicle Usage: $milesDrivenPerDay miles/day in a $vehicleModel using $selectedFuelType
                                Breakdown of emissions by source,please be brief:
                            """.trimIndent()


                val response = model.generateContent(query)
                analysisResult.value = response.text ?: "No response from AI."
            } catch (e:Exception){
                errorMessage.value = "Error: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
}
