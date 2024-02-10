package com.kos.boxdrawe.presentation

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.kos.boxdrawe.widget.NumericTextFieldState
import com.kos.boxdrawer.detal.box.*
import figure.Figure
import figure.IFigure
import figure.composition.FigureColor
import turtoise.DrawerSettings
import turtoise.ZigzagInfo
import vectors.Vec2

class BoxData(val tools: ITools) {

    val figures = mutableStateOf<IFigure>(Figure.Empty)

    val selectZigTopId = mutableIntStateOf(PazExt.PAZ_NONE)
    val selectZigBottomId = mutableIntStateOf(PazExt.PAZ_HOLE)

    fun boxFigures(line: String, outVariant: BoxCad.EOutVariant): BoxAlgorithm {

        val ds = tools.ds()
        val inside = insideChecked.value

        val wald = WaldParam(
            topOffset = topOffset.decimal, //  tools.ds().holeOffset,
            bottomOffset = bottomOffset.decimal,//tools.ds().holeOffset,
            holeBottomOffset = bottomHoleOffset.decimal,
            holeTopOffset = topHoleOffset.decimal,
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
            weight = weight.decimal + if (inside) ds.boardWeight * 2 else 0.0,
            heights = listOf(edgeFL.decimal, edgeBL.decimal, edgeBR.decimal, edgeFR.decimal)
        )

        val polki = CalculatePolka.createPolki(line)

        return BoxAlgorithm(
            boxInfo = boxInfo,
            zigs = ZigInfoList(
                zigW = widthZigState.zigInfo,
                zigH = heightZigState.zigInfo,
                zigWe = weightZigState.zigInfo,
                zigPolka = polkaZigState.zigInfo,
                zigPolkaPol = polkaPolZigState.zigInfo
            ),
            wald = wald,
            polki = polki,
            outVariant = outVariant,
            polkiIn = polkiInChecked.value
        )
    }

    fun createBox(line: String) {
        val alg = boxFigures(line, if (alternative.value) BoxCad.EOutVariant.ALTERNATIVE else BoxCad.EOutVariant.VOLUME)
        val ds = tools.ds()
        val fig = boxFigures(alg, ds)
        figures.value =fig
    }

    fun saveBox(fileName: String, line: String) {
        val alg = boxFigures(line, if (alternative.value) BoxCad.EOutVariant.ALTERNATIVE else BoxCad.EOutVariant.COLUMN)
        val ds = tools.ds()
        val fig = FigureColor(
            Color.DarkGray.toArgb(),
            boxFigures(alg, ds)
        )

        tools.saveFigures(fileName, fig)
    }

    suspend fun printBox(line: String):String{
        val alg = boxFigures(line, if (alternative.value) BoxCad.EOutVariant.ALTERNATIVE else BoxCad.EOutVariant.COLUMN)

        return alg.commandLine()
    }

    fun redrawBox(){
        createBox(text.value)
    }

    val width = NumericTextFieldState(100.0) { redrawBox() }
    val height = NumericTextFieldState(50.0) { redrawBox() }
    val weight = NumericTextFieldState(60.0) { redrawBox() }
    val topOffset = NumericTextFieldState(2.0) { redrawBox() }
    val bottomOffset = NumericTextFieldState(2.0) { redrawBox() }
    val topHoleOffset = NumericTextFieldState(2.0) { redrawBox() }
    val bottomHoleOffset = NumericTextFieldState(2.0) { redrawBox() }
    var insideChecked = mutableStateOf(false)
    var polkiInChecked = mutableStateOf(false)
    var alternative = mutableStateOf(true)
    val text = mutableStateOf("")

    val edgeFL = NumericTextFieldState(0.0) { redrawBox() }
    val edgeBL = NumericTextFieldState(0.0) { redrawBox() }
    val edgeBR = NumericTextFieldState(0.0) { redrawBox() }
    val edgeFR = NumericTextFieldState(0.0) { redrawBox() }


    val widthZigState = ZigZagState({redrawBox()})
    val heightZigState = ZigZagState({redrawBox()})
    val weightZigState = ZigZagState({redrawBox()})
    val polkaZigState = ZigZagState({redrawBox()})
    val polkaPolZigState = ZigZagState({redrawBox()})

    companion object {
        fun boxFigures(alg: BoxAlgorithm, ds: DrawerSettings): IFigure{
            val bwi = alg.boxInfo.width - ds.boardWeight * 2
            val bwe = alg.boxInfo.weight - ds.boardWeight * 2
            val upWidth = if (alg.polkiIn) ds.boardWeight else 0.0

            val calc = CalculatePolka.calculatePolki(alg.polki, bwi,bwe, upWidth)

            calc.zigPolkaH = alg.zigs.zigPolka
            calc.zigPolkaPol = alg.zigs.zigPolkaPol

            return BoxCad.box(
                startPoint = Vec2.Zero,
                boxInfo = alg.boxInfo,
                zigW = alg.zigs.zigW,
                zigH = alg.zigs.zigH,
                zigWe = alg.zigs.zigWe,
                drawerSettings = ds,
                waldParams = alg.wald,
                polki = calc,
                outVariant = alg.outVariant
            )
        }
    }
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