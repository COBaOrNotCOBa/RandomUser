package com.example.randomuser.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.randomuser.presentation.navigation.RandomUserNavGraph

@Composable
fun RandomUserApp() {
    MaterialTheme(
        colorScheme = lightColorScheme(),
    ) {
        val navController = rememberNavController()
        RandomUserNavGraph(navController)
    }
}
