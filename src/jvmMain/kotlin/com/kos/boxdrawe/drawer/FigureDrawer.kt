package com.kos.boxdrawe.drawer

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import figure.*
import vectors.Vec2

fun DrawScope.drawFigures(figureLine: IFigure) {

    val penColor = Color.Gray
    val style = Stroke(width = 1.0f)

    val drawer = ComposeFigureDrawer(this, penColor, style)
    figureLine.draw(drawer)
}
