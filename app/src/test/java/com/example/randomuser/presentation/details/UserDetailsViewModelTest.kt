package com.example.randomuser.presentation.details

import androidx.lifecycle.SavedStateHandle
import com.example.randomuser.domain.model.User
import com.example.randomuser.domain.usecase.GetUserByIdUseCase
import com.example.randomuser.fakes.FakeUserRepository
import com.example.randomuser.presentation.model.UiState
import com.example.randomuser.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserDetailsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `when user not found emits Empty state`() = runTest {
        val repository = FakeUserRepository()
        val useCase = GetUserByIdUseCase(repository)
        val savedStateHandle = SavedStateHandle(mapOf(KEY_USER_ID to UNKNOWN_USER_ID))

        val viewModel = UserDetailsViewModel(
            getUserByIdUseCase = useCase,
            savedStateHandle = savedStateHandle
        )

        val job = launch { viewModel.uiState.collect() }

        try {
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(state is UiState.Empty)
        } finally {
            job.cancel()
        }
    }

    @Test
    fun `when user exists emits Success state`() = runTest {
        val repository = FakeUserRepository()
        val existingUser = User(
            id = USER_ID,
            fullName = FULL_NAME,
            gender = GENDER,
            email = EMAIL,
            phone = PHONE,
            cell = null,
            age = AGE,
            dateOfBirth = DATE,
            country = COUNTRY,
            city = CITY,
            street = STREET,
            pictureUrl = PICTURE_URL,
            nat = NAT
        )
        repository.setUsers(listOf(existingUser))

        val useCase = GetUserByIdUseCase(repository)
        val savedStateHandle = SavedStateHandle(mapOf(KEY_USER_ID to USER_ID))

        val viewModel = UserDetailsViewModel(
            getUserByIdUseCase = useCase,
            savedStateHandle = savedStateHandle
        )

        val job = launch { viewModel.uiState.collect() }

        try {
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(state is UiState.Success)
        } finally {
            job.cancel()
        }
    }

    private companion object {
        const val KEY_USER_ID = "userId"
        const val UNKNOWN_USER_ID = "unknown-id"

        const val USER_ID = "1"
        const val FULL_NAME = "John Doe"
        const val GENDER = "male"
        const val EMAIL = "john@example.com"
        const val PHONE = "+44 20 0000 0000"
        const val AGE = 30
        const val DATE = "1990-01-01T00:00:00Z"
        const val COUNTRY = "UK"
        const val CITY = "London"
        const val STREET = "221B Baker Street"
        const val PICTURE_URL = "https://example.com/photo.jpg"
        const val NAT = "GB"
    }
}
