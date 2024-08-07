package com.kos.boxdrawe.drawer

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.rememberTextMeasurer
import com.kos.boxdrawe.themes.ThemeColors
import com.kos.figure.FigureInfo
import com.kos.figure.IFigure
import vectors.Vec2


fun DrawScope.drawFigures(figureLine: IFigure, selectedItem: List<FigureInfo>, measurer: TextMeasurer) {

    val penColor = Color.Gray
    val style = Stroke(width = Stroke.HairlineWidth)


    val drawer = ComposeFigureDrawer(this, penColor, style, measurer)
    figureLine.draw(drawer)

    drawer.penColor = ThemeColors.selectedFigureColor
    selectedItem.forEach {
        withTransform({
            val m = Matrix(it.transform.values.copyOf())
            transform(m)
        }) {
            it.figure.draw(drawer)
        }
    }

    val bound = figureLine.rect()
    drawer.penColor = Color.Green
    drawer.style = Stroke()
    drawer.drawRect(bound.min- Vec2(10.0, 10.0), bound.max-bound.min+Vec2(20.0, 20.0))



}
