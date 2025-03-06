package com.example.greenstep

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.greenstep.screens.*
import com.example.greenstep.ui.theme.GreenStepTheme
import com.google.firebase.auth.FirebaseAuth
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    private val trackingViewModel: TrackingViewModel by viewModels { TrackingViewModelFactory(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (!isGranted) {
                Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show()
            }
        }

        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        setContent {
            GreenStepTheme {
                val navController = rememberNavController()
                val auth: FirebaseAuth = FirebaseAuth.getInstance()
                val viewModel = CarbonInterfaceViewModel()

                GreenStep(auth, navController, viewModel, trackingViewModel)
            }
        }
    }
}


@Composable
fun GreenStep(
    auth: FirebaseAuth,
    navController: NavHostController,
    viewModel: CarbonInterfaceViewModel,
    trackingViewModel: TrackingViewModel?
) {
    val startDestination = if (auth.currentUser != null) {
        "dashboard"
    } else {
        "splash"
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("splash") {
            SplashScreen(onSplashNavigation = {
                navController.navigate("signIn")
            })
        }
        composable("signUp") {
            SignUpScreen(auth, onSignUpSuccess = {
                navController.navigate("signIn")
            }, onSignUpNavigation = {
                navController.navigate("signIn")
            })
        }
        composable("signIn") {
            SignInScreen(auth, onSignInSuccess = {
                navController.navigate("dashboard")
            }, onSignInNavigation = {
                navController.navigate("signUp")
            })
        }
        composable("calculator") {
            trackingViewModel?.let {
                CalculatorScreen(navController, it)
            }
        }
        composable("account") {
            AccountScreen(navController, auth, onSignOut = {
                navController.navigate("signIn")
            }, onSheetNav = {
                navController.navigate("carbonEmissionForm")
            })
        }
        composable("dashboard") {
            DashboardScreen(viewModel, navController)
        }
        composable("history") {
            HistoryScreen(navController, viewModel)
        }
        composable("carbonEmissionForm") {
            CarbonEmissionForm(onComplete = {
                navController.navigate("account")
            })
        }
    }
}
