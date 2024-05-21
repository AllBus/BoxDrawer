package com.kos.boxdrawe.drawer

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.kos.boxdrawe.themes.ThemeColors
import com.kos.figure.IFigure
import vectors.Vec2

fun DrawScope.drawFigures(figureLine: IFigure, selectedItem: List<IFigure>) {

    val penColor = Color.Gray
    val style = Stroke(width = Stroke.HairlineWidth)

    val drawer = ComposeFigureDrawer(this, penColor, style)
    figureLine.draw(drawer)

    drawer.penColor = ThemeColors.selectedFigureColor
    selectedItem.forEach {
        it.draw(drawer)
    }

    val bound = figureLine.rect()
    drawer.penColor = Color.Green
    drawer.style = Stroke()
    drawer.drawRect(bound.min- Vec2(10.0, 10.0), bound.max-bound.min+Vec2(20.0, 20.0))



}
