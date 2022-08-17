package com.baec23.upcycler.util

sealed class Screen(var route: String) {
    object SplashScreen : Screen("splash_screen")
    object LoginScreen : Screen("login_screen")
    object SignUpScreen : Screen("signup_screen")
    object MainScreen : Screen("main_screen")
    object CreateJobScreen : Screen("createjob_screen")
    object JobDetailsScreen : Screen("jobdetails_screen")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}
