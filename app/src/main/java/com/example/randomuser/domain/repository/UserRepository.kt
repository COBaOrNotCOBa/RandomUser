package com.example.randomuser.domain.repository

import com.example.randomuser.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun fetchAndSaveRandomUser(
        gender: String?,
        nationality: String?
    ): Result<User>

    fun getUsers(): Flow<List<User>>

    fun getUserById(id: String): Flow<User?>

    suspend fun deleteUser(id: String)
}
