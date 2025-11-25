package com.example.randomuser.presentation.common

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface StringProvider {
    fun get(@StringRes resId: Int, vararg args: Any): String
}

class AndroidStringProvider @Inject constructor(
    @param:ApplicationContext private val context: Context
) : StringProvider {
    override fun get(@StringRes resId: Int, vararg args: Any): String =
        context.getString(resId, *args)
}
