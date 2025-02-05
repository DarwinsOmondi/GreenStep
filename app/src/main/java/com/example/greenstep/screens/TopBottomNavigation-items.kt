package com.example.greenstep.screens

import androidx.compose.foundation.background
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.example.greenstep.R

sealed class TopBottomNavigational(val title:String, val route:String) {

    sealed class BottomNavigationItems( bTitle:String, bRoute:String,val icon:Int): TopBottomNavigational(bTitle, bRoute) {
        object Dashboard : BottomNavigationItems("Dashboard", "dashboard", R.drawable.baseline_dashboard_24)
        object History : BottomNavigationItems("History", "history", R.drawable.baseline_history_24)
        object Calculator : BottomNavigationItems("Calculator", "calculator", R.drawable.baseline_calculate_24)
        object Account:BottomNavigationItems("Account","account",R.drawable.baseline_account_circle_24)
    }
}


val bottomItemsList = listOf(
    TopBottomNavigational.BottomNavigationItems.Dashboard,
    TopBottomNavigational.BottomNavigationItems.History,
    TopBottomNavigational.BottomNavigationItems.Calculator,
    TopBottomNavigational.BottomNavigationItems.Account,
)

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar(
        modifier = Modifier.background(Color(245, 245, 220)) // Beige background for the navigation bar
    ) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        bottomItemsList.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                label = {
                    Text(
                        text = screen.title,
                        color = if (currentRoute == screen.route) Color(34, 139, 34) else Color.White
                    )
                },
                icon = {
                    Icon(
                        painter = painterResource(screen.icon),
                        contentDescription = screen.title,
                        tint = if (currentRoute == screen.route) Color(34, 139, 34) else Color.White
                    )
                }
            )
        }
    }
}
