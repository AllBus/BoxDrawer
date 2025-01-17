package com.kos.boxdrawe.icons

import androidx.compose.material.icons.materialPath
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill

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



    private val CUT_SIZE = 36.dp
    private val CORNER_RADIUS = 24.dp
    private val ZERO_COORDINATE = 0f

    @Composable
    fun Modifier.simBackground(
        backgroundColor: Color
    ): Modifier {
        return this then Modifier.drawWithCache {
            // Размеры компонента
            val width = size.width
            val height = size.height
            // Размеры среза. Должны быть захардкожены
            val cutSize = CUT_SIZE.toPx()
            val cutWidthPoint = width - cutSize
            val cornerRadius = CORNER_RADIUS.toPx()
            val cornerDiameter = cornerRadius*2
            val centerCorrect = cornerRadius*(Math.sqrt(2.0)-1).toFloat()

            val path = Path().apply {
                arcTo(Rect(ZERO_COORDINATE, ZERO_COORDINATE, cornerDiameter, cornerDiameter), 180f, 90f, true)
                arcTo(Rect(cutWidthPoint-cornerRadius-centerCorrect, ZERO_COORDINATE, cutWidthPoint+cornerRadius-centerCorrect, cornerDiameter), -90f, 45f, false)
                arcTo(Rect(width-cornerDiameter, cutSize-cornerRadius+centerCorrect, width, cutSize+cornerRadius+centerCorrect), -45f, 45f, false)
                arcTo(Rect(width-cornerDiameter, height-cornerDiameter, width, height), 0f, 90f, false)
                arcTo(Rect(ZERO_COORDINATE, height-cornerDiameter, cornerDiameter, height), 90f, 90f, false)
                close()
            }
            onDrawWithContent {
                drawPath(
                    path = path,
                    color = Color(0xA0FF0000),
                    style = Fill,
                )
                drawContent()
            }
        }
    }
//
//    @Composable
//    fun Modifier.simBackgroundA(
//        backgroundColor: Color
//    ): Modifier {
//        return this then Modifier.drawWithCache {
//            onDrawWithContent {
//
//                // Размеры компонента
//                val width = size.width
//                val height = size.height
//                // Размеры среза. Должны быть захардкожены
//                val cutWidth = CUT_WIDTH.toPx()
//                val cutHeight = CUT_HEIGHT.toPx()
//                val cutWidthPoint = width - cutWidth
//                val cutRadius = CUT_RADIUS.toPx()
//                val cornerRadius = CORNER_RADIUS.toPx()
//
//                val path = Path().apply {
//                    moveTo(
//                        ZERO_COORDINATE,
//                        cornerRadius
//                    ) // Начало с верхнего левого закругленного угла
//                    quadraticBezierTo(
//                        ZERO_COORDINATE,
//                        ZERO_COORDINATE,
//                        cornerRadius,
//                        ZERO_COORDINATE
//                    )
//                    lineTo(cutWidthPoint - cutRadius, ZERO_COORDINATE) // Верхняя линия
//
//                    cubicTo(
//                        x1 = cutWidthPoint - cutRadius,
//                        y1 = ZERO_COORDINATE,
//                        x2 = cutWidthPoint,
//                        y2 = ZERO_COORDINATE,
//                        x3 = cutWidthPoint + cutRadius,
//                        y3 = ZERO_COORDINATE + cutRadius,
//                    )
//                    lineTo(x = width - cutRadius, y = cutHeight - cutRadius)
//                    cubicTo(
//                        x1 = width - cutRadius,
//                        y1 = cutHeight - cutRadius,
//                        x2 = width,
//                        y2 = cutHeight,
//                        x3 = width,
//                        y3 = cutHeight + cutRadius,
//                    )
//
//                    lineTo(width, height - cornerRadius)
//                    quadraticBezierTo(
//                        width, height,
//                        width - cornerRadius, height
//                    )
//                    lineTo(cornerRadius, height)
//                    quadraticBezierTo(
//                        ZERO_COORDINATE,
//                        height,
//                        ZERO_COORDINATE,
//                        height - cornerRadius
//                    )
//                    close()
//                }
//
//                drawPath(
//                    path = path,
//                    color = Color(0xA0FF0000),
//                    style = Fill,
//                )
//
//                drawRoundRect(Color(0x4000FFFF),cornerRadius = CornerRadius(cornerRadius,cornerRadius))
//                drawContent()
//            }
//        }
//    }

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

    @Composable
    fun rememberPointerArrow(): ImageVector {
        return remember {
            ImageVector.Builder(
                name = "pointer",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24.0f,
                viewportHeight = 24.0f
            ).apply {
                materialPath {
                    this.moveTo(
                        4f, 4f
                    )
                    this.lineToRelative(2f, 10f)
                    lineToRelative(3f, -3f)
                    lineToRelative(10f, 10f)
                    lineToRelative(2f, -2f)
                    lineToRelative(-10f, -10f)
                    lineToRelative(5f, -2f)
                    this.lineToRelative(-11f, -6f)
                    this.close()
                }
            }.build()
        }
    }

    @Composable
    fun rememberCurve(): ImageVector {
        return remember {
            ImageVector.Builder(
                name = "curve",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24.0f,
                viewportHeight = 24.0f
            ).apply {
                materialPath {
                    this.moveTo(
                        4f, 11f
                    )
                    this.curveToRelative(1f, -5f, 9f, 5f, 16f, 0f)
                    lineToRelative(0f, 2f)
                    this.curveToRelative(-1f, -5f, -9f, 5f, -16f, 0f)
                    lineToRelative(0f, -2f)
                    close()
                }
            }.build()
        }


    }
}