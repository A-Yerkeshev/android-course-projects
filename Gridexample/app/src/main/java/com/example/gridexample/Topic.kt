package com.example.gridexample

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Topic(
    @StringRes val stringResId: Int,
    val paricipants: Int,
    @DrawableRes val iconResId: Int
)