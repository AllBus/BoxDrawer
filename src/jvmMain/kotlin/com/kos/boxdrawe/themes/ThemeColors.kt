package com.kos.boxdrawe.themes

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object ThemeColors {

    val inputBorder = Color(0xFF204020)
    val inputBackground = Color(0xFFFFffff)
    val displayLabelColor = Color(0x8FFFffff)
    fun inputBackgroundState(enabled: Boolean) = if (enabled) Color(0xFFFFffff) else Color(0xFFC0C0C0)
    val templateFormBorder = Color(0xFF203020)
    val templateArgumentColor = Color(0xFFAA3020)
    val tabBackground = Color.LightGray
    val figureListBorder = Color(0xff49463A)
    val figureListBackground = Color(0x90DDD7C3)
    val selectedFigureColor = Color(0xff6BFF60)
    val figureListItemShape = RoundedCornerShape(4.dp)


}