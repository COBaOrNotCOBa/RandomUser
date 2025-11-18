package com.example.randomuser.data.remote.dto

data class RandomUserResponseDto(
    val results: List<UserDto>?,
    val info: InfoDto?
)

data class InfoDto(
    val seed: String?,
    val results: Int?,
    val page: Int?,
    val version: String?
)
