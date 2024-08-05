package com.kos.boxdrawe.themes

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object ThemeColors {

    val inputBorder = Color(0xFF204020)
    val inputShape = RoundedCornerShape(8.dp)
    val inputBackground = Color(0xFFFFffff)
    val displayLabelColor = Color(0x8FFFffff)
    val controllerBackground = Color(0x40909090)
    fun inputBackgroundState(enabled: Boolean) = if (enabled) Color(0xFFFFffff) else Color(0xFFC0C0C0)
    val templateFormBorder = Color(0x80203020)
    val templateArgumentColor = Color(0xFFAA3020)
    val tabBackground = Color.LightGray
    val figureListBorder = Color(0xff49463A)
    val figureListBackground = Color(0x90DDD7C3)
    val figureListTextColor =  Color(0xFFFFFFFF)
    val figureListTransformColor = Color(0xFFFFDD66)
    val selectedFigureColor = Color(0xff6BFF60)
    val figureListItemShape = RoundedCornerShape(4.dp)
    val editorBackground = Color(0x30FFFFFF)

    val bezierPointTangent = Color(0xAA00B563)
    val bezierPoint = Color(0xAA808080)
    val bezierSelectedPoint = Color(0xAA6EFF00)
    val bezierPointSelect = Color(0xAA00C609)
    val bezierPointButton = Color(0xAAFFD800)

    val NumericFieldHeight = 30.dp
}