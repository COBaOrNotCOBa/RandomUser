package com.example.randomuser.presentation.userlist

import com.example.randomuser.domain.model.User
import com.example.randomuser.domain.usecase.DeleteUserUseCase
import com.example.randomuser.domain.usecase.GetUsersUseCase
import com.example.randomuser.fakes.FakeUserRepository
import com.example.randomuser.presentation.model.UiState
import com.example.randomuser.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `empty users list produces Empty state`() = runTest {
        val repository = FakeUserRepository()
        val viewModel = createViewModel(repository)

        val job = launch { viewModel.uiState.collect() }

        try {
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(state is UiState.Empty)
            assertEquals(EMPTY_MESSAGE, (state as UiState.Empty).message)
        } finally {
            job.cancel()
        }
    }

    @Test
    fun `non empty users list produces Success state`() = runTest {
        val repository = FakeUserRepository()
        repository.setUsers(listOf(createUser()))

        val viewModel = createViewModel(repository)

        val job = launch { viewModel.uiState.collect() }

        try {
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(state is UiState.Success)
            val data = (state as UiState.Success).data
            assertEquals(1, data.size)
            assertEquals(FULL_NAME, data[0].fullName)
        } finally {
            job.cancel()
        }
    }

    @Test
    fun `onDeleteUser removes user and emits Empty state`() = runTest {
        val repository = FakeUserRepository()
        repository.setUsers(listOf(createUser()))

        val viewModel = createViewModel(repository)

        val job = launch { viewModel.uiState.collect() }

        try {
            advanceUntilIdle()

            val initialState = viewModel.uiState.value
            assertTrue(initialState is UiState.Success)
            assertEquals(1, (initialState as UiState.Success).data.size)

            viewModel.onDeleteUser(USER_ID)
            advanceUntilIdle()

            val finalState = viewModel.uiState.value
            assertTrue(finalState is UiState.Empty)
            assertEquals(EMPTY_MESSAGE, (finalState as UiState.Empty).message)
        } finally {
            job.cancel()
        }
    }

    private fun createViewModel(repository: FakeUserRepository): UserListViewModel {
        val getUsersUseCase = GetUsersUseCase(repository)
        val deleteUserUseCase = DeleteUserUseCase(repository)
        return UserListViewModel(
            getUsersUseCase = getUsersUseCase,
            deleteUserUseCase = deleteUserUseCase
        )
    }

    private fun createUser(): User =
        User(
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

    private companion object {
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

        const val EMPTY_MESSAGE = "You have not created any users yet"
    }
}
