package com.example.randomuser.data.repository

import com.example.randomuser.data.local.UserDao
import com.example.randomuser.data.local.UserEntity
import com.example.randomuser.data.mapper.toDomain
import com.example.randomuser.data.mapper.toEntity
import com.example.randomuser.data.remote.RandomUserApi
import com.example.randomuser.data.remote.dto.RandomUserResponseDto
import com.example.randomuser.data.remote.dto.UserDto
import com.example.randomuser.domain.model.User
import com.example.randomuser.fakes.TestData
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class UserRepositoryImplTest {

    private lateinit var api: RandomUserApi
    private lateinit var userDao: UserDao
    private lateinit var repository: UserRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        api = mockk()
        userDao = mockk(relaxUnitFun = true)
        repository = UserRepositoryImpl(api, userDao)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `fetchAndSaveRandomUser success stores entity and returns domain user`() = runTest {
        mockkStatic("com.example.randomuser.data.mapper.UserMappersKt")

        val gender = "male"
        val nationality = "us"

        val userDto: UserDto = mockk()
        val entity: UserEntity = mockk()
        val domainUser: User = TestData.userJohn

        coEvery { api.getRandomUser(gender, nationality) } returns
                RandomUserResponseDto(results = listOf(userDto), info = null)

        every { userDto.toEntity() } returns entity
        every { entity.toDomain() } returns domainUser

        val result = repository.fetchAndSaveRandomUser(gender, nationality)

        assertTrue(result.isSuccess)
        assertEquals(domainUser, result.getOrNull())

        coVerify(exactly = 1) { api.getRandomUser(gender, nationality) }
        coVerify(exactly = 1) { userDao.insertUser(entity) }
    }

    @Test
    fun `fetchAndSaveRandomUser returns failure when response has no users`() = runTest {
        coEvery { api.getRandomUser(null, null) } returns
                RandomUserResponseDto(results = emptyList(), info = null)

        val result = repository.fetchAndSaveRandomUser(null, null)

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is IllegalStateException)
        assertEquals("No users in response", exception?.message)

        coVerify(exactly = 0) { userDao.insertUser(any()) }
    }

    @Test
    fun `fetchAndSaveRandomUser returns failure when mapper returns null entity`() = runTest {
        mockkStatic("com.example.randomuser.data.mapper.UserMappersKt")

        val userDto: UserDto = mockk()

        coEvery { api.getRandomUser(null, null) } returns
                RandomUserResponseDto(results = listOf(userDto), info = null)

        every { userDto.toEntity() } returns null

        val result = repository.fetchAndSaveRandomUser(null, null)

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is IllegalStateException)
        assertEquals("Invalid user data", exception?.message)

        coVerify(exactly = 0) { userDao.insertUser(any()) }
    }

    @Test
    fun `fetchAndSaveRandomUser wraps HttpException as server error`() = runTest {
        val body = "error".toResponseBody("text/plain".toMediaType())
        val response: Response<RandomUserResponseDto> =
            Response.error(500, body)
        val httpException = HttpException(response)

        coEvery { api.getRandomUser(any(), any()) } throws httpException

        val result = repository.fetchAndSaveRandomUser("male", "us")

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception is Exception)
        assertEquals("Server error: 500", exception?.message)
    }

    @Test
    fun `fetchAndSaveRandomUser wraps IOException as network error`() = runTest {
        coEvery { api.getRandomUser(any(), any()) } throws IOException("no internet")

        val result = repository.fetchAndSaveRandomUser("male", "us")

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertEquals("Network error. Check your connection.", exception?.message)
    }

    @Test
    fun `fetchAndSaveRandomUser wraps generic exception as unexpected error`() = runTest {
        coEvery { api.getRandomUser(any(), any()) } throws IllegalStateException("boom")

        val result = repository.fetchAndSaveRandomUser("male", "us")

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertEquals("Unexpected error: boom", exception?.message)
    }

    @Test
    fun `getUsers maps entities to domain users`() = runTest {
        mockkStatic("com.example.randomuser.data.mapper.UserMappersKt")

        val entity: UserEntity = mockk()
        val domainUser: User = TestData.userJohn

        every { entity.toDomain() } returns domainUser
        coEvery { userDao.getUsers() } returns flowOf(listOf(entity))

        val result = repository.getUsers().first()

        assertEquals(listOf(domainUser), result)
    }

    @Test
    fun `getUserById maps entity to domain user`() = runTest {
        mockkStatic("com.example.randomuser.data.mapper.UserMappersKt")

        val entity: UserEntity = mockk()
        val domainUser: User = TestData.userJohn
        val userId = "1"

        every { entity.toDomain() } returns domainUser
        coEvery { userDao.getUserById(userId) } returns flowOf(entity)

        val result = repository.getUserById(userId).first()

        assertEquals(domainUser, result)
    }

    @Test
    fun `getUserById returns null when dao returns null`() = runTest {
        val userId = "1"
        coEvery { userDao.getUserById(userId) } returns flowOf(null)

        val result = repository.getUserById(userId).first()

        assertNull(result)
    }

    @Test
    fun `deleteUser delegates to dao`() = runTest {
        val userId = "123"

        repository.deleteUser(userId)

        coVerify(exactly = 1) { userDao.deleteUser(userId) }
    }
}
