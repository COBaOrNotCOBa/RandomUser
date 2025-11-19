package com.example.randomuser.data.repository

import com.example.randomuser.data.local.UserDao
import com.example.randomuser.data.mapper.toDomain
import com.example.randomuser.data.mapper.toEntity
import com.example.randomuser.data.remote.RandomUserApi
import com.example.randomuser.domain.model.User
import com.example.randomuser.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api: RandomUserApi,
    private val userDao: UserDao
) : UserRepository {

    override suspend fun fetchAndSaveRandomUser(
        gender: String?,
        nationality: String?
    ): Result<User> {
        return try {
            val dto = api.getRandomUser(gender, nationality)

            val userDto = dto.results?.firstOrNull()
                ?: return Result.failure(IllegalStateException("No users in response"))

            val entity = userDto.toEntity()
                ?: return Result.failure(IllegalStateException("Invalid user data"))

            userDao.insertUser(entity)

            Result.success(entity.toDomain())
        } catch (e: HttpException) {
            Result.failure(Exception("Server error: ${e.code()}", e))
        } catch (e: IOException) {
            Result.failure(Exception("Network error. Check your connection.", e))
        } catch (e: Exception) {
            Result.failure(Exception("Unexpected error: ${e.message}", e))
        }
    }

    override fun getUsers(): Flow<List<User>> =
        userDao.getUsers()
            .map { list -> list.map { it.toDomain() } }

    override fun getUserById(id: String): Flow<User?> =
        userDao.getUserById(id)
            .map { it?.toDomain() }

    override suspend fun deleteUser(id: String) {
        userDao.deleteUser(id)
    }
}
