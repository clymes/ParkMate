package com.example.parkmate.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.parkmate.ui.view.home.HomeView
import com.example.parkmate.ui.view.vehicle.AddVehicleView
import com.example.parkmate.ui.view.vehicle.VehicleDetailView
import com.example.parkmate.ui.view.vehicle.VehicleListView

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300),
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300),
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300),
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300),
            )
        },
    ) {
        composable<HomeRoute> {
            HomeView(
                goToVehicleList = { navController.navigate(VehicleListRoute) },
            )
        }
        composable<VehicleListRoute> {
            VehicleListView(
                onBackButtonClick = { navController.popBackStack() },
                goToAddVehicleView = { navController.navigate(AddVehicleRoute) },
                goToVehicleDetailView = { vehicleId ->
                    navController.navigate(VehicleDetailRoute(vehicleId))
                },
            )
        }
        composable<AddVehicleRoute> {
            AddVehicleView(
                onBackButtonClick = { navController.popBackStack() },
            )
        }
        composable<VehicleDetailRoute> { backStackEntry ->
            val route: VehicleDetailRoute = backStackEntry.toRoute()
            VehicleDetailView(
                vehicleId = route.vehicleId,
                onBackButtonClick = { navController.popBackStack() },
            )
        }
    }
}