package com.example.greenstep.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.greenstep.CarbonAnalysisViewModel
import com.example.greenstep.CarbonInterfaceViewModel
import com.example.greenstep.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: CarbonInterfaceViewModel, navController: NavHostController) {
    val footprints = remember { mutableStateOf<List<CarbonFootPrintData>>(emptyList()) }
    var miles by remember { mutableFloatStateOf(0f) }
    var electricity by remember { mutableFloatStateOf(0f) }
    var userName by remember { mutableStateOf("User Name") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var refreshTrigger by remember { mutableStateOf(false) } // UI refresh trigger
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val footprintsData = remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    val carbonAnalysisViewModel :CarbonAnalysisViewModel = viewModel()




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

    LaunchedEffect(Unit) {
        currentUser?.uid?.let { uid ->
            firestore.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        userName = document.getString("name") ?: "User Name"
                        val imageUriString = document.getString("imageUri") ?: ""
                        imageUri = if (imageUriString.isNotEmpty()) Uri.parse(imageUriString) else null
                    }
                }
        }
    }





    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF43A047)),
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color.Gray, CircleShape)
                        ) {
                            Image(
                                painter = imageUri?.let { rememberAsyncImagePainter(it) }
                                    ?: painterResource(id = R.drawable.baseline_account_circle_24),
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = userName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.Black,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            )
        },
        bottomBar = { BottomNavigationBar(navController) },
        containerColor = Color(0xFFFFFFFF)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box (
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.Start)
            ){
                Column(Modifier.padding(16.dp)){
                    Text(
                        "Activity",
                        style = TextStyle(
                            fontStyle = typography.bodyLarge.fontStyle,
                            fontSize = typography.titleLarge.fontSize,
                            fontFamily = typography.bodyLarge.fontFamily,
                            fontWeight = typography.titleLarge.fontWeight
                        )
                    )

                    Text(
                        "summary",
                        style = TextStyle(
                            fontStyle = typography.bodyLarge.fontStyle,
                            fontSize = typography.titleLarge.fontSize,
                            fontFamily = typography.bodyLarge.fontFamily,
                            fontWeight = typography.titleLarge.fontWeight
                        )
                    )
                }

            }
            Spacer(Modifier.height(16.dp))
            CarbonFootprintBarChart(vehicleEmissions = miles, electricityEmissions = electricity)
            val totalVehicleEmissions = footprints.value.sumOf { it.milesDriven.toDouble() }.toFloat()
            val totalElectricityEmissions = footprints.value.sumOf { it.electricityUsed.toDouble() }.toFloat()

            val totalCarbonEmission = (totalVehicleEmissions + totalElectricityEmissions)

                carbonAnalysisViewModel.analyzeCarbonEmissions(totalCarbonEmission,totalVehicleEmissions,totalElectricityEmissions)

            GeminiAnalysis(carbonAnalysisViewModel)
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
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor =Color(0xFFFFFFFF))
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
                        Color(0xFF1D6FBC).toArgb(),
                        Color(0xFF0BC226).toArgb()
                    )
                    valueTextColor = Color.Black.toArgb()
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



@Composable
fun GeminiAnalysis(geminiAnalysisViewModel:CarbonAnalysisViewModel){

    val geminiResponse = geminiAnalysisViewModel.analysisResult
    Card (
        Modifier.wrapContentSize(),
        colors = CardDefaults.cardColors(containerColor =Color(0xFFFFFFFF)),
        elevation = CardDefaults.cardElevation(4.dp)
    ){
        if (geminiAnalysisViewModel.isLoading.value){
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }else{
            Text("${geminiResponse.value}",
                color = Color.Black,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black)
        }
    }
}