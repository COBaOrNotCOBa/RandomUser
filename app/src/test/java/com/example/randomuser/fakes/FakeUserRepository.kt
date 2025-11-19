package com.example.randomuser.fakes

import com.example.randomuser.domain.model.User
import com.example.randomuser.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeUserRepository : UserRepository {

    private val usersFlow = MutableStateFlow<List<User>>(emptyList())

    fun setUsers(users: List<User>) {
        usersFlow.value = users
    }

    override suspend fun fetchAndSaveRandomUser(
        gender: String?,
        nationality: String?
    ): Result<User> {
        val user = User(
            id = DEFAULT_ID,
            fullName = DEFAULT_FULL_NAME,
            gender = gender,
            email = DEFAULT_EMAIL,
            phone = null,
            cell = null,
            age = DEFAULT_AGE,
            dateOfBirth = DEFAULT_DATE,
            country = DEFAULT_COUNTRY,
            city = DEFAULT_CITY,
            street = DEFAULT_STREET,
            pictureUrl = null,
            nat = nationality?.uppercase()
        )

        val newList = usersFlow.value + user
        usersFlow.value = newList

        return Result.success(user)
    }

    override fun getUsers(): Flow<List<User>> = usersFlow

    override fun getUserById(id: String): Flow<User?> =
        usersFlow.map { list -> list.find { it.id == id } }

    override suspend fun deleteUser(id: String) {
        usersFlow.value = usersFlow.value.filterNot { it.id == id }
    }

    private companion object {
        const val DEFAULT_ID = "test-id"
        const val DEFAULT_FULL_NAME = "Test User"
        const val DEFAULT_EMAIL = "test@example.com"
        const val DEFAULT_AGE = 30
        const val DEFAULT_DATE = "2000-01-01T00:00:00Z"
        const val DEFAULT_COUNTRY = "Testland"
        const val DEFAULT_CITY = "Test City"
        const val DEFAULT_STREET = "Test Street 1"
    }
}
