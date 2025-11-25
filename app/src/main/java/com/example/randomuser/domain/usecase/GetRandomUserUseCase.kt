package com.example.randomuser.domain.usecase

import com.example.randomuser.domain.model.User
import com.example.randomuser.domain.repository.UserRepository
import com.example.randomuser.presentation.model.Gender
import javax.inject.Inject

class GetRandomUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        gender: Gender,
        nationality: String?
    ): Result<User> {
        return userRepository.fetchAndSaveRandomUser(
            gender = gender.apiValue,
            nationality = nationality
        )
    }
}
