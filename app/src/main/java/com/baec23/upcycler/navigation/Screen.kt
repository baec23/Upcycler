package com.baec23.upcycler.navigation

sealed class Screen(val route: String, val displayName: String, val hasCustomTopBar: Boolean = false) {
    object LoginScreen : Screen("login_screen", "Login", true)
    object SignUpScreen : Screen("signup_screen", "Sign Up", true)
    object MainScreen : Screen("main_screen", "Upcycler")
    object CreateJobScreen : Screen("createjob_screen", "Create Job")
    object JobDetailsScreen : Screen("jobdetails_screen", "Job Details", true)
    object MyJobHistoryScreen : Screen("myjobhistory_screen", "My Jobs")
    object ChatListScreen : Screen("chatlist_screen", "Chats")
    object ChatScreen : Screen("chat_screen", "Chat", true)

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}
