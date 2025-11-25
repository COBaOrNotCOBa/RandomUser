package com.example.randomuser.domain.model

import androidx.annotation.StringRes
import com.example.randomuser.R

data class NationalityOption(
    val code: String?,
    @param:StringRes val labelResId: Int
)

object NationalityOptions {

    val all: List<NationalityOption> = listOf(
        NationalityOption(null, R.string.nationality_any),
        NationalityOption("au", R.string.nationality_au),
        NationalityOption("br", R.string.nationality_br),
        NationalityOption("ca", R.string.nationality_ca),
        NationalityOption("ch", R.string.nationality_ch),
        NationalityOption("de", R.string.nationality_de),
        NationalityOption("dk", R.string.nationality_dk),
        NationalityOption("es", R.string.nationality_es),
        NationalityOption("fi", R.string.nationality_fi),
        NationalityOption("fr", R.string.nationality_fr),
        NationalityOption("gb", R.string.nationality_gb),
        NationalityOption("ie", R.string.nationality_ie),
        NationalityOption("in", R.string.nationality_in),
        NationalityOption("ir", R.string.nationality_ir),
        NationalityOption("mx", R.string.nationality_mx),
        NationalityOption("nl", R.string.nationality_nl),
        NationalityOption("no", R.string.nationality_no),
        NationalityOption("nz", R.string.nationality_nz),
        NationalityOption("rs", R.string.nationality_rs),
        NationalityOption("tr", R.string.nationality_tr),
        NationalityOption("ua", R.string.nationality_ua),
        NationalityOption("us", R.string.nationality_us),
    )

    val default: NationalityOption = all.first()
}
