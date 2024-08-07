package com.kos.boxdrawer.presentation.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp

object BoxTypography {
    val DefaultLineHeightStyle = LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Center,
        trim = LineHeightStyle.Trim.None,
    )

    val DefaultTextStyle = TextStyle.Default.copy(
        platformStyle = null,
        lineHeightStyle = DefaultLineHeightStyle,
    )

    val typography = Typography(
//    defaultFontFamily = FontFamily.Default,
//        body1 = DefaultTextStyle.copy(
//            fontWeight = FontWeight.Normal,
//            fontSize = 14.sp,
//            lineHeight = 17.sp,
//            letterSpacing = 0.5.sp
//    ),
//    body2 = DefaultTextStyle.copy(
//        fontWeight = FontWeight.Normal,
//        fontSize = 10.sp,
//        lineHeight = 12.sp,
//        letterSpacing = 0.25.sp
//    ),
//    button = DefaultTextStyle.copy(
//        fontWeight = FontWeight.Medium,
//        fontSize = 14.sp,
//        lineHeight = 14.sp,
//        letterSpacing = 1.25.sp
//    ),
//        caption = DefaultTextStyle.copy(
//            fontWeight = FontWeight.Normal,
//        fontSize = 12.sp,
//        lineHeight = 14.sp,
//        letterSpacing = 0.4.sp
//    ),
//    overline = DefaultTextStyle.copy(
//        fontWeight = FontWeight.Normal,
//        fontSize = 10.sp,
//        lineHeight = 10.sp,
//        letterSpacing = 1.5.sp
//    )

    )

}