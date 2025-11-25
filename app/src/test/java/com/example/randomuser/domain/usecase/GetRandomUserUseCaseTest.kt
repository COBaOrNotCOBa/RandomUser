package com.example.randomuser.domain.usecase

import com.example.randomuser.domain.model.User
import com.example.randomuser.domain.repository.UserRepository
import com.example.randomuser.fakes.TestData
import com.example.randomuser.presentation.model.Gender
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class GetRandomUserUseCaseTest {

    @Test
    fun `maps Gender_MALE to api value and passes nationality`() = runTest {
        val fakeRepository = CapturingUserRepository()
        fakeRepository.fetchResult = Result.success(TestData.userJohn)

        val useCase = GetRandomUserUseCase(fakeRepository)

        val result = useCase(
            gender = Gender.MALE,
            nationality = "us"
        )

        Assert.assertEquals(TestData.userJohn, result.getOrNull())

        Assert.assertEquals("male", fakeRepository.lastGender)
        Assert.assertEquals("us", fakeRepository.lastNationality)
    }

    @Test
    fun `maps Gender_ANY to null gender for api`() = runTest {
        val fakeRepository = CapturingUserRepository()
        fakeRepository.fetchResult = Result.success(TestData.userJohn)

        val useCase = GetRandomUserUseCase(fakeRepository)

        useCase(
            gender = Gender.ANY,
            nationality = "de"
        )

        Assert.assertNull(fakeRepository.lastGender)
        Assert.assertEquals("de", fakeRepository.lastNationality)
    }


    private class CapturingUserRepository : UserRepository {

        var fetchResult: Result<User> = Result.failure(IllegalStateException("not set"))

        var lastGender: String? = null
            private set

        var lastNationality: String? = null
            private set

        override suspend fun fetchAndSaveRandomUser(
            gender: String?,
            nationality: String?
        ): Result<User> {
            lastGender = gender
            lastNationality = nationality
            return fetchResult
        }

        override fun getUsers() = throw NotImplementedError()

        override fun getUserById(id: String) = throw NotImplementedError()

        override suspend fun deleteUser(id: String) = Unit
    }
}
