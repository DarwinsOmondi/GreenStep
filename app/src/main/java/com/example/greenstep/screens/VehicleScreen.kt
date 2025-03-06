package com.example.greenstep.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.greenstep.Vehicle.VehicleModelItem
import com.example.greenstep.VehicleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleScreen(vehicleMakeId: String, viewModel: VehicleViewModel = viewModel(),navController: NavHostController) {
    val vehicleModels by viewModel.vehicleModels.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    val filteredModels = vehicleModels.filter {
        it.data.attributes.name.contains(searchQuery, ignoreCase = true)
    }

    LaunchedEffect(vehicleMakeId) {
        viewModel.fetchVehicleModels(vehicleMakeId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vehicle Models") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.fetchVehicleModels(vehicleMakeId) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search Vehicle Models...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(12.dp)
            )

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator()
                    }
                    errorMessage != null -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = errorMessage ?: "Unknown error",
                                color = Color.Red,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { viewModel.fetchVehicleModels(vehicleMakeId) }) {
                                Text("Retry")
                            }
                        }
                    }
                    filteredModels.isNotEmpty() -> {
                        VehicleList(filteredModels)
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
}

@Composable
fun VehicleList(models: List<VehicleModelItem>) {
    LazyColumn(modifier = Modifier.padding(8.dp)) {
        items(models) { model ->
            VehicleCard(model)
        }
    }
}

@Composable
fun VehicleCard(model: VehicleModelItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Model: ${model.data.attributes.name}",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Vehicle Make: ${model.data.attributes.vehicle_make}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "ID: ${model.data.id}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
        }
    }
}
