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
import kotlin.math.atan
import kotlin.math.atan2

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

    init {
        redraw()
    }

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
            c1.value.drop(1).flatMapIndexed { i, v ->
                val r = listOf(v.x - st.x, v.y - st.y)
                if ((i+1) % 3 == 0) st = v
                r
            }.joinToString(" ")
        }"
    }

    fun newBezier(){
        cList.value = listOf(
            Vec2(0.0, 0.0), Vec2(80.0, 20.0), Vec2(20.0, 80.0), Vec2(100.0, 100.0),
        )
        redraw()
    }

    fun movePoint(index: Int, newPosition: Vec2) {
        val r = c1.value
        if (index >= 0 && index < r.size) {
            cList.value = r.mapIndexed { i, vec -> if (i == index) newPosition else vec }
            redraw()
        }

    }

    fun movePointFlat(index: Int, newPosition: Vec2) {
        val r = c1.value
        if (index >= 0 && index < r.size) {

            val p = when (index % 3){
                0  -> 0
                1 -> -2
                2 -> 2
                else -> 0
            }
            if (p!= 0){
                val pp = index+p
                val p3 = index+p/2
                if (pp>= 0 && pp < r.size){
                    val d = Vec2.distance(r[p3], r[pp])
                    val a = r[p3]-newPosition
                    val d2 = Vec2.distance(r[p3], newPosition)
                    val position2 = r[p3] + a*d /d2
                    cList.value = r.mapIndexed { i, vec ->
                        if (i == index) newPosition else
                        if (i == pp) position2 else
                            vec
                    }
                    redraw()
                    return
                }
            }
            cList.value = r.mapIndexed { i, vec -> if (i == index) newPosition else vec }
            redraw()
        }
    }

    fun movePointEdge(index: Int, newPosition: Vec2) {
        val r = c1.value
        if (index >= 0 && index < r.size) {

            val p = when (index % 3){
                0  -> 0
                1 -> -2
                2 -> 2
                else -> 0
            }
            if (p!= 0){
                val pp = index+p
                val p3 = index+p/2
                if (pp>= 0 && pp < r.size){

                    val n2 = r[pp] - r[p3]
                    val n1 = r[p3] - r[index]
                    val nn = r[p3] - newPosition

                    val t2 = atan2(n2.y, n2.x)
                    val t1 = atan2(n1.y, n1.x)
                    val tn = atan2(nn.y, nn.x)

                    val dn = tn-t1

                    val position2 = r[p3]+ n2.rotate(dn)
                    //val newN2 = n2+dn

                    cList.value = r.mapIndexed { i, vec ->
                        if (i == index) newPosition else
                            if (i == pp) position2 else
                                vec
                    }
                    redraw()
                    return
                }
            }
            cList.value = r.mapIndexed { i, vec -> if (i == index) newPosition else vec }
            redraw()
        }
    }

    /** сделать линию прямой */
    fun movePointLine(index: Int) {
        val r = c1.value
        if (index >= 0 && index < r.size) {
            val p = when (index % 3){
                0  -> 0
                1 -> 1
                2 -> -1
                else -> 0
            }

            if (p!= 0){
                val st = index-p
                val ep = index+2*p
                val c1 = Vec2.lerp(r[st], r[ep],0.25)
                val c2 = Vec2.lerp(r[st], r[ep],0.75)
                val pp = index + p
                cList.value = r.mapIndexed { i, vec ->
                    if (i == index) c1 else
                        if (i == pp) c2 else
                            vec
                }
                 redraw()
            }
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
