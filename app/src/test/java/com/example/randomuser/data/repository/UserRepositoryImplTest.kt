package com.example.randomuser.data.repository

import com.example.randomuser.data.local.UserDao
import com.example.randomuser.data.local.UserEntity
import com.example.randomuser.data.remote.RandomUserApi
import com.example.randomuser.data.remote.dto.DobDto
import com.example.randomuser.data.remote.dto.IdDto
import com.example.randomuser.data.remote.dto.InfoDto
import com.example.randomuser.data.remote.dto.LocationDto
import com.example.randomuser.data.remote.dto.LoginDto
import com.example.randomuser.data.remote.dto.NameDto
import com.example.randomuser.data.remote.dto.PictureDto
import com.example.randomuser.data.remote.dto.RandomUserResponseDto
import com.example.randomuser.data.remote.dto.StreetDto
import com.example.randomuser.data.remote.dto.UserDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class UserRepositoryImplTest {

    @Test
    fun `fetchAndSaveRandomUser success returns user and inserts into dao`() = runTest {
        // given
        val api = FakeRandomUserApi()
        val dao = FakeUserDao()
        val repository = UserRepositoryImpl(api, dao)

        val dtoUser = createJohnUserDto()
        api.response = RandomUserResponseDto(
            results = listOf(dtoUser),
            info = InfoDto(
                seed = "seed",
                results = 1,
                page = 1,
                version = "1.4"
            )
        )

        // when
        val result = repository.fetchAndSaveRandomUser(
            gender = "male",
            nationality = "us"
        )

        // then
        assertTrue(result.isSuccess)
        val user = result.getOrNull()!!
        assertEquals("1", user.id)
        assertEquals("John Doe", user.fullName)
        assertEquals("US", user.nationality)

        // проверяем, что запись ушла в DAO
        assertEquals(1, dao.storedUsers.size)
        val stored = dao.storedUsers.first()
        assertEquals("1", stored.uuid)
        assertEquals("John Doe", stored.fullName)
        assertEquals("US", stored.nationality)
    }

    @Test
    fun `fetchAndSaveRandomUser with empty results returns failure`() = runTest {
        // given
        val api = FakeRandomUserApi().apply {
            response = RandomUserResponseDto(
                results = emptyList(),
                info = null
            )
        }
        val dao = FakeUserDao()
        val repository = UserRepositoryImpl(api, dao)

        // when
        val result = repository.fetchAndSaveRandomUser(
            gender = null,
            nationality = null
        )

        // then
        assertTrue(result.isFailure)
        assertTrue(
            result.exceptionOrNull()?.message?.contains("No users in response") == true
        )
        assertTrue(dao.storedUsers.isEmpty())
    }

    @Test
    fun `fetchAndSaveRandomUser http error returns server error message`() = runTest {
        // given
        val api = FakeRandomUserApi().apply {
            val errorResponse = Response.error<RandomUserResponseDto>(
                500,
                "Server error".toResponseBody("application/json".toMediaType())
            )
            throwable = HttpException(errorResponse)
        }
        val dao = FakeUserDao()
        val repository = UserRepositoryImpl(api, dao)

        // when
        val result = repository.fetchAndSaveRandomUser(
            gender = null,
            nationality = null
        )

        // then
        assertTrue(result.isFailure)
        assertEquals("Server error: 500", result.exceptionOrNull()?.message)
        assertTrue(dao.storedUsers.isEmpty())
    }

    @Test
    fun `fetchAndSaveRandomUser io error returns network error message`() = runTest {
        // given
        val api = FakeRandomUserApi().apply {
            throwable = IOException("network down")
        }
        val dao = FakeUserDao()
        val repository = UserRepositoryImpl(api, dao)

        // when
        val result = repository.fetchAndSaveRandomUser(
            gender = null,
            nationality = null
        )

        // then
        assertTrue(result.isFailure)
        assertEquals(
            "Network error. Check your connection.",
            result.exceptionOrNull()?.message
        )
        assertTrue(dao.storedUsers.isEmpty())
    }

    // --- Fakes & helpers ---

    private class FakeRandomUserApi : RandomUserApi {
        var response: RandomUserResponseDto? = null
        var throwable: Throwable? = null

        override suspend fun getRandomUser(
            gender: String?,
            nationality: String?
        ): RandomUserResponseDto {
            throwable?.let { throw it }
            return response ?: error("FakeRandomUserApi.response is not set")
        }
    }

    private class FakeUserDao : UserDao {
        private val usersInternal = mutableListOf<UserEntity>()
        val storedUsers: List<UserEntity>
            get() = usersInternal.toList()

        override fun getUsers(): Flow<List<UserEntity>> =
            flowOf(usersInternal.toList())

        override fun getUserById(id: String): Flow<UserEntity?> =
            flowOf(usersInternal.firstOrNull { it.uuid == id })

        override suspend fun insertUser(user: UserEntity) {
            usersInternal.removeAll { it.uuid == user.uuid }
            usersInternal.add(user)
        }

        override suspend fun deleteUser(id: String) {
            usersInternal.removeAll { it.uuid == id }
        }
    }

    private fun createJohnUserDto(): UserDto =
        UserDto(
            gender = "male",
            name = NameDto(
                title = null,
                first = "John",
                last = "Doe"
            ),
            location = LocationDto(
                street = StreetDto(
                    number = 1,
                    name = "1st Avenue"
                ),
                city = "New York",
                state = "NY",
                country = "United States",
                postcode = "10001"
            ),
            email = "john.doe@example.com",
            login = LoginDto(
                uuid = "1",
                username = "johndoe"
            ),
            dob = DobDto(
                date = "1995-01-01T00:00:00Z",
                age = 30
            ),
            phone = "+1 111 111",
            cell = "+1 222 222",
            id = IdDto(
                name = "SSN",
                value = "123-45-6789"
            ),
            picture = PictureDto(
                large = "https://example.com/john.jpg",
                medium = "",
                thumbnail = ""
            ),
            nationality = "US"
        )
}
