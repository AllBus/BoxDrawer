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
        val inside = insideChecked.value


        val wald = WaldParam(
            topOffset = tools.ds().holeOffset,
            bottomOffset = tools.ds().holeOffset,
            holeOffset = tools.ds().holeOffset,
            holeWeight = tools.ds().holeWeight,
            topForm = PazExt.intToPaz(selectZigTopId.value),
            bottomForm = PazExt.intToPaz(selectZigBottomId.value),
        )

        val boxInfo = BoxInfo(
            width = width.decimal + if (inside) ds.boardWeight * 2 else 0.0,
            height = height.decimal + if (inside) {
                wald.fullTopOffset(ds.boardWeight) +
                wald.fullBottomOffset(ds.boardWeight)
            } else 0.0,
            weight = weight.decimal + if (inside) ds.boardWeight * 2 else 0.0
        )

        val polki = CalculatePolka.createPolki(line)

        val bwi = boxInfo.width - ds.boardWeight * 2
        val bwe = boxInfo.weight - ds.boardWeight * 2
        val upWidth = if (polkiInChecked.value) ds.boardWeight else 0.0

        val calc = CalculatePolka.calculatePolki(polki, bwi,bwe, upWidth)

        calc.zigPolkaH = polkaZigState.zigInfo
        calc.zigPolkaPol = polkaPolZigState.zigInfo

        return BoxCad.box(
            startPoint = Vec2.Zero,
            boxInfo = boxInfo,
            zigW = widthZigState.zigInfo,
            zigH = heightZigState.zigInfo,
            zigWe = weightZigState.zigInfo,
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


    val widthZigState = ZigZagState({redrawBox()})
    val heightZigState = ZigZagState({redrawBox()})
    val weightZigState = ZigZagState({redrawBox()})
    val polkaZigState = ZigZagState({redrawBox()})
    val polkaPolZigState = ZigZagState({redrawBox()})
}

class ZigZagState(val redrawBox: () -> Unit){

    val width = NumericTextFieldState(15.0) { redrawBox() }
    val delta = NumericTextFieldState(35.0) { redrawBox() }
    val height = NumericTextFieldState(0.0) { redrawBox() }
    val enable = mutableStateOf(true)

    val zigInfo : ZigzagInfo get() {
        return ZigzagInfo(
            width = width.decimal,
            delta = delta.decimal,
            height = height.decimal,
            enable =enable.value
        )
    }
}