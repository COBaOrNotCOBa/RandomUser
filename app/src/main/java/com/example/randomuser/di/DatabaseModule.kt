package com.example.randomuser.di

import android.content.Context
import androidx.room.Room
import com.example.randomuser.data.local.AppDatabase
import com.example.randomuser.data.local.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "random_user_db"
        ).build()

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()
}
