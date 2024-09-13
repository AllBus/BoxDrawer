package com.kos.boxdrawe.icons

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.materialPath
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

object Expand {

    @Composable
    fun rememberExpandLess(tint:Color = Color.Unspecified): ImageVector {
        return remember {
            ImageVector.Builder(
                name = "expand_less",
                defaultWidth = 40.0.dp,
                defaultHeight = 40.0.dp,
                viewportWidth = 40.0f,
                viewportHeight = 40.0f
            ).apply {
                materialPath {

                    moveTo(11.125f, 24.542f)
                    quadToRelative(-0.417f, -0.375f, -0.417f, -0.917f)
                    reflectiveQuadToRelative(0.375f, -0.917f)
                    lineToRelative(8f, -8f)
                    quadToRelative(0.209f, -0.208f, 0.438f, -0.291f)
                    quadToRelative(0.229f, -0.084f, 0.479f, -0.084f)
                    quadToRelative(0.25f, 0f, 0.479f, 0.084f)
                    quadToRelative(0.229f, 0.083f, 0.438f, 0.291f)
                    lineToRelative(8f, 7.959f)
                    quadToRelative(0.375f, 0.375f, 0.375f, 0.916f)
                    quadToRelative(0f, 0.542f, -0.375f, 0.959f)
                    quadToRelative(-0.417f, 0.375f, -0.959f, 0.375f)
                    quadToRelative(-0.541f, 0f, -0.916f, -0.375f)
                    lineToRelative(-7.042f, -7f)
                    lineToRelative(-7.042f, 7.041f)
                    quadToRelative(-0.333f, 0.417f, -0.875f, 0.396f)
                    quadToRelative(-0.541f, -0.021f, -0.958f, -0.437f)
                    close()
                }
            }.build()
        }
    }
    @Composable
    fun rememberExpandMore(tint:Color = Color.Unspecified): ImageVector {
        return remember {
            ImageVector.Builder(
                name = "expand_more",
                defaultWidth = 40.0.dp,
                defaultHeight = 40.0.dp,
                viewportWidth = 40.0f,
                viewportHeight = 40.0f,
                tintColor = tint,
            ).apply {
                materialPath {
                    moveTo(20f, 25f)
                    quadToRelative(-0.25f, 0f, -0.479f, -0.104f)
                    quadToRelative(-0.229f, -0.104f, -0.438f, -0.313f)
                    lineToRelative(-8f, -8f)
                    quadToRelative(-0.375f, -0.375f, -0.354f, -0.937f)
                    quadToRelative(0.021f, -0.563f, 0.396f, -0.938f)
                    quadToRelative(0.417f, -0.416f, 0.937f, -0.375f)
                    quadToRelative(0.521f, 0.042f, 0.938f, 0.417f)
                    lineToRelative(7f, 7f)
                    lineToRelative(7.042f, -7.042f)
                    quadToRelative(0.375f, -0.333f, 0.937f, -0.354f)
                    quadToRelative(0.563f, -0.021f, 0.938f, 0.396f)
                    quadToRelative(0.416f, 0.375f, 0.375f, 0.938f)
                    quadToRelative(-0.042f, 0.562f, -0.417f, 0.937f)
                    lineToRelative(-7.958f, 7.958f)
                    quadToRelative(-0.209f, 0.209f, -0.438f, 0.313f)
                    quadTo(20.25f, 25f, 20f, 25f)
                    close()
                }
            }.build()
        }
    }


}