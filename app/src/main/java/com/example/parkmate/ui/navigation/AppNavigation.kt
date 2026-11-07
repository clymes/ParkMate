package com.example.parkmate.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.parkmate.ui.view.home.HomeView
import com.example.parkmate.ui.view.vehicle.VehicleListView

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = HomeRoute
    ) {
        composable<HomeRoute> {
            HomeView(
                goToVehicleList = { navController.navigate(VehicleListRoute) }
            )
        }
        composable<VehicleListRoute> {
            VehicleListView()
        }
    }
}