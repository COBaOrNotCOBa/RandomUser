package com.example.randomuser.di

import com.example.randomuser.data.local.UserDao
import com.example.randomuser.data.remote.RandomUserApi
import com.example.randomuser.data.repository.UserRepositoryImpl
import com.example.randomuser.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(
        api: RandomUserApi,
        userDao: UserDao
    ): UserRepository = UserRepositoryImpl(api, userDao)
}
