package com.example.randomuser.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val uuid: String,
    val fullName: String,
    val gender: String?,
    val email: String?,
    val phone: String?,
    val cell: String?,
    val age: Int?,
    val dateOfBirth: String?,
    val country: String?,
    val city: String?,
    val street: String?,
    val pictureUrl: String?,
    val nat: String?
)
