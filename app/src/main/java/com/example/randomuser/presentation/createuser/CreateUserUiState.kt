package com.example.randomuser.presentation.createuser

import com.example.randomuser.domain.model.User
import com.example.randomuser.presentation.model.Gender
import com.example.randomuser.presentation.model.UiState

data class CreateUserUiState(
    val requestState: UiState<User> = UiState.Idle,
    val gender: Gender = Gender.ANY,
    val nationality: String? = null,
)
