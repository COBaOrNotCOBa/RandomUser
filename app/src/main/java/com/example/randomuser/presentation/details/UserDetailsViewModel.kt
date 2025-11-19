package com.example.randomuser.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.randomuser.domain.model.User
import com.example.randomuser.domain.usecase.GetUserByIdUseCase
import com.example.randomuser.presentation.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class UserDetailsViewModel @Inject constructor(
    getUserByIdUseCase: GetUserByIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userId: String = checkNotNull(savedStateHandle["userId"])

    val uiState: StateFlow<UiState<User>> =
        getUserByIdUseCase(userId)
            .map { user ->
                if (user == null) {
                    UiState.Empty("User not found")
                } else {
                    UiState.Success(user)
                }
            }
            .catch { emit(UiState.Error(it.message ?: "Error loading user")) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = UiState.Loading
            )
}
