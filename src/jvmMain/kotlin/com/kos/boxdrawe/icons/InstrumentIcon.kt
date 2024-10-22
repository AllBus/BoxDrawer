package com.kos.boxdrawe.icons

import androidx.compose.material.icons.materialPath
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object InstrumentIcon {

    @Composable
    fun rememberRectangle(): ImageVector {
        return remember {
            ImageVector.Builder(
                name = "rectangle",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24.0f,
                viewportHeight = 24.0f
            ).apply {
                materialPath{
                    this.moveTo(3f, 8f)
                    this.lineTo(21f, 8f)
                    this.lineTo(21f, 16f)
                    this.lineTo(3f, 16f)
                    this.lineTo(3f, 8f)
                    this.close()
                }
            }.build()
        }
    }

    @Composable
    fun rememberLine(): ImageVector {
        return remember {
            ImageVector.Builder(
                name = "line",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24.0f,
                viewportHeight = 24.0f
            ).apply {
                materialPath{
                    this.moveTo(3f, 8f)
                    this.lineTo(21f, 16f)
                    this.lineTo(23f, 18f)
                    this.lineTo(5f, 10f)
                    this.lineTo(3f, 8f)
                    close()
                }
            }
        }.build()
    }

    @Composable
    fun rememberCircle():ImageVector {
        return remember {
            ImageVector.Builder(
                name = "circle",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24.0f,
                viewportHeight = 24.0f
            ).apply {
                materialPath{
                    this.moveTo(12f, 12f)
                    this.moveToRelative(-10f, 0f)
                    this.arcToRelative(10f, 10f, 0f, true, true, 20f, 0f)
                    this.arcToRelative(10f, 10f, 0f, true, true, -20f, 0f)

                }
            }
        }.build()
    }

    @Composable
    fun rememberEllipse():ImageVector {
        return remember {
            ImageVector.Builder(
                name = "ellipse",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24.0f,
                viewportHeight = 24.0f
            ).apply {
                materialPath {
                    this.moveTo(12f, 12f)
                    this.moveToRelative(-10f, 0f)
                    this.arcToRelative(10f, 6f, 0f, true, true, 20f, 0f)
                    this.arcToRelative(10f, 6f, 0f, true, true, -20f, 0f)

                }
            }.build()
        }

    }

    @Composable
    fun rememberTriangle():ImageVector {
        return remember {
            ImageVector.Builder(
                name = "triangle",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24.0f,
                viewportHeight = 24.0f
            ).apply {
                materialPath {
                    this.moveTo(12f, 8f)
                    this.lineTo(16f, 16f)
                    this.lineTo(8f, 16f)
                    this.lineTo(12f, 8f)
                    this.close()
                }
            }.build()
        }
    }

    @Composable
    fun rememberStar():ImageVector {
        return remember {
            ImageVector.Builder(
                name = "star",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24.0f,
                viewportHeight = 24.0f
            ).apply {
                materialPath {
                    this.moveTo(12f, 18f)
                    this.lineTo(18f, 12f)
                    this.lineTo(12f, 6f)
                    this.lineTo(6f, 12f)
                    this.lineTo(12f, 18f)
                    this.close()
                }
            }.build()
        }
    }

    @Composable
    fun rememberPolygon():ImageVector {
        return remember {
            ImageVector.Builder(
                name = "polygon",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24.0f,
                viewportHeight = 24f,
            ).apply {
                materialPath {
                    this.moveTo( 4f, 12f)
                    this.lineTo(8f, 4f)
                    this.lineTo(16f, 4f)
                    this.lineTo(20f, 12f)
                    this.lineTo(16f, 20f)
                    this.lineTo(8f, 20f)
                    this.lineTo(4f, 12f)
                    this.close()
                }

            }.build()
        }
    }

    @Composable
    fun rememberBezier(): ImageVector {
        return remember {
            ImageVector.Builder(
                name = "polyline",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 30.0f,
                viewportHeight = 24.0f
            ).apply {
                materialPath {
                    this.moveTo(5f, 10f)
                    this.quadToRelative(5f, 12f, 10f, 0f)
                    this.quadToRelative(5f, -12f, 10f, 0f)
                    this.lineToRelative(0f, 2f)
                    this.quadToRelative(-5f, -12f, -10f, 0f)
                    this.quadToRelative(-5f, 12f, -10f, 0f)
                    this.lineToRelative(0f, -2f)
                    this.close()
                }
            }.build()
        }
    }

    @Composable
    fun rememberPolyLine(): ImageVector {
        return remember {
            ImageVector.Builder(
                name = "polyline",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 30.0f,
                viewportHeight = 24.0f
            ).apply {
                materialPath {
                    moveTo(5f, 10f)
                    lineToRelative(5f, 12f)
                    lineToRelative( 10f, 0f)
                    lineToRelative(5f, -12f)
                    lineToRelative( 10f, 0f)
                    lineToRelative(0f, 2f)
                    lineToRelative(-5f, -12f)
                    lineToRelative( -10f, 0f)
                    lineToRelative(-5f, 12f)
                    lineToRelative( -10f, 0f)
                    lineToRelative(0f, -2f)
                    close()
                }
            }.build()
        }
    }
}