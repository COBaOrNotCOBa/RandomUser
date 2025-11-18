package com.example.randomuser.domain.usecase

import com.example.randomuser.domain.model.User
import com.example.randomuser.domain.repository.UserRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetUsersUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(): Flow<List<User>> = repository.getUsers()
}
