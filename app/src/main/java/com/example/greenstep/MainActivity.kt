package com.example.greenstep

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.greenstep.screens.AccountScreen
import com.example.greenstep.screens.CalculatorScreen
import com.example.greenstep.screens.CarbonEmissionForm
import com.example.greenstep.screens.DashboardScreen
import com.example.greenstep.screens.HistoryScreen
import com.example.greenstep.screens.SignInScreen
import com.example.greenstep.screens.SignUpScreen
import com.example.greenstep.screens.SplashScreen
import com.example.greenstep.ui.theme.GreenStepTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GreenStepTheme {
                val navController = rememberNavController()
                val auth:FirebaseAuth = FirebaseAuth.getInstance()
                val viewModel = CarbonInterfaceViewModel()
                GreenStep(
                    auth,
                    navController,
                    viewModel
                )
            }
        }
    }
}

@Composable
fun GreenStep(auth: FirebaseAuth,navController: NavHostController,viewModel: CarbonInterfaceViewModel){
    val startDestination = if (auth.currentUser != null) {
        "dashboard"
    }else{
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
            SignUpScreen(
                auth,
                onSignUpSuccess ={
                    navController.navigate("signIn")
                },onSignUpNavigation ={
                    navController.navigate("signIn")
                }
            )
        }
        composable("signIn") {
            SignInScreen(auth,
                onSignInSuccess ={
                navController.navigate("dashboard")
            },onSignInNavigation ={
                navController.navigate("signUp")
            })
        }
        composable("calculator"){
            CalculatorScreen(navController)
        }
        composable("account"){
            AccountScreen(
                navController,
                auth,
                onSignOut = {
                    navController.navigate("signIn")
                },
                onSheetNav = {
                    navController.navigate("carbonEmissionForm")
                }
            )
        }
        composable("dashboard"){
            DashboardScreen(viewModel, navController)
        }
        composable("history"){
            HistoryScreen(navController, viewModel)
        }
        composable("carbonEmissionForm"){
            CarbonEmissionForm(onComplete = {
                navController.navigate("account")
            })
        }
    }
}
