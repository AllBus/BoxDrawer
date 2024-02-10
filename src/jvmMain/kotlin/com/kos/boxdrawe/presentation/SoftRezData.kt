package com.kos.boxdrawe.presentation

import androidx.compose.runtime.mutableStateOf
import com.kos.boxdrawe.widget.NumericTextFieldState
import com.kos.boxdrawer.detal.soft.SoftRez
import com.kos.figure.FigureLine
import com.kos.figure.IFigure
import vectors.Vec2

class SoftRezData(val tools: ITools) {
    val sr = SoftRez()

    fun saveRez(fileName: String, figure: IFigure) {
        tools.saveFigures(fileName, drawRez(figure))
    }

    fun drawRez(figure: IFigure): IFigure {
        val f = if (figure.count == 0) {
            FigureLine(Vec2(0.0, 0.0), Vec2(2.0, 0.0))
        } else
            figure

        return sr.drawRect(
            w = width.decimal,
            h = height.decimal,
            sdx = cellWidthDistance.decimal,
            sdy = cellHeightDistance.decimal,
            xCount = cellWidthCount.decimal.toInt(),
            yCount = cellHeightCount.decimal.toInt(),
            fit = innerChecked.value,
            form = f,
        )
    }

    var innerChecked = mutableStateOf(true)
    val width = NumericTextFieldState(60.0)
    val height = NumericTextFieldState(60.0)
    val cellWidthCount = NumericTextFieldState(5.0, 0, 1000.0)
    val cellHeightCount = NumericTextFieldState(6.0, 0, 1000.0)
    val cellWidthDistance = NumericTextFieldState(2.0, 2)
    val cellHeightDistance = NumericTextFieldState(2.0, 2)
}