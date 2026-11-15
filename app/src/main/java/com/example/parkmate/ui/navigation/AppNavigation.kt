package com.example.parkmate.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.parkmate.ui.view.home.HomeView
import com.example.parkmate.ui.view.location.LocationAddView
import com.example.parkmate.ui.view.location.LocationListView
import com.example.parkmate.ui.view.parking.ParkingLocationView
import com.example.parkmate.ui.view.parking.ParkingSummaryView
import com.example.parkmate.ui.view.parking.ParkingTypeView
import com.example.parkmate.ui.view.parking.ParkingVehicleView
import com.example.parkmate.ui.view.vehicle.VehicleAddView
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
                goToLocationList = { navController.navigate(LocationListRoute) },
                goToParkingLocation = { navController.navigate(ParkingLocationRoute) },
            )
        }

        // VEICOLI
        composable<VehicleListRoute> {
            VehicleListView(
                onBackButtonClick = { navController.popBackStack() },
                goToVehicleAddView = { navController.navigate(VehicleAddRoute) },
                goToVehicleDetailView = { vehicleId ->
                    navController.navigate(VehicleDetailRoute(vehicleId))
                },
            )
        }
        composable<VehicleAddRoute> {
            VehicleAddView(
                onBackButtonClick = { navController.popBackStack() },
                onSuccessfulAdd = { navController.popBackStack() },
            )
        }
        composable<VehicleDetailRoute> { backStackEntry ->
            val route: VehicleDetailRoute = backStackEntry.toRoute()
            VehicleDetailView(
                vehicleId = route.vehicleId,
                onBackButtonClick = { navController.popBackStack() },
            )
        }

        //POSIZIONI
        composable<LocationListRoute> {
            LocationListView(
                onBackButtonClick = { navController.popBackStack() },
                goToLocationAddView = { navController.navigate(LocationAddRoute) },
            )
        }
        composable<LocationAddRoute> {
            LocationAddView(
                onBackButtonClick = { navController.popBackStack() },
                onSuccessfulAdd = { navController.popBackStack() }
            )
        }

        //PARCHEGGI
        composable<ParkingLocationRoute> {
            ParkingLocationView(
                onBackButtonClick = { navController.popBackStack() },
                onLocationSelected = { navController.navigate(ParkingVehicleRoute) },
                onAddLocationClick = { navController.navigate(LocationAddRoute) },
            )
        }
        composable<ParkingVehicleRoute> {
            ParkingVehicleView(
                onBackButtonClick = { navController.popBackStack() },
                onVehicleSelected = { navController.navigate(ParkingTypeRoute) },
                onAddVehicleClick = { navController.navigate(VehicleAddRoute) },
            )
        }
        composable<ParkingTypeRoute> {
            ParkingTypeView(
                onBackButtonClick = { navController.popBackStack() },
                onNextButtonClick = { navController.navigate(ParkingSummaryRoute) },
            )
        }
        composable<ParkingSummaryRoute> {
            ParkingSummaryView(
                onBackButtonClick = { navController.popBackStack() },
                onNextButtonClick = {
                    navController.navigate(HomeRoute) {
                        popUpTo(HomeRoute) { inclusive = true }
                    }
                },
            )
        }
    }
}