package com.example.randomuser.presentation.createuser

import com.example.randomuser.domain.usecase.GetRandomUserUseCase
import com.example.randomuser.fakes.TestData
import com.example.randomuser.fakes.FakeUserRepository
import com.example.randomuser.presentation.model.UiState
import com.example.randomuser.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CreateUserViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeRepository = FakeUserRepository()
    private val getRandomUserUseCase = GetRandomUserUseCase(fakeRepository)

    @Test
    fun `initial state is Idle with no selection`() = runTest {
        val viewModel = CreateUserViewModel(getRandomUserUseCase)

        val state = viewModel.uiState.value
        assertTrue(state.requestState is UiState.Idle)
        assertNull(state.gender)
        assertNull(state.nationality)
    }

    @Test
    fun `generateUser success updates state to Success and calls callback with id`() = runTest {
        val viewModel = CreateUserViewModel(getRandomUserUseCase)
        val expectedUser = TestData.userJohn
        fakeRepository.fetchRandomUserResult = Result.success(expectedUser)

        var navigatedUserId: String? = null

        viewModel.generateUser { id ->
            navigatedUserId = id
        }
        advanceUntilIdle()

        val state = viewModel.uiState.value
        val requestState = state.requestState

        assertTrue(requestState is UiState.Success)
        assertEquals(expectedUser, (requestState as UiState.Success).data)
        assertEquals(expectedUser.id, navigatedUserId)
    }

    @Test
    fun `generateUser failure updates state to Error`() = runTest {
        val viewModel = CreateUserViewModel(getRandomUserUseCase)
        val throwable = Exception("Boom")
        fakeRepository.fetchRandomUserResult = Result.failure(throwable)

        viewModel.generateUser { }
        advanceUntilIdle()

        val state = viewModel.uiState.value
        val requestState = state.requestState

        assertTrue(requestState is UiState.Error)
        assertTrue((requestState as UiState.Error).message.isNotBlank())
    }
}
