package com.example.greenstep.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.greenstep.CarbonInterfaceViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    navController: NavHostController,
    viewModel: CarbonInterfaceViewModel
) {
    val footprints = remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    LaunchedEffect(Unit) {
        viewModel.fetchCarbonFootprintData { data ->
            footprints.value = data
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        containerColor = Color(245, 245, 220)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Calculation History",
                fontWeight = FontWeight.Bold,
                color = Color(34, 139, 34),
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (footprints.value.isEmpty()) {
                Text("No history found", color = Color.Gray)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(footprints.value) { entry ->
                        HistoryCard(entry)
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryCard(entry: Map<String, Any>) {
    val timestamp = entry["timestamp"] as? Long
    val formattedDate = timestamp?.let {
        SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(it))
    } ?: "Unknown Date"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(245, 245, 220))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Date: $formattedDate",
                fontWeight = FontWeight.Bold,
                color = Color(34, 139, 34) // Dark Green
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("Miles Driven: ${entry["milesDriven"]} miles", color = Color.DarkGray)
            Text("Electricity Used: ${entry["electricityUsed"]} kWh", color = Color.DarkGray)
            Text(
                text = "Carbon Emission: ${entry["carbonEmission"]} kg CO₂",
                color = Color(34, 139, 34), // Dark Green
                fontWeight = FontWeight.Bold
            )
        }
    }
}
