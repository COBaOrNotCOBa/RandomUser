package com.example.randomuser.presentation.preview

import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "Phone",
    showBackground = true,
    device = Devices.PIXEL_4
)
@Preview(
    name = "Foldable",
    showBackground = true,
    device = Devices.FOLDABLE
)
@Preview(
    name = "Tablet",
    showBackground = true,
    device = Devices.PIXEL_C
)
annotation class DevicePreviews
