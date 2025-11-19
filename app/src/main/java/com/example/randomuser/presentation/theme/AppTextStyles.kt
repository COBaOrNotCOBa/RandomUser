package com.example.randomuser.presentation.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Immutable
object AppTextStyles {

    val SectionTitle: TextStyle
        get() = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

    val InfoLabel: TextStyle
        get() = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

    val InfoValue: TextStyle
        get() = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        )
}
