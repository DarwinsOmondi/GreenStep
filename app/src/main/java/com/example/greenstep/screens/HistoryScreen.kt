package com.example.greenstep.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.example.greenstep.CarbonInterfaceViewModel
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
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
        topBar = {
            TopAppBar(
                title = { Text("History") },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF43A047))
            )
        },
        bottomBar = { BottomNavigationBar(navController) },
        containerColor = Color(0xFFFFFFFF)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            if (footprints.value.isEmpty()) {
                Text("No history found", color = Color.Gray)
            } else {
                val totalVehicleEmissions = footprints.value.sumOf {
                    (it["milesDriven"] as? Number)?.toDouble() ?: 0.0
                }
                val totalElectricityEmissions = footprints.value.sumOf {
                    (it["electricityUsed"] as? Number)?.toDouble() ?: 0.0
                }

                Spacer(Modifier.height(16.dp))

                CarbonEmissionsPieChart(
                    vehicleEmissions = totalVehicleEmissions.toFloat(),
                    electricityEmissions = totalElectricityEmissions.toFloat()
                )


                Spacer(Modifier.height(16.dp))

                Text(
                    "Carbon FootPrint History",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        color = Color.Black
                    )
                )

                Spacer(Modifier.height(16.dp))

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
        colors = CardDefaults.cardColors(Color(0xFFD9D9D9))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Date: $formattedDate",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                textAlign = TextAlign.End,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))

            Row {
                Text(
                    text = "Miles Driven: ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.Black)

                Text(
                    "${(entry["milesDriven"] as? Number)?.toDouble() ?: 0.0}", color = Color(0xFF43A047),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,)
                Text(
                    text = "miles",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.Black)
            }
            Row {
                Text(
                    text = "Electricity Used: ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.Black)

                Text(
                    " ${(entry["electricityUsed"] as? Number)?.toDouble() ?: 0.0}", color = Color(0xFF43A047),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,)
                Text(
                    text = "kWh",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.Black)
            }
            Row {
                Text(
                    text = "Carbon Emission: ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.Black)

                Text(
                    " ${(entry["carbonEmission"] as? Number)?.toDouble() ?: 0.0} ", color = Color(0xFF43A047),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,)
                Text(
                    text = "kg CO₂",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.Black)
            }
        }
    }
}

@Composable
fun CarbonEmissionsPieChart(vehicleEmissions: Float, electricityEmissions: Float) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                PieChart(context).apply {
                    description.isEnabled = false
                    setUsePercentValues(true)
                    setEntryLabelColor(Color.Black.toArgb())
                    legend.isEnabled = true
                }
            },
            update = { pieChart ->
                val entries = listOf(
                    PieEntry(vehicleEmissions, "Vehicle"),
                    PieEntry(electricityEmissions, "Electricity")
                )

                val dataSet = PieDataSet(entries, "Carbon Emissions").apply {
                    colors = listOf(
                        Color(0xFF1D6FBC).toArgb(),
                        Color(0xFF0BC226).toArgb()
                    )
                    valueTextSize = 14f
                    valueTextColor = Color.White.toArgb()
                }

                val pieData = PieData(dataSet)
                pieChart.data = pieData
                pieChart.invalidate()
            }
        )
    }
}