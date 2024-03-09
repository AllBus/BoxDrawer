package com.kos.boxdrawe.themes

import androidx.compose.ui.graphics.Color

object ThemeColors {

    val inputBorder = Color(0xFF204020)
    val inputBackground = Color(0xFFFFffff)
    val displayLabelColor = Color(0x8FFFffff)
    fun inputBackgroundState(enabled: Boolean) = if (enabled) Color(0xFFFFffff) else Color(0xFFC0C0C0)
}