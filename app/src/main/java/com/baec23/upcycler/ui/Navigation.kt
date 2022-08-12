package com.baec23.upcycler.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.baec23.upcycler.Screen
import com.baec23.upcycler.ui.createjob.CreateJobScreen
import com.baec23.upcycler.ui.login.LoginScreen
import com.baec23.upcycler.ui.main.MainScreen
import com.baec23.upcycler.ui.signup.SignUpScreen
import com.baec23.upcycler.ui.splash.SplashScreen


@Composable
fun Navigation(
    navHostController: NavHostController,
    appChannel: AppEventChannel
) {
    NavHost(navController = navHostController, startDestination = Screen.SplashScreen.route) {
        composable(route = Screen.SplashScreen.route) {
            SplashScreen(appChannel = appChannel)
        }
        composable(route = Screen.LoginScreen.route) {
            LoginScreen(appChannel = appChannel)
        }
        composable(route = Screen.SignUpScreen.route) {
            SignUpScreen(appChannel = appChannel)
        }
        composable(route = Screen.MainScreen.route) {
            MainScreen(appChannel = appChannel)
        }
        composable(route = Screen.CreateJobScreen.route) {
            CreateJobScreen(appChannel = appChannel)
        }
    }
}