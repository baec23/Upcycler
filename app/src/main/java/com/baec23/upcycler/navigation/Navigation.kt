package com.baec23.upcycler.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.baec23.upcycler.ui.chats.ChatsScreen
import com.baec23.upcycler.ui.createjob.CreateJobScreen
import com.baec23.upcycler.ui.jobdetails.JobDetailsScreen
import com.baec23.upcycler.ui.login.LoginScreen
import com.baec23.upcycler.ui.main.MainScreen
import com.baec23.upcycler.ui.myjobhistory.MyJobHistoryScreen
import com.baec23.upcycler.ui.signup.SignUpScreen


@Composable
fun Navigation(
    navHostController: NavHostController
) {
    NavHost(navController = navHostController, startDestination = Screen.LoginScreen.route) {
        composable(route = Screen.LoginScreen.route) {
            LoginScreen()
        }
        composable(route = Screen.SignUpScreen.route) {
            SignUpScreen()
        }
        composable(route = Screen.MainScreen.route) {
            MainScreen()
        }
        composable(route = Screen.CreateJobScreen.route) {
            CreateJobScreen()
        }
        composable(route = Screen.MyJobHistoryScreen.route) {
            MyJobHistoryScreen()
        }
        composable(route = Screen.ChatsScreen.route) {
            ChatsScreen()
        }
        composable(
            route = Screen.JobDetailsScreen.route + "/{jobId}",
            arguments = listOf(
                navArgument("jobId") {
                    type = NavType.IntType
                    nullable = false
                }
            )
        ) {
            JobDetailsScreen(jobId = it.arguments?.getInt("jobId") ?: 0)
        }
    }
}