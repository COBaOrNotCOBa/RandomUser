package com.example.randomuser.presentation.preview

import com.example.randomuser.domain.model.User

val sampleUser = User(
    id = "123",
    fullName = "John Doe",
    gender = "male",
    email = "john.doe@example.com",
    phone = "+44 20 7946 0958",
    cell = "+44 7700 900123",
    age = 30,
    dateOfBirth = "2001-01-01",
    country = "United Kingdom",
    city = "London",
    street = "221B Baker Street",
    pictureUrl = "https://randomuser.me/api/portraits/men/75.jpg",
    nat = "GB"
)

val sampleUsers = listOf(
    sampleUser,
    sampleUser.copy(
        id = "124",
        fullName = "Emma Smith",
        gender = "female",
        email = "emma.smith@example.com",
        nat = "US",
        country = "United States",
        city = "New York"
    )
)