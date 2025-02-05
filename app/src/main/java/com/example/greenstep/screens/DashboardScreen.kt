package com.example.greenstep.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.example.greenstep.CarbonInterfaceViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate

@Composable
fun DashboardScreen(viewModel: CarbonInterfaceViewModel, navController: NavHostController) {
    val footprints = remember { mutableStateOf<List<CarbonFootPrintData>>(emptyList()) }
    var miles by remember { mutableFloatStateOf(0f) }
    var electricity by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        viewModel.fetchCarbonFootprintData { data ->
            val parsedData = data.mapNotNull { doc ->
                try {
                    CarbonFootPrintData(
                        milesDriven = (doc["milesDriven"] as? Number)?.toFloat() ?: 0f,
                        electricityUsed = (doc["electricityUsed"] as? Number)?.toFloat() ?: 0f,
                        carbonEmission = (doc["carbonEmission"] as? Number)?.toFloat() ?: 0f
                    )
                } catch (e: Exception) {
                    null
                }
            }
            footprints.value = parsedData

            miles = parsedData.sumOf { it.milesDriven.toDouble() }.toFloat()
            electricity = parsedData.sumOf { it.electricityUsed.toDouble() }.toFloat()
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome",
                style = MaterialTheme.typography.headlineSmall.copy(color = Color(34, 139, 34))
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Carbon Footprint Overview",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF66BB6A))
            )
            Spacer(modifier = Modifier.height(8.dp))

            CarbonFootprintBarChart(vehicleEmissions = miles, electricityEmissions = electricity)
        }
    }
}

@Composable
fun CarbonFootprintBarChart(vehicleEmissions: Float, electricityEmissions: Float) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            factory = { context ->
                BarChart(context).apply {
                    description.isEnabled = false
                    setDrawGridBackground(false)
                    setDrawBarShadow(false)
                    setTouchEnabled(true)
                    legend.isEnabled = true

                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.setDrawGridLines(false)
                    axisLeft.setDrawGridLines(false)
                    axisRight.isEnabled = false
                    axisLeft.axisMinimum = 0f

                    extraBottomOffset = 10f
                }
            },
            update = { chart ->
                val entries = listOf(
                    BarEntry(0f, vehicleEmissions.takeIf { it.isFinite() } ?: 0f),
                    BarEntry(1f, electricityEmissions.takeIf { it.isFinite() } ?: 0f)
                )

                val dataSet = BarDataSet(entries, "Carbon Footprint").apply {
                    colors = listOf(
                        Color(0xFF43A047).toArgb(),
                        Color(0xFF66BB6A).toArgb()
                    )
                    valueTextColor = Color.White.toArgb()
                    valueTextSize = 14f
                }

                val barData = BarData(dataSet).apply {
                    barWidth = 0.9f
                }

                chart.data = barData
                chart.notifyDataSetChanged()
                chart.invalidate()
            }
        )
    }
}

data class CarbonFootPrintData(
    val milesDriven: Float,
    val electricityUsed: Float,
    val carbonEmission: Float
)
