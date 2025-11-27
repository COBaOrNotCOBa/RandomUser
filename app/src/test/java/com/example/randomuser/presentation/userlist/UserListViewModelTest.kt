package com.example.randomuser.presentation.userlist

import com.example.randomuser.R
import com.example.randomuser.domain.model.User
import com.example.randomuser.domain.repository.UserRepository
import com.example.randomuser.domain.usecase.DeleteUserUseCase
import com.example.randomuser.domain.usecase.GetUsersUseCase
import com.example.randomuser.fakes.TestData
import com.example.randomuser.presentation.common.StringProvider
import com.example.randomuser.presentation.model.UiState
import com.example.randomuser.util.MainDispatcherRule
import io.mockk.coVerify
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

class UserListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val userRepository: UserRepository = mockk(relaxed = true)
    private val getUsersUseCase = GetUsersUseCase(userRepository)
    private val deleteUserUseCase = DeleteUserUseCase(userRepository)
    private val stringProvider: StringProvider = mockk()

    private fun createViewModel(getUsersFlow: Flow<List<User>>): UserListViewModel {
        every { userRepository.getUsers() } returns getUsersFlow
        return UserListViewModel(
            getUsersUseCase = getUsersUseCase,
            deleteUserUseCase = deleteUserUseCase,
            stringProvider = stringProvider
        )
    }

    @Test
    fun `when users list is empty emits Empty state with proper message`() = runTest {
        val emptyMessage = "You have not created any users yet"
        every { stringProvider.get(R.string.list_empty_message, *anyVararg()) } returns emptyMessage

        val viewModel = createViewModel(flowOf(emptyList()))

        val states = mutableListOf<UiState<List<User>>>()
        val job = launch { viewModel.uiState.toList(states) }

        advanceUntilIdle()

        assertTrue(states.first() is UiState.Loading)
        val last = states.last()
        assertTrue(last is UiState.Empty)
        assertEquals(emptyMessage, (last as UiState.Empty).message)

        job.cancel()
    }

    @Test
    fun `when users list is not empty emits Success with users`() = runTest {
        val user = TestData.userJohn
        val viewModel = createViewModel(flowOf(listOf(user)))

        val states = mutableListOf<UiState<List<User>>>()
        val job = launch { viewModel.uiState.toList(states) }

        advanceUntilIdle()

        assertTrue(states.first() is UiState.Loading)
        val last = states.last()
        assertTrue(last is UiState.Success)
        assertEquals(listOf(user), (last as UiState.Success).data)

        job.cancel()
    }

    @Test
    fun `when repository flow throws error emits Error state with message`() = runTest {
        val errorMessage = "Error loading users"
        every { stringProvider.get(R.string.error_loading_users, *anyVararg()) } returns errorMessage

        val errorFlow: Flow<List<User>> = flow {
            throw RuntimeException("boom")
        }

        val viewModel = createViewModel(errorFlow)

        val states = mutableListOf<UiState<List<User>>>()
        val job = launch { viewModel.uiState.toList(states) }

        advanceUntilIdle()

        val last = states.last()
        assertTrue(last is UiState.Error)
        assertEquals(errorMessage, (last as UiState.Error).message)

        job.cancel()
    }

    @Test
    fun `onDeleteUser calls DeleteUserUseCase with correct id`() = runTest {
        val viewModel = createViewModel(flowOf(emptyList()))

        val userId = "123"
        viewModel.onDeleteUser(userId)

        advanceUntilIdle()

        coVerify(exactly = 1) {
            userRepository.deleteUser(userId)
        }
    }
}
