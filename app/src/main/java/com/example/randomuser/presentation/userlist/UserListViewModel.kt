package com.example.randomuser.presentation.userlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.randomuser.R
import com.example.randomuser.domain.model.User
import com.example.randomuser.domain.usecase.DeleteUserUseCase
import com.example.randomuser.domain.usecase.GetUsersUseCase
import com.example.randomuser.presentation.common.StringProvider
import com.example.randomuser.presentation.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    getUsersUseCase: GetUsersUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val stringProvider: StringProvider,
) : ViewModel() {

    val uiState: StateFlow<UiState<List<User>>> = getUsersUseCase()
        .map { users ->
            if (users.isEmpty()) {
                UiState.Empty(stringProvider.get(R.string.list_empty_message))
            } else {
                UiState.Success(users)
            }
        }
        .catch {
            emit(UiState.Error(stringProvider.get(R.string.error_loading_users)))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )

    fun onDeleteUser(userId: String) {
        viewModelScope.launch {
            deleteUserUseCase(userId)
        }
    }
}

fun nationalityToFlagEmoji(nat: String?): String {
    return when (nat?.uppercase()) {
        "AU" -> "🇦🇺"
        "BR" -> "🇧🇷"
        "CA" -> "🇨🇦"
        "CH" -> "🇨🇭"
        "DE" -> "🇩🇪"
        "DK" -> "🇩🇰"
        "ES" -> "🇪🇸"
        "FI" -> "🇫🇮"
        "FR" -> "🇫🇷"
        "GB" -> "🇬🇧"
        "IE" -> "🇮🇪"
        "IN" -> "🇮🇳"
        "IR" -> "🇮🇷"
        "MX" -> "🇲🇽"
        "NL" -> "🇳🇱"
        "NO" -> "🇳🇴"
        "NZ" -> "🇳🇿"
        "RS" -> "🇷🇸"
        "TR" -> "🇹🇷"
        "UA" -> "🇺🇦"
        "US" -> "🇺🇸"
        else -> "🏳️"
    }
}
