package com.example.greenstep.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.greenstep.Vehicle.VehicleModel
import com.example.greenstep.Vehicle.VehicleModelItem
import com.example.greenstep.VehicleViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleScreen(vehicleMakeId: String, viewModel: VehicleViewModel = viewModel()) {
    val vehicleModels by viewModel.vehicleModels.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Ensure data is refetched when vehicleMakeId changes
    LaunchedEffect(vehicleMakeId) {
        viewModel.fetchVehicleModels(vehicleMakeId)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Vehicle Models") })
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator()
                }
                errorMessage != null -> {
                    Text(
                        text = errorMessage ?: "Unknown error",
                        color = Color.Red,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                vehicleModels.isNotEmpty() -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(vehicleModels) { model ->
                            VehicleCard(model)
                        }
                    }
                }
                else -> {
                    Text(
                        text = "No vehicle models found.",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}


@Composable
fun VehicleCard(model: VehicleModelItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Model: ${model.data.attributes.name}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "vehicle make: ${model.data.attributes.vehicle_make}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "ID: ${model.data.id}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}


@Composable
fun VehicleList(models: List<VehicleModelItem>) {
    LazyColumn {
        items(models) { model ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Model: ${model.data.attributes.name}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "ID: ${model.data.id}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}