package com.example.greenstep.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarbonEmissionForm(onComplete:() -> Unit) {
    var ownsVehicle by remember { mutableStateOf(true) }
    var vehicleModel by remember { mutableStateOf("") }
    var selectedFuelType by remember { mutableStateOf("Select Fuel Type") }
    var selectedElectricitySource by remember { mutableStateOf("Select Electricity Source") }
    val fuelTypes = listOf("Gasoline", "Diesel", "Electric", "Hybrid", "Hydrogen Fuel Cell")
    val electricitySources = listOf("Coal", "Natural Gas", "Oil", "Nuclear", "Hydropower", "Wind", "Solar", "Biomass", "Geothermal")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text("Do you own a vehicle", fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = ownsVehicle, onClick = { ownsVehicle = true })
            Text("Yes", modifier = Modifier.padding(end = 16.dp))
            RadioButton(selected = !ownsVehicle, onClick = { ownsVehicle = false })
            Text("No")
        }

        if (ownsVehicle) {
            Text("What vehicle model do you drive", fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = vehicleModel,
                onValueChange = {
                    vehicleModel = it
                },
                label = { Text("Vehicle Model",
                    color = Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor =Color.Black,
                    focusedBorderColor = Color.DarkGray,
                    unfocusedBorderColor = Color.Black
                ),
            )


            Text("What gasoline type do you use", fontWeight = FontWeight.Bold)
            DropdownMenuBox(selectedItem = selectedFuelType, options = fuelTypes) { selectedFuelType = it }
        }

        Text("Where do you get your electricity from", fontWeight = FontWeight.Bold)
        DropdownMenuBox(selectedItem = selectedElectricitySource, options = electricitySources) { selectedElectricitySource = it }


        TextButton(
            onClick = {
                onComplete()
            },
        ) {
            Text("Next")
        }
    }
}

@Composable
fun DropdownMenuBox(selectedItem: String, options: List<String>, onItemSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

        Box {
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
