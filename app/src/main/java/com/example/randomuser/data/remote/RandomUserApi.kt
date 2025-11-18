package com.example.randomuser.data.remote

import com.example.randomuser.data.remote.dto.RandomUserResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface RandomUserApi {

    @GET("api/")
    suspend fun getRandomUser(
        @Query("gender") gender: String?,
        @Query("nat") nationality: String?
    ): RandomUserResponseDto
}
