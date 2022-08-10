package com.baec23.upcycler

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.baec23.upcycler.ui.login.LoginScreen
import com.baec23.upcycler.ui.main.MainScreen
import com.baec23.upcycler.ui.signup.SignUpScreen
import com.baec23.upcycler.ui.theme.UpcyclerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            UpcyclerTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "login_screen"
                ) {
                    composable("login_screen") {
                        LoginScreen(
                            navController = navController
                        )
                    }
                    composable("signup_screen"){
                        SignUpScreen(navController = navController)
                    }
                    composable("main_screen") {
                        MainScreen()
                    }
                }
            }
        }

    }
}


