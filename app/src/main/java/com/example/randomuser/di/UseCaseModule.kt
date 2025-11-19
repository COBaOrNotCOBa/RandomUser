package com.example.randomuser.di

import com.example.randomuser.domain.repository.UserRepository
import com.example.randomuser.domain.usecase.DeleteUserUseCase
import com.example.randomuser.domain.usecase.GetRandomUserUseCase
import com.example.randomuser.domain.usecase.GetUserByIdUseCase
import com.example.randomuser.domain.usecase.GetUsersUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideGetRandomUserUseCase(repository: UserRepository) =
        GetRandomUserUseCase(repository)

    @Provides
    fun provideGetUsersUseCase(repository: UserRepository) =
        GetUsersUseCase(repository)

    @Provides
    fun provideGetUserByIdUseCase(repository: UserRepository) =
        GetUserByIdUseCase(repository)

    @Provides
    fun provideDeleteUserUseCase(repository: UserRepository): DeleteUserUseCase =
        DeleteUserUseCase(repository)
}
