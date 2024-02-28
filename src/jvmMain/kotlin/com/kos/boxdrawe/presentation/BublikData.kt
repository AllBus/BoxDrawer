package com.kos.boxdrawe.presentation

import androidx.compose.runtime.mutableStateOf
import com.kos.boxdrawe.widget.NumericTextFieldState
import com.kos.boxdrawer.detal.bublik.BublikCad
import com.kos.figure.Figure
import com.kos.figure.IFigure

class BublikData(val tools: ITools) {

    val figures = mutableStateOf<IFigure>(Figure.Empty)

    val radiusBublik = NumericTextFieldState(120.0) { redrawBox() }
    val radius = NumericTextFieldState(40.0) { redrawBox() }
    val segmentCount = NumericTextFieldState(12.0, 0, 1000.0) { redrawBox() }
    val sideCount = NumericTextFieldState(8.0, 0,1000.0) { redrawBox() }
    var pazPositionLeftTop = mutableStateOf(true)
    var pazPositionCenter = mutableStateOf(true)
    var pazPositionLeftBottom = mutableStateOf(true)
    var pazPositionRightTop = mutableStateOf(true)
    var pazPositionRightBottom = mutableStateOf(true)

    private val cad = BublikCad()

    fun redrawBox(){
        figures.value = createFigure()

    }

    private fun createFigure():IFigure {
        val w = radiusBublik.decimal;
        val h = radius.decimal;
        val se1 = segmentCount.decimal.toInt();
        val se2 = sideCount.decimal.toInt();

        val bublikPaz = BublikPaz(
            center = pazPositionCenter.value,
            leftTop =pazPositionLeftTop.value,
            leftBottom = pazPositionLeftBottom.value,
            rightTop = pazPositionRightTop.value,
            rightBottom = pazPositionRightBottom.value
        )
        return cad.torus(w, h, se1, se2, bublikPaz, tools.ds())
    }

    fun save(fileName: String) {
        tools.saveFigures(fileName, createFigure())
    }
}

