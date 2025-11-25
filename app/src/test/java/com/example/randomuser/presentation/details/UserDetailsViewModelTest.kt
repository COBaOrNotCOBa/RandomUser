package com.example.randomuser.presentation.details

import androidx.lifecycle.SavedStateHandle
import com.example.randomuser.domain.usecase.GetUserByIdUseCase
import com.example.randomuser.fakes.FakeStringProvider
import com.example.randomuser.fakes.FakeUserRepository
import com.example.randomuser.fakes.TestData
import com.example.randomuser.presentation.model.UiState
import com.example.randomuser.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserDetailsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `when user exists emits Success with that user`() = runTest {
        val repository = FakeUserRepository().apply {
            setUsers(listOf(TestData.userJohn))
        }
        val getUserByIdUseCase = GetUserByIdUseCase(repository)
        val savedStateHandle = SavedStateHandle(mapOf("userId" to TestData.userJohn.id))
        val stringProvider = FakeStringProvider()

        val viewModel = UserDetailsViewModel(
            getUserByIdUseCase = getUserByIdUseCase,
            savedStateHandle = savedStateHandle,
            stringProvider = stringProvider,
        )

        val state = viewModel.uiState.first { it is UiState.Success }

        assertTrue(state is UiState.Success)
        val user = (state as UiState.Success).data
        assertEquals(TestData.userJohn.id, user.id)
        assertEquals(TestData.userJohn.fullName, user.fullName)
    }

    @Test
    fun `when user not found emits Empty with proper message`() = runTest {
        val repository = FakeUserRepository().apply {
            setUsers(emptyList())
        }
        val getUserByIdUseCase = GetUserByIdUseCase(repository)
        val savedStateHandle = SavedStateHandle(mapOf("userId" to "unknown-id"))
        val stringProvider = FakeStringProvider()

        val viewModel = UserDetailsViewModel(
            getUserByIdUseCase = getUserByIdUseCase,
            savedStateHandle = savedStateHandle,
            stringProvider = stringProvider,
        )

        val state = viewModel.uiState.first { it !is UiState.Loading }

        assertTrue(state is UiState.Empty)
        assertEquals("User not found", (state as UiState.Empty).message)
    }
}
