package com.ges.vehiclegate.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ges.vehiclegate.ui.screen_add.AddVehicleScreen
import com.ges.vehiclegate.ui.screen_home.HomeScreen
import com.ges.vehiclegate.ui.screen_today.TodayScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onAddVehicle = { navController.navigate(Routes.ADD) },
                onSeeToday = { navController.navigate(Routes.TODAY) }
            )
        }

        composable(Routes.ADD) {
            AddVehicleScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.TODAY) {
            TodayScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
