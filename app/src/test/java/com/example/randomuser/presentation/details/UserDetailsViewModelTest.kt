package com.example.randomuser.presentation.details

import androidx.lifecycle.SavedStateHandle
import com.example.randomuser.R
import com.example.randomuser.domain.model.User
import com.example.randomuser.domain.repository.UserRepository
import com.example.randomuser.domain.usecase.GetUserByIdUseCase
import com.example.randomuser.fakes.TestData
import com.example.randomuser.presentation.common.StringProvider
import com.example.randomuser.presentation.model.UiState
import com.example.randomuser.util.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserDetailsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val userRepository: UserRepository = mockk()
    private val getUserByIdUseCase = GetUserByIdUseCase(userRepository)
    private val stringProvider: StringProvider = mockk()

    private fun createViewModel(
        userId: String,
        userFlow: Flow<User?>
    ): UserDetailsViewModel {
        every { userRepository.getUserById(userId) } returns userFlow
        val savedStateHandle = SavedStateHandle(mapOf("userId" to userId))
        return UserDetailsViewModel(
            getUserByIdUseCase = getUserByIdUseCase,
            savedStateHandle = savedStateHandle,
            stringProvider = stringProvider
        )
    }

    @Test
    fun `when user not found emits Empty state with message`() = runTest {
        val userId = "not_found"
        val message = "User not found"
        every { stringProvider.get(R.string.error_user_not_found, *anyVararg()) } returns message

        val viewModel = createViewModel(userId, flowOf(null))

        val states = mutableListOf<UiState<User>>()
        val job = launch { viewModel.uiState.toList(states) }

        advanceUntilIdle()

        assertTrue(states.first() is UiState.Loading)
        val last = states.last()
        assertTrue(last is UiState.Empty)
        assertEquals(message, (last as UiState.Empty).message)

        job.cancel()
    }

    @Test
    fun `when user exists emits Success with user`() = runTest {
        val userId = "1"
        val user = TestData.userJohn

        val viewModel = createViewModel(userId, flowOf(user))

        val states = mutableListOf<UiState<User>>()
        val job = launch { viewModel.uiState.toList(states) }

        advanceUntilIdle()

        assertTrue(states.first() is UiState.Loading)
        val last = states.last()
        assertTrue(last is UiState.Success)
        assertEquals(user, (last as UiState.Success).data)

        job.cancel()
    }

    @Test
    fun `when repository flow throws error emits Error state with message`() = runTest {
        val userId = "1"
        val errorMessage = "Error loading user"
        every { stringProvider.get(R.string.error_loading_user, *anyVararg()) } returns errorMessage

        val errorFlow: Flow<User?> = flow {
            throw RuntimeException("boom")
        }

        val viewModel = createViewModel(userId, errorFlow)

        val states = mutableListOf<UiState<User>>()
        val job = launch { viewModel.uiState.toList(states) }

        advanceUntilIdle()

        val last = states.last()
        assertTrue(last is UiState.Error)
        assertEquals(errorMessage, (last as UiState.Error).message)

        job.cancel()
    }
}
