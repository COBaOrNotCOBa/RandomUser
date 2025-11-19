package com.example.randomuser.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.randomuser.presentation.navigation.RandomUserNavGraph

@Composable
fun RandomUserAppRoot() {
    val navController = rememberNavController()
    RandomUserNavGraph(navController)
}
