package com.example.randomuser.presentation.createuser

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.randomuser.domain.model.User
import com.example.randomuser.domain.usecase.GetRandomUserUseCase
import com.example.randomuser.presentation.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class CreateUserViewModel @Inject constructor(
    private val getRandomUserUseCase: GetRandomUserUseCase
) : ViewModel() {

    var uiState by mutableStateOf<UiState<User>>(UiState.Idle)
        private set

    var selectedGender by mutableStateOf<String?>(null)
        private set

    var selectedNat by mutableStateOf<String?>(null)
        private set

    fun onGenderSelected(gender: String?) {
        selectedGender = gender
    }

    fun onNatSelected(nat: String?) {
        selectedNat = nat
    }

    fun generateUser(onSuccessNavigate: (String) -> Unit) {
        viewModelScope.launch {
            uiState = UiState.Loading
            val result = getRandomUserUseCase(selectedGender, selectedNat)

            uiState = result.fold(
                onSuccess = { user ->
                    onSuccessNavigate(user.id)
                    UiState.Success(user)
                },
                onFailure = { e ->
                    UiState.Error(e.message ?: "Failed to load user")
                }
            )
        }
    }
}
