package com.crxssed.cosmos.utils.extensions

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.palette.graphics.Palette

fun Bitmap.dominantColour(): Int {
    return Palette.from(this).generate().dominantSwatch?.rgb ?: Color.Black.toArgb()
}