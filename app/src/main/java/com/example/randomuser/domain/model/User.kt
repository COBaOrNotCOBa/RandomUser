package com.example.randomuser.domain.model

data class User(
    val id: String,
    val fullName: String,
    val gender: String?,
    val email: String?,
    val phone: String?,
    val cell: String?,
    val age: Int?,
    val date: String?,
    val country: String?,
    val city: String?,
    val street: String?,
    val pictureUrl: String?,
    val nat: String?
)
