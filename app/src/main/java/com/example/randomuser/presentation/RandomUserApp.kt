package com.example.randomuser.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.randomuser.presentation.navigation.RandomUserNavGraph
import com.example.randomuser.presentation.theme.RandomUserTheme

@Composable
fun RandomUserApp() {
    RandomUserTheme {
        val navController = rememberNavController()
        RandomUserNavGraph(navController)
    }
}
