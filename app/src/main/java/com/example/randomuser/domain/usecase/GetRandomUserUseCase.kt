package com.example.randomuser.domain.usecase

import com.example.randomuser.domain.model.User
import com.example.randomuser.domain.repository.UserRepository
import jakarta.inject.Inject

class GetRandomUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(
        gender: String?,
        nationality: String?
    ): Result<User> = repository.fetchAndSaveRandomUser(gender, nationality)
}
