package com.baec23.upcycler.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.baec23.upcycler.ui.chats.ChatListScreen
import com.baec23.upcycler.ui.chats.ChatScreen
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
        composable(route = Screen.ChatListScreen.route) {
            ChatListScreen()
        }
        composable(route = Screen.ChatScreen.route + "/{chatSessionId}",
            arguments = listOf(
                navArgument("chatSessionId") {
                    type = NavType.LongType
                    nullable = false
                }
            )) {
            ChatScreen()
        }
        composable(
            route = Screen.JobDetailsScreen.route + "/{jobId}",
            arguments = listOf(
                navArgument("jobId") {
                    type = NavType.LongType
                    nullable = false
                }
            )
        ) {
            JobDetailsScreen(jobId = it.arguments?.getLong("jobId") ?: 0)
        }
    }
}