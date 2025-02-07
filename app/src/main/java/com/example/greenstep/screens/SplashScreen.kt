package com.example.greenstep.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SplashScreen(onSplashNavigation: () -> Unit) {
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0BA121),
            Color(0xFF000000)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier.fillMaxSize()
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "GreenStep",
                    style = TextStyle(
                        fontSize = typography.headlineLarge.fontSize,
                        fontFamily = typography.titleLarge.fontFamily,
                        fontWeight = typography.labelLarge.fontWeight
                    ),
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(16.dp))


                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Track your carbon emission with ease",
                    style = TextStyle(
                        fontSize = typography.headlineLarge.fontSize,
                        fontFamily = typography.labelLarge.fontFamily,
                        fontWeight = typography.headlineSmall.fontWeight
                    ),
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(160.dp))

                Button(
                    onClick = {
                        onSplashNavigation()
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    contentPadding = PaddingValues(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF40D256),
                        contentColor = Color.Black
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 16.dp
                    )
                ) {
                    Text(
                        text = "Get Started",
                        style = TextStyle(
                            fontSize = typography.bodyLarge.fontSize,
                            fontFamily = typography.titleLarge.fontFamily,
                            fontWeight = typography.titleLarge.fontWeight
                        )
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen(onSplashNavigation = {})
}