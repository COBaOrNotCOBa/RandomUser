package com.example.randomuser.fakes

import com.example.randomuser.domain.model.User

object TestData {
    val userJohn = User(
        id = "1",
        fullName = "John Doe",
        gender = "male",
        email = "john.doe@example.com",
        phone = "+1 111 111",
        cell = "+1 222 222",
        age = 30,
        dateOfBirth = "1995-01-01T00:00:00Z",
        country = "United States",
        city = "New York",
        street = "1st Avenue",
        pictureUrl = "https://example.com/john.jpg",
        nationality = "US",
    )
}
