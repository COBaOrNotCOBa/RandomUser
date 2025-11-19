package com.example.randomuser.data.mapper

import com.example.randomuser.data.local.UserEntity
import com.example.randomuser.data.remote.dto.UserDto
import com.example.randomuser.domain.model.User

fun UserDto.toEntity(): UserEntity? {
    val id = login?.uuid ?: return null

    val fullName = buildString {
        name?.title?.let { append(it).append(" ") }
        name?.first?.let { append(it).append(" ") }
        name?.last?.let { append(it) }
    }.trim()

    val street = listOfNotNull(
        location?.street?.name,
        location?.street?.number?.toString()
    ).joinToString(" ")

    return UserEntity(
        uuid = id,
        fullName = fullName.ifBlank { "Unknown" },
        gender = gender,
        email = email,
        phone = phone,
        cell = cell,
        age = dob?.age,
        dateOfBirth = dob?.date,
        country = location?.country,
        city = location?.city,
        street = street.ifBlank { null },
        pictureUrl = picture?.large,
        nat = nat
    )
}

fun UserEntity.toDomain(): User =
    User(
        id = uuid,
        fullName = fullName,
        gender = gender,
        email = email,
        phone = phone,
        cell = cell,
        age = age,
        dateOfBirth = dateOfBirth,
        country = country,
        city = city,
        street = street,
        pictureUrl = pictureUrl,
        nat = nat
    )
