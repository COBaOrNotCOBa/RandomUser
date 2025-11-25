package com.example.randomuser.presentation.createuser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.randomuser.domain.usecase.GetRandomUserUseCase
import com.example.randomuser.presentation.model.Gender
import com.example.randomuser.presentation.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateUserViewModel @Inject constructor(
    private val getRandomUserUseCase: GetRandomUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateUserUiState())
    val uiState: StateFlow<CreateUserUiState> = _uiState.asStateFlow()

    fun onGenderSelected(gender: Gender) {
        _uiState.update { it.copy(gender = gender) }
    }

    fun onNationalitySelected(nationality: String?) {
        _uiState.update { it.copy(nationality = nationality) }
    }

    fun generateUser(onSuccessNavigate: (String) -> Unit) {
        val currentState = _uiState.value

        viewModelScope.launch {
            _uiState.update { it.copy(requestState = UiState.Loading) }

            val result = getRandomUserUseCase(
                gender = currentState.gender,
                nationality = currentState.nationality
            )

            _uiState.update { prev ->
                result.fold(
                    onSuccess = { user ->
                        onSuccessNavigate(user.id)
                        prev.copy(requestState = UiState.Success(user))
                    },
                    onFailure = { e ->
                        prev.copy(
                            requestState = UiState.Error(
                                e.message ?: "Failed to load user"
                            )
                        )
                    }
                )
            }
        }
    }
}
