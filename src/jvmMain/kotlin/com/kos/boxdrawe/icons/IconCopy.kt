package com.kos.boxdrawe.icons

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object IconCopy {

    @Composable
    fun rememberContentCopy(): ImageVector {
        return remember {
            ImageVector.Builder(
                name = "content_copy",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 40.0f,
                viewportHeight = 40.0f
            ).apply {
                path(
                    fill = SolidColor(Color.Black),
                    fillAlpha = 1f,
                    stroke = null,
                    strokeAlpha = 1f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(13.292f, 30.958f)
                    quadToRelative(-1.084f, 0f, -1.875f, -0.77f)
                    quadToRelative(-0.792f, -0.771f, -0.792f, -1.855f)
                    verticalLineToRelative(-22f)
                    quadToRelative(0f, -1.083f, 0.792f, -1.854f)
                    quadToRelative(0.791f, -0.771f, 1.875f, -0.771f)
                    horizontalLineToRelative(17.083f)
                    quadToRelative(1.083f, 0f, 1.854f, 0.771f)
                    quadTo(33f, 5.25f, 33f, 6.333f)
                    verticalLineToRelative(22f)
                    quadToRelative(0f, 1.084f, -0.771f, 1.855f)
                    quadToRelative(-0.771f, 0.77f, -1.854f, 0.77f)
                    close()
                    moveToRelative(0f, -2.625f)
                    horizontalLineToRelative(17.083f)
                    verticalLineToRelative(-22f)
                    horizontalLineTo(13.292f)
                    verticalLineToRelative(22f)
                    close()
                    moveTo(8f, 36.25f)
                    quadToRelative(-1.083f, 0f, -1.854f, -0.771f)
                    quadToRelative(-0.771f, -0.771f, -0.771f, -1.854f)
                    verticalLineTo(10.792f)
                    quadToRelative(0f, -0.542f, 0.375f, -0.938f)
                    quadToRelative(0.375f, -0.396f, 0.917f, -0.396f)
                    quadToRelative(0.583f, 0f, 0.958f, 0.396f)
                    reflectiveQuadToRelative(0.375f, 0.938f)
                    verticalLineToRelative(22.833f)
                    horizontalLineToRelative(17.625f)
                    quadToRelative(0.5f, 0f, 0.896f, 0.375f)
                    reflectiveQuadToRelative(0.396f, 0.917f)
                    quadToRelative(0f, 0.583f, -0.396f, 0.958f)
                    reflectiveQuadToRelative(-0.896f, 0.375f)
                    close()
                    moveToRelative(5.292f, -29.917f)
                    verticalLineToRelative(22f)
                    verticalLineToRelative(-22f)
                    close()
                }
            }.build()
        }
    }

    @Composable
    fun rememberCalculate(): ImageVector {
        return remember {
            ImageVector.Builder(
                name = "calculate",
                defaultWidth = 24.0.dp,
                defaultHeight = 24.0.dp,
                viewportWidth = 40.0f,
                viewportHeight = 40.0f
            ).apply {
                path(
                    fill = SolidColor(Color.Black),
                    fillAlpha = 1f,
                    stroke = null,
                    strokeAlpha = 1f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(11.333f, 14.958f)
                    horizontalLineToRelative(6f)
                    quadToRelative(0.417f, 0f, 0.729f, -0.291f)
                    quadToRelative(0.313f, -0.292f, 0.313f, -0.75f)
                    quadToRelative(0f, -0.459f, -0.313f, -0.75f)
                    quadToRelative(-0.312f, -0.292f, -0.729f, -0.292f)
                    horizontalLineToRelative(-6f)
                    quadToRelative(-0.416f, 0f, -0.729f, 0.313f)
                    quadToRelative(-0.312f, 0.312f, -0.312f, 0.729f)
                    quadToRelative(0f, 0.458f, 0.312f, 0.75f)
                    quadToRelative(0.313f, 0.291f, 0.729f, 0.291f)
                    close()
                    moveTo(23f, 28.833f)
                    horizontalLineToRelative(6.083f)
                    quadToRelative(0.459f, 0f, 0.75f, -0.291f)
                    quadToRelative(0.292f, -0.292f, 0.292f, -0.75f)
                    quadToRelative(0f, -0.459f, -0.292f, -0.73f)
                    quadToRelative(-0.291f, -0.27f, -0.75f, -0.27f)
                    horizontalLineTo(23f)
                    quadToRelative(-0.458f, 0f, -0.75f, 0.27f)
                    quadToRelative(-0.292f, 0.271f, -0.292f, 0.73f)
                    quadToRelative(0f, 0.458f, 0.292f, 0.75f)
                    quadToRelative(0.292f, 0.291f, 0.75f, 0.291f)
                    close()
                    moveToRelative(0f, -4.375f)
                    horizontalLineToRelative(6.083f)
                    quadToRelative(0.459f, 0f, 0.75f, -0.312f)
                    quadToRelative(0.292f, -0.313f, 0.292f, -0.771f)
                    quadToRelative(0f, -0.417f, -0.292f, -0.708f)
                    quadToRelative(-0.291f, -0.292f, -0.75f, -0.292f)
                    horizontalLineTo(23f)
                    quadToRelative(-0.458f, 0f, -0.75f, 0.292f)
                    quadToRelative(-0.292f, 0.291f, -0.292f, 0.75f)
                    quadToRelative(0f, 0.458f, 0.292f, 0.75f)
                    quadToRelative(0.292f, 0.291f, 0.75f, 0.291f)
                    close()
                    moveToRelative(-8.625f, 5.75f)
                    quadToRelative(0.417f, 0f, 0.708f, -0.312f)
                    quadToRelative(0.292f, -0.313f, 0.292f, -0.729f)
                    verticalLineToRelative(-2.542f)
                    horizontalLineToRelative(2.5f)
                    quadToRelative(0.458f, 0f, 0.75f, -0.292f)
                    quadToRelative(0.292f, -0.291f, 0.292f, -0.75f)
                    quadToRelative(0f, -0.416f, -0.292f, -0.729f)
                    quadToRelative(-0.292f, -0.312f, -0.75f, -0.312f)
                    horizontalLineToRelative(-2.5f)
                    verticalLineToRelative(-2.5f)
                    quadToRelative(0f, -0.417f, -0.292f, -0.709f)
                    quadToRelative(-0.291f, -0.291f, -0.75f, -0.291f)
                    quadToRelative(-0.458f, 0f, -0.75f, 0.291f)
                    quadToRelative(-0.291f, 0.292f, -0.291f, 0.709f)
                    verticalLineToRelative(2.5f)
                    horizontalLineToRelative(-2.5f)
                    quadToRelative(-0.459f, 0f, -0.75f, 0.312f)
                    quadToRelative(-0.292f, 0.313f, -0.292f, 0.729f)
                    quadToRelative(0f, 0.459f, 0.292f, 0.75f)
                    quadToRelative(0.291f, 0.292f, 0.75f, 0.292f)
                    horizontalLineToRelative(2.5f)
                    verticalLineToRelative(2.542f)
                    quadToRelative(0f, 0.416f, 0.312f, 0.729f)
                    quadToRelative(0.313f, 0.312f, 0.771f, 0.312f)
                    close()
                    moveToRelative(8.375f, -13.041f)
                    quadToRelative(0.333f, 0.333f, 0.771f, 0.333f)
                    quadToRelative(0.437f, 0f, 0.729f, -0.333f)
                    lineToRelative(1.75f, -1.75f)
                    lineToRelative(1.792f, 1.791f)
                    quadToRelative(0.291f, 0.292f, 0.708f, 0.292f)
                    reflectiveQuadToRelative(0.75f, -0.333f)
                    quadToRelative(0.292f, -0.334f, 0.292f, -0.75f)
                    quadToRelative(0f, -0.417f, -0.292f, -0.75f)
                    lineToRelative(-1.792f, -1.75f)
                    lineToRelative(1.792f, -1.792f)
                    quadToRelative(0.292f, -0.292f, 0.292f, -0.708f)
                    quadToRelative(0f, -0.417f, -0.334f, -0.709f)
                    quadToRelative(-0.291f, -0.333f, -0.729f, -0.333f)
                    quadToRelative(-0.437f, 0f, -0.729f, 0.292f)
                    lineTo(26f, 12.417f)
                    lineToRelative(-1.792f, -1.792f)
                    quadToRelative(-0.291f, -0.292f, -0.729f, -0.271f)
                    quadToRelative(-0.437f, 0.021f, -0.729f, 0.313f)
                    quadToRelative(-0.333f, 0.333f, -0.333f, 0.771f)
                    quadToRelative(0f, 0.437f, 0.333f, 0.729f)
                    lineToRelative(1.75f, 1.75f)
                    lineToRelative(-1.75f, 1.791f)
                    quadToRelative(-0.333f, 0.334f, -0.312f, 0.73f)
                    quadToRelative(0.02f, 0.395f, 0.312f, 0.729f)
                    close()
                    moveTo(7.875f, 34.75f)
                    quadToRelative(-1.042f, 0f, -1.833f, -0.792f)
                    quadToRelative(-0.792f, -0.791f, -0.792f, -1.833f)
                    verticalLineTo(7.875f)
                    quadToRelative(0f, -1.042f, 0.792f, -1.833f)
                    quadToRelative(0.791f, -0.792f, 1.833f, -0.792f)
                    horizontalLineToRelative(24.25f)
                    quadToRelative(1.042f, 0f, 1.833f, 0.792f)
                    quadToRelative(0.792f, 0.791f, 0.792f, 1.833f)
                    verticalLineToRelative(24.25f)
                    quadToRelative(0f, 1.042f, -0.792f, 1.833f)
                    quadToRelative(-0.791f, 0.792f, -1.833f, 0.792f)
                    close()
                    moveToRelative(0f, -2.625f)
                    horizontalLineToRelative(24.25f)
                    verticalLineTo(7.875f)
                    horizontalLineTo(7.875f)
                    verticalLineToRelative(24.25f)
                    close()
                    moveToRelative(0f, 0f)
                    verticalLineTo(7.875f)
                    verticalLineToRelative(24.25f)
                    close()
                }
            }.build()
        }
    }
}