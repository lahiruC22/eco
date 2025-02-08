package com.hci.eco

import androidx.annotation.DrawableRes

data class ImageData(
    @DrawableRes val imageResId: Int,
    val title: String,
    val description: String
)
