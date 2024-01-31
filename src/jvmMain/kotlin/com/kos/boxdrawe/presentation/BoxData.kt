package com.kos.boxdrawe.presentation

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import com.kos.boxdrawe.widget.NumericTextFieldState
import com.kos.boxdrawer.detal.box.*
import figure.Figure
import figure.FigureEmpty
import figure.IFigure
import turtoise.Orientation
import turtoise.ZigzagInfo
import vectors.Vec2

class BoxData(val tools: ITools) {

    val figures = mutableStateOf<IFigure>(Figure.Empty)

    val selectZigTopId = mutableIntStateOf(PazExt.PAZ_NONE)
    val selectZigBottomId = mutableIntStateOf(PazExt.PAZ_HOLE)


    private val box = BoxCad

    fun boxFigures(line: String): IFigure {

        val ds =  tools.ds()
        val boxInfo = BoxInfo(width.decimal, height.decimal, weight.decimal)
        val polki = CalculatePolka.createPolki(line)

        val bwi = boxInfo.width - ds.boardWeight * 2
        val bwe = boxInfo.weight - ds.boardWeight * 2
        val upWidth = if (polkiInChecked.value) ds.boardWeight else 0.0

        val calc = CalculatePolka.calculatePolki(polki, bwi,bwe, upWidth)

        calc.pazDelta = 15.0
        calc.pazWidth = 35.0

        val wald = WaldParam(
            topOffset = tools.ds().holeOffset,
            bottomOffset = tools.ds().holeOffset,
            holeOffset = tools.ds().holeOffset,
            holeWeight = tools.ds().holeWeight,
            topForm = PazExt.intToPaz(selectZigTopId.value),
            bottomForm = PazExt.intToPaz(selectZigBottomId.value),
        )

        return BoxCad.box(
            startPoint = Vec2.Zero,
            boxInfo = boxInfo,
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
            waldParams = wald,
            polki = calc,
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

    fun redrawBox(){
        createBox(text.value)
    }

    val width = NumericTextFieldState(100.0) { redrawBox() }
    val height = NumericTextFieldState(50.0) { redrawBox() }
    val weight = NumericTextFieldState(60.0) { redrawBox() }
    var insideChecked = mutableStateOf(false)
    var polkiInChecked = mutableStateOf(false)
    val text = mutableStateOf("")


}