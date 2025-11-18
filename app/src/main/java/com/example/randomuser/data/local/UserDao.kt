package com.example.randomuser.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM users ORDER BY rowid DESC")
    fun getUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE uuid = :uuid LIMIT 1")
    fun getUserById(uuid: String): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
}
