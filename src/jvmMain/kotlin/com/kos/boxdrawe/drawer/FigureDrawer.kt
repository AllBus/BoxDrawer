package com.kos.boxdrawe.drawer

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.kos.figure.IFigure
import vectors.Vec2

fun DrawScope.drawFigures(figureLine: IFigure) {

    val penColor = Color.Gray
    val style = Stroke(width = 1.0f)

    val drawer = ComposeFigureDrawer(this, penColor, style)
    figureLine.draw(drawer)
    val bound = figureLine.rect()
    drawer.penColor = Color.Green
    drawer.style = Stroke()
    drawer.drawRect(bound.min- Vec2(10.0, 10.0), bound.max-bound.min+Vec2(20.0, 20.0))

}
