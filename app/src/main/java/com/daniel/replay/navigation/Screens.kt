package com.daniel.replay.navigation

sealed class Screens(val route: String) {
    object Splash : Screens("splash_screen")
    object Home : Screens("home_screen")
    object Settings : Screens("settings_screen")
}