package com.example.greenstep.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.greenstep.CarbonFromSheetViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarbonEmissionForm(
    onComplete: () -> Unit,
    carbonFromSheetViewModel: CarbonFromSheetViewModel = viewModel()
) {
    var ownsVehicle by remember { mutableStateOf(true) }
    var vehicleModel by remember { mutableStateOf("") }
    var selectedFuelType by remember { mutableStateOf("Select Fuel Type") }
    var selectedElectricitySource by remember { mutableStateOf("Select Electricity Source") }
    val fuelTypes = listOf("Gasoline", "Diesel", "Electric", "Hybrid", "Hydrogen Fuel Cell")
    val electricitySources = listOf("Coal", "Natural Gas", "Oil", "Nuclear", "Hydropower", "Wind", "Solar", "Biomass", "Geothermal")

    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text("Do you own a vehicle", fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = ownsVehicle, onClick = { ownsVehicle = true })
            Text("Yes", modifier = Modifier.padding(end = 16.dp).clickable { ownsVehicle = true })
            RadioButton(selected = !ownsVehicle, onClick = { ownsVehicle = false })
            Text("No", modifier = Modifier.clickable { ownsVehicle = false })
        }

        if (ownsVehicle) {
            Text("What vehicle model do you drive", fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = vehicleModel,
                onValueChange = { vehicleModel = it },
                label = { Text("Vehicle Model") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = Color.DarkGray,
                    unfocusedBorderColor = Color.Black
                )
            )

            Text("What fuel type do you use", fontWeight = FontWeight.Bold)
            DropdownMenuBox(selectedItem = selectedFuelType, options = fuelTypes) { selectedFuelType = it }
        }

        Text("Where do you get your electricity from", fontWeight = FontWeight.Bold)
        DropdownMenuBox(selectedItem = selectedElectricitySource, options = electricitySources) { selectedElectricitySource = it }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            TextButton(
                onClick = {
                    scope.launch {
                        try {
                            isLoading = true
                            carbonFromSheetViewModel.saveSheetData(vehicleModel, selectedFuelType, selectedElectricitySource)
                        } finally {
                            isLoading = false
                        }
                    }
                },
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Save")
                }
            }

            TextButton(
                onClick = { onComplete() },
            ) {
                Text("Next")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuBox(selectedItem: String, options: List<String>, onItemSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Button(onClick = { expanded = true }) {
            Text(selectedItem)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onItemSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
