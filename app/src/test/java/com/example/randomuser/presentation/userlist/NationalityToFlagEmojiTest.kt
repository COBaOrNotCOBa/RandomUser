package com.example.randomuser.presentation.userlist

import org.junit.Assert.assertEquals
import org.junit.Test

class NationalityToFlagEmojiTest {

    @Test
    fun `returns correct flag for all supported nat codes`() {
        EXPECTATIONS.forEach { (code, flag) ->
            assertEquals(flag, nationalityToFlagEmoji(code))
            assertEquals(flag, nationalityToFlagEmoji(code.lowercase()))
        }
    }

    @Test
    fun `returns default flag for unknown or null nat`() {
        assertEquals(DEFAULT_FLAG, nationalityToFlagEmoji(null))
        assertEquals(DEFAULT_FLAG, nationalityToFlagEmoji(""))
        assertEquals(DEFAULT_FLAG, nationalityToFlagEmoji("XX"))
    }

    private companion object {
        const val DEFAULT_FLAG = "🏳️"

        val EXPECTATIONS = mapOf(
            "AU" to "🇦🇺",
            "BR" to "🇧🇷",
            "CA" to "🇨🇦",
            "CH" to "🇨🇭",
            "DE" to "🇩🇪",
            "DK" to "🇩🇰",
            "ES" to "🇪🇸",
            "FI" to "🇫🇮",
            "FR" to "🇫🇷",
            "GB" to "🇬🇧",
            "IE" to "🇮🇪",
            "IN" to "🇮🇳",
            "IR" to "🇮🇷",
            "MX" to "🇲🇽",
            "NL" to "🇳🇱",
            "NO" to "🇳🇴",
            "NZ" to "🇳🇿",
            "RS" to "🇷🇸",
            "TR" to "🇹🇷",
            "UA" to "🇺🇦",
            "US" to "🇺🇸"
        )
    }
}
