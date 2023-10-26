package com.kos.boxdrawe.presentation

import androidx.compose.runtime.mutableStateOf
import com.kos.boxdrawe.widget.NumericTextFieldState
import com.kos.boxdrawer.detal.box.BoxCad
import com.kos.boxdrawer.detal.box.BoxInfo
import com.kos.boxdrawer.detal.box.PazForm
import com.kos.boxdrawer.detal.box.WaldParam
import figure.Figure
import figure.IFigure
import turtoise.ZigzagInfo
import vectors.Vec2

class BoxData(val tools: ITools) {

    val figures = mutableStateOf<IFigure>(Figure.Empty)


    private val box = BoxCad

    fun boxFigures(line: String): IFigure {

        val wald = WaldParam(
            topOffset = tools.ds().holeOffset,
            bottomOffset = tools.ds().holeOffset,
            holeOffset = tools.ds().holeOffset,
            holeWeight = tools.ds().holeWeight,
            topForm = PazForm.Paz,
            bottomForm = PazForm.Hole,
        )

        return BoxCad.box(
            startPoint = Vec2.Zero,
            boxInfo = BoxInfo(width.decimal, height.decimal, weight.decimal),
            zigW = ZigzagInfo(
                width = 15.0,
                delta = 35.0
            ),
            zigH = ZigzagInfo(
                width = 15.0,
                delta = 35.0
            ),
            zigWe = ZigzagInfo(
                width = 15.0,
                delta = 35.0
            ),
            drawerSettings = tools.ds(),
            waldParams = wald
        )
    }

    fun createBox(line: String) {
        val fig = boxFigures(line)
        figures.value = fig
    }

    fun saveBox(fileName: String, line: String) {
        val fig = boxFigures(line)
        figures.value = fig

        tools.saveFigures(fileName, fig)
    }

    val width = NumericTextFieldState(100.0) { createBox(text.value) }
    val height = NumericTextFieldState(50.0) { createBox(text.value) }
    val weight = NumericTextFieldState(60.0) { createBox(text.value) }
    var insideChecked = mutableStateOf(false)
    val text = mutableStateOf("")
}