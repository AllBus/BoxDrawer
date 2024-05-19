package com.kos.boxdrawe.presentation

import androidx.compose.runtime.mutableStateOf
import com.kos.boxdrawe.widget.NumericTextFieldState
import com.kos.boxdrawer.detal.bublik.BublikCad
import com.kos.figure.Figure
import com.kos.figure.IFigure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.io.File

class BublikData(override val tools: ITools): SaveFigure {

    val radiusBublik = NumericTextFieldState(120.0) { redrawBox() }
    val radius = NumericTextFieldState(40.0) { redrawBox() }
    val holeRadius = NumericTextFieldState(30.0) { redrawBox() }
    val segmentCount = NumericTextFieldState(12.0, 0, 1000.0) { redrawBox() }
    val sideCount = NumericTextFieldState(8.0, 0,1000.0) { redrawBox() }
    var pazPositionLeftTop = mutableStateOf(true)
    var pazPositionCenter = mutableStateOf(true)
    var pazPositionLeftBottom = mutableStateOf(true)
    var pazPositionRightTop = mutableStateOf(true)
    var pazPositionRightBottom = mutableStateOf(true)

    private val cad = BublikCad()

    val redrawIndex = MutableStateFlow<Int>(0)

    val figures = redrawIndex.map { createFigure() }

    fun redrawBox(){
        redrawIndex.value+=1
    }

    override suspend fun createFigure():IFigure {
        val w = radiusBublik.decimal;
        val h = radius.decimal;
        val se1 = segmentCount.decimal.toInt();
        val se2 = sideCount.decimal.toInt();

        val bublikPaz = BublikPaz(
            center = pazPositionCenter.value,
            leftTop =pazPositionLeftTop.value,
            leftBottom = pazPositionLeftBottom.value,
            rightTop = pazPositionRightTop.value,
            rightBottom = pazPositionRightBottom.value,
        )
        return cad.torus(
            radius = w,
            torRadius = h,
            ringPart = se1,
            stenaPart = se2,
            bublikPaz = bublikPaz,
            holeRadius = holeRadius.decimal,
            drawerSettings = tools.ds(),
        )
    }

}

