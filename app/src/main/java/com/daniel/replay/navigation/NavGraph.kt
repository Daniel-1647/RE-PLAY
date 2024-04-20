package com.daniel.replay.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.daniel.replay.ui.screens.HomeScreen

@Composable
fun SetupNavGraph(navController: NavHostController, startDestination: String) {
    NavHost(navController = navController, startDestination = startDestination){
        composable(route = Screens.Home.route){
            HomeScreen(navController = navController)
        }
    }
}