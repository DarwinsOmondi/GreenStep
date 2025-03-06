package com.example.greenstep.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.greenstep.CarbonInterfaceViewModel
import com.example.greenstep.TrackingViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    navController: NavHostController,
    trackingViewModel: TrackingViewModel,
    viewModel: CarbonInterfaceViewModel = viewModel(),
) {
    var milesDriven by remember { mutableStateOf("") }
    var electricityUsed by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val carbonEstimateVehicle by viewModel.carbonEstimateVehicle.observeAsState()
    val carbonEstimateElectricity by viewModel.carbonEstimateElectricity.observeAsState()
    val errorMessage by viewModel.error.observeAsState()

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val distance = trackingViewModel.distanceCovered.collectAsState()
    val steps = trackingViewModel.stepCount.collectAsState()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        containerColor = Color(245, 245, 220)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Distance Covered: ${distance.value} meters", color = Color.Black)
            Text("Step Count: ${steps.value}", color = Color.Black)
            Text(
                text = "Carbon Footprint Calculator",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(34, 139, 34),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Calculate your carbon footprint based on your daily activities",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = milesDriven,
                onValueChange = { milesDriven = it },
                label = { Text("Miles Driven") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Info, contentDescription = "Miles Driven", tint = Color(0, 0, 128))
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(144, 238, 144),
                    unfocusedBorderColor = Color(169, 169, 169),
                 //   textColor = Color.Black
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = electricityUsed,
                onValueChange = { electricityUsed = it },
                label = { Text("Electricity Used (kWh)") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Info, contentDescription = "Electricity Used", tint = Color(0, 0, 128))
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(144, 238, 144),
                    unfocusedBorderColor = Color(169, 169, 169),
                //    textColor = Color.Black
                )
            )
            Spacer(modifier = Modifier.height(32.dp))

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = Color(173, 216, 230) // Light Blue
                )
            } else {
                Button(
                    onClick = {
                        val miles = milesDriven.toFloatOrNull()
                        val electricity = electricityUsed.toFloatOrNull()

                        if (miles != null && electricity != null) {
                            isLoading = true
                            coroutineScope.launch {
                                viewModel.calculateCarbonFootprint(
                                    miles,
                                    electricity,
                                    onSuccess = { carbonVehicle, carbonElectricity ->
                                        viewModel.updateCarbonEstimateVehicle(carbonVehicle, carbonElectricity)
                                        val totalCarbon = carbonVehicle + carbonElectricity
                                        viewModel.saveCarbonFootprint(miles, electricity, totalCarbon)
                                        isLoading = false
                                    },
                                    onError = { error ->
                                        isLoading = false
                                    }
                                )
                            }
                        } else {
                            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(255, 165, 0))
                ) {
                    Text("Calculate", color = Color.White)
                }
            }


            carbonEstimateVehicle?.let { vehicleEstimate ->
                carbonEstimateElectricity?.let { electricityEstimate ->
                    Spacer(modifier = Modifier.height(32.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(211, 211, 211))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Your Carbon Footprint",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(34, 139, 34)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${vehicleEstimate + electricityEstimate} kg CO₂",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color(34, 139, 34),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            errorMessage?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = it,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
