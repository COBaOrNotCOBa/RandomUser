package com.example.randomuser.domain.usecase

import com.example.randomuser.domain.model.User
import com.example.randomuser.domain.repository.UserRepository
import com.example.randomuser.fakes.TestData
import com.example.randomuser.presentation.model.Gender
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetRandomUserUseCaseTest {

    private val userRepository: UserRepository = mockk()
    private val useCase = GetRandomUserUseCase(userRepository)

    @Test
    fun `invoke delegates to repository with mapped gender and returns result`() = runTest {
        val nationality = "us"
        val expectedUser: User = TestData.userJohn

        coEvery {
            userRepository.fetchAndSaveRandomUser("male", nationality)
        } returns Result.success(expectedUser)

        val result = useCase(Gender.MALE, nationality)

        assertEquals(Result.success(expectedUser), result)

        coVerify(exactly = 1) {
            userRepository.fetchAndSaveRandomUser("male", nationality)
        }
    }
}
