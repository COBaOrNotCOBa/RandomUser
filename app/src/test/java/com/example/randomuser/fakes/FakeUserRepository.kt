package com.example.randomuser.fakes

import com.example.randomuser.domain.model.User
import com.example.randomuser.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeUserRepository : UserRepository {

    private val usersFlow = MutableStateFlow<List<User>>(emptyList())

    var lastFetchGender: String? = null
        private set
    var lastFetchNationality: String? = null
        private set

    override suspend fun fetchAndSaveRandomUser(
        gender: String?,
        nationality: String?
    ): Result<User> {
        lastFetchGender = gender
        lastFetchNationality = nationality
        return Result.failure(IllegalStateException("Not implemented for this test"))
    }

    override fun getUsers(): Flow<List<User>> = usersFlow

    override fun getUserById(id: String): Flow<User?> =
        usersFlow.map { list -> list.firstOrNull { it.id == id } }

    override suspend fun deleteUser(id: String) {
        usersFlow.value = usersFlow.value.filterNot { it.id == id }
    }

    fun setUsers(users: List<User>) {
        usersFlow.value = users
    }
}
