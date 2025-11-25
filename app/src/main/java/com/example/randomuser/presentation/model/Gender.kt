package com.example.randomuser.presentation.model

import androidx.annotation.StringRes
import com.example.randomuser.R

enum class Gender(
    @param:StringRes val labelResId: Int,
    val apiValue: String?
) {
    ANY(R.string.gender_any, null),
    MALE(R.string.gender_male, "male"),
    FEMALE(R.string.gender_female, "female");
}
