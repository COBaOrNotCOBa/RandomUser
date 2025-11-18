package com.example.randomuser.presentation.navigation

sealed class Screen(val route: String) {
    object UserList : Screen("user_list")

    object CreateUser : Screen("create_user")

    object UserDetails : Screen("user_details/{userId}") {
        fun createRoute(userId: String) = "user_details/$userId"
    }
}
