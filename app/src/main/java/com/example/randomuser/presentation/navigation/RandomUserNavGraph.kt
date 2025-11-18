package com.example.randomuser.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.randomuser.presentation.details.UserDetailsScreen
import com.example.randomuser.presentation.createuser.CreateUserScreen
import com.example.randomuser.presentation.userlist.UserListScreen

@Composable
fun RandomUserNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.UserList.route
    ) {
        composable(Screen.UserList.route) {
            UserListScreen(
                onCreateUserClick = {
                    navController.navigate(Screen.CreateUser.route)
                },
                onUserClick = { userId ->
                    navController.navigate(Screen.UserDetails.createRoute(userId))
                },
                onRequestCreateFirstUser = {
                    navController.navigate(Screen.CreateUser.route)
                }
            )
        }

        composable(Screen.CreateUser.route) {
            CreateUserScreen(
                onBackClick = { navController.popBackStack() },
                onUserCreated = { userId ->
                    navController.navigate(Screen.UserDetails.createRoute(userId)) {
                    }
                }
            )
        }

        composable(
            route = Screen.UserDetails.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            UserDetailsScreen(
                userId = userId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
