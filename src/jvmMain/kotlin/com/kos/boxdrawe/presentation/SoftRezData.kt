package com.kos.boxdrawe.presentation

import androidx.compose.runtime.mutableStateOf
import com.kos.boxdrawe.widget.NumericTextFieldState
import com.kos.boxdrawer.detal.soft.SoftRez
import com.kos.figure.FigureEmpty
import com.kos.figure.FigureLine
import com.kos.figure.collections.FigureList
import com.kos.figure.IFigure
import com.kos.figure.composition.FigureTranslateWithRotate
import kotlinx.coroutines.flow.MutableStateFlow
import vectors.Vec2

class SoftRezData(override val tools: ITools, val tortoise: TortoiseData) : SaveFigure {
    val sr = SoftRez()

    var baseFigure: IFigure = FigureEmpty

    override suspend fun createFigure(): IFigure = drawRez(baseFigure)

    val figures = MutableStateFlow<IFigure>(FigureEmpty)

    fun drawRez(figure: IFigure): IFigure {
        baseFigure = figure

        val f = if (figure.count == 0) {
            FigureLine(Vec2(0.0, 0.0), Vec2(2.0, 0.0))
        } else
            figure

        val result = if (isInSize.value) {
            FigureTranslateWithRotate(
            sr.drawRez(
                widthF = height.decimal,
                heightF = width.decimal,
                delta = cellHeightDistance.decimal * 2.0,
                dlina = lineLength.decimal,
                soedinenie = cellWidthDistance.decimal,
                firstSmall = firstSmall.value,
            ), Vec2(width.decimal, 0.0), 90.0,)
        } else {
            sr.drawRect(
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
        val r = if (isDrawBox.value) {
            FigureList(
                listOf(
                    sr.drawRectangle(
                        w = width.decimal,
                        h = height.decimal,
                    ),
                    result
                )
            )
        } else result

        figures.value = r
        return r
    }

    fun redraw() {
        drawRez(tortoise.figures.value)
    }

    fun print(): String{
        return figures.value.print()
    }

    var innerChecked = mutableStateOf(true)
    var isDrawBox = mutableStateOf(true)
    val width = NumericTextFieldState(60.0) { redraw() }
    val height = NumericTextFieldState(60.0) { redraw() }
    val cellWidthCount = NumericTextFieldState(5.0, 0, 1000.0) { redraw() }
    val cellHeightCount = NumericTextFieldState(6.0, 0, 1000.0) { redraw() }
    val cellWidthDistance = NumericTextFieldState(6.0, 2) { redraw() }
    val cellHeightDistance = NumericTextFieldState(3.0, 2) { redraw() }
    val isInSize = mutableStateOf(true)
    val firstSmall = mutableStateOf(false)
    val lineLength = NumericTextFieldState(18.0, 2) { redraw() }
}