package com.example.randomuser.presentation.userlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.randomuser.domain.model.User
import com.example.randomuser.domain.usecase.GetUsersUseCase
import com.example.randomuser.presentation.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class UserListViewModel @Inject constructor(
    getUsersUseCase: GetUsersUseCase
) : ViewModel() {

    val uiState: StateFlow<UiState<List<User>>> = getUsersUseCase()
        .map { users ->
            if (users.isEmpty()) {
                UiState.Empty("You have not created any users yet")
            } else {
                UiState.Success(users)
            }
        }
        .catch { emit(UiState.Error(it.message ?: "Error loading users")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = UiState.Loading
        )
}

fun natToFlagEmoji(nat: String?): String {
    return when (nat?.uppercase()) {
        "US" -> "🇺🇸"
        "GB" -> "🇬🇧"
        "DE" -> "🇩🇪"
        "FR" -> "🇫🇷"
        "AU" -> "🇦🇺"
        "BR" -> "🇧🇷"
        "CA" -> "🇨🇦"
        "ES" -> "🇪🇸"
        "FI" -> "🇫🇮"
        "IN" -> "🇮🇳"
        "MX" -> "🇲🇽"
        "UA" -> "🇺🇦"
        "TR" -> "🇹🇷"
        else -> "🏳️"
    }
}

