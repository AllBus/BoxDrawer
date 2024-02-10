package com.kos.boxdrawe.presentation

import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import com.kos.figure.Figure
import com.kos.figure.FigureBezier
import com.kos.figure.IFigure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import vectors.Vec2

class BezierData(val tools: Tools) {

    private val cList = MutableStateFlow(
        listOf(
            Vec2(0.0, 0.0), Vec2(80.0, 20.0), Vec2(20.0, 80.0), Vec2(100.0, 100.0),
        )
    )

    val c1 = cList.asStateFlow()

    val startActionPos = cList.map { bezier ->
        if (bezier.size > 2) {
            val v0 = bezier.first()
            val v1 = bezier[1]
            v0 + Vec2.normal(v0, v1) * -30.0
        } else {
            Vec2.Zero
        }
    }

    val endActionPos = cList.map { bezier ->
        if (bezier.size > 2) {
            val v0 = bezier.last()
            val v1 = bezier[bezier.size - 2]
            v0 + Vec2.normal(v0, v1) * -30.0
        } else {
            Vec2.Zero
        }
    }

    val currentDistance = mutableDoubleStateOf(1.0)

    val figure = mutableStateOf<IFigure>(Figure.Empty)


    fun redraw() {

        figure.value = FigureBezier(
            c1.value
        )
    }

    fun save(fileName: String) {
        redraw()
        tools.saveFigures(fileName, figure.value)
    }

    fun print(): String {
        var st = c1.value.first()
        return "b ${
            c1.value.flatMapIndexed { i, v ->
                val r = listOf(v.x - st.x, v.y - st.y)
                if (i % 3 == 0) st = v
                r
            }.joinToString(" ")
        }"
    }

    fun movePoint(index: Int, newPosition: Vec2) {
        val r = c1.value
        if (index >= 0 && index < r.size) {
            cList.value = r.mapIndexed { i, vec -> if (i == index) newPosition else vec }
            redraw()
        }

    }

    fun addStartBezier() {
        val list = c1.value

        val f = list.firstOrNull() ?: Vec2.Zero
        cList.value =
            listOf(Vec2(-100.0, 0.0), Vec2(-100.0, 100.0), Vec2(0.0, 100.0)).map { it + f } + list
        redraw()
    }

    fun addEndBezier() {
        val list = c1.value
        val f = list.lastOrNull() ?: Vec2.Zero
        cList.value =
            list + listOf(Vec2(0.0, 100.0), Vec2(100.0, 100.0), Vec2(100.0, 0.0)).map { it + f }
        redraw()
    }

    fun deleteStart(){
        if (c1.value.size>4) {
            cList.value = c1.value.drop(3)
            redraw()
        }
    }

    fun deleteEnd(){
        if (c1.value.size>4) {
            cList.value = c1.value.dropLast(3)
            redraw()
        }
    }
}
