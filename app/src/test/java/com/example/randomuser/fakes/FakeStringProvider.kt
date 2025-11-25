package com.example.randomuser.fakes

import androidx.annotation.StringRes
import com.example.randomuser.R
import com.example.randomuser.presentation.common.StringProvider

class FakeStringProvider : StringProvider {

    override fun get(@StringRes resId: Int, vararg args: Any): String {
        return when (resId) {
            R.string.list_empty_message -> "You have not created any users yet"
            R.string.error_loading_users -> "Error loading users"
            R.string.error_user_not_found -> "User not found"
            R.string.error_loading_user -> "Error loading user"
            else -> "string-$resId"
        }
    }
}
