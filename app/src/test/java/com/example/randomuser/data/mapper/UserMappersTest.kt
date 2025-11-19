package com.example.randomuser.data.mapper

import com.example.randomuser.data.local.UserEntity
import com.example.randomuser.data.remote.dto.DobDto
import com.example.randomuser.data.remote.dto.LocationDto
import com.example.randomuser.data.remote.dto.LoginDto
import com.example.randomuser.data.remote.dto.NameDto
import com.example.randomuser.data.remote.dto.StreetDto
import com.example.randomuser.data.remote.dto.UserDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class UserMappersTest {

    @Test
    fun `toEntity returns null when uuid is missing`() {
        val dto = UserDto(
            gender = FEMALE,
            name = NameDto(TITLE, FIRST_NAME, LAST_NAME),
            location = null,
            email = EMAIL,
            login = null,
            dob = null,
            phone = PHONE,
            cell = CELL,
            id = null,
            picture = null,
            nat = NAT
        )

        val entity = dto.toEntity()

        assertNull(entity)
    }

    @Test
    fun `toEntity maps fields correctly`() {
        val dto = UserDto(
            gender = FEMALE,
            name = NameDto(TITLE, FIRST_NAME, LAST_NAME),
            location = LocationDto(
                street = StreetDto(STREET_NUMBER, STREET_NAME),
                city = CITY,
                state = STATE,
                country = COUNTRY,
                postcode = POSTCODE
            ),
            email = EMAIL,
            login = LoginDto(
                uuid = USER_ID,
                username = USERNAME
            ),
            dob = DobDto(
                date = DOB,
                age = AGE
            ),
            phone = PHONE,
            cell = CELL,
            id = null,
            picture = null,
            nat = NAT
        )

        val entity = dto.toEntity()

        assertNotNull(entity)
        entity!!

        assertEquals(USER_ID, entity.uuid)
        assertEquals("$TITLE $FIRST_NAME $LAST_NAME", entity.fullName)
        assertEquals(FEMALE, entity.gender)
        assertEquals(EMAIL, entity.email)
        assertEquals(PHONE, entity.phone)
        assertEquals(CELL, entity.cell)
        assertEquals(AGE, entity.age)
        assertEquals(DOB, entity.dateOfBirth)
        assertEquals(COUNTRY, entity.country)
        assertEquals(CITY, entity.city)
        assertEquals("$STREET_NAME $STREET_NUMBER", entity.street)
        assertEquals(NAT, entity.nat)
    }

    @Test
    fun `toDomain maps entity to domain model`() {
        val entity = UserEntity(
            uuid = USER_ID,
            fullName = "$TITLE $FIRST_NAME $LAST_NAME",
            gender = FEMALE,
            email = EMAIL,
            phone = PHONE,
            cell = CELL,
            age = AGE,
            dateOfBirth = DOB,
            country = COUNTRY,
            city = CITY,
            street = "$STREET_NAME $STREET_NUMBER",
            pictureUrl = PICTURE_URL,
            nat = NAT
        )

        val user = entity.toDomain()

        assertEquals(entity.uuid, user.id)
        assertEquals(entity.fullName, user.fullName)
        assertEquals(entity.gender, user.gender)
        assertEquals(entity.email, user.email)
        assertEquals(entity.phone, user.phone)
        assertEquals(entity.cell, user.cell)
        assertEquals(entity.age, user.age)
        assertEquals(entity.dateOfBirth, user.dateOfBirth)
        assertEquals(entity.country, user.country)
        assertEquals(entity.city, user.city)
        assertEquals(entity.street, user.street)
        assertEquals(entity.pictureUrl, user.pictureUrl)
        assertEquals(entity.nat, user.nat)
    }

    private companion object {
        const val USER_ID = "uuid-123"
        const val TITLE = "Ms"
        const val FIRST_NAME = "Emma"
        const val LAST_NAME = "Smith"
        const val FEMALE = "female"
        const val EMAIL = "emma.smith@example.com"
        const val USERNAME = "emma_smith"
        const val PHONE = "+44 20 0000 0000"
        const val CELL = "+44 7700 000000"
        const val DOB = "1990-01-01T10:00:00Z"
        const val COUNTRY = "United Kingdom"
        const val CITY = "London"
        const val STATE = "London"
        const val POSTCODE = "NW1"
        const val STREET_NAME = "Baker Street"
        const val NAT = "GB"
        const val PICTURE_URL = "https://example.com/photo.jpg"
        const val STREET_NUMBER = 10
        const val AGE = 35
    }
}
