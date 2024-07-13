package com.kos.boxdrawe.presentation

import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import com.kos.boxdrawe.widget.NumericTextFieldState
import com.kos.figure.FigureBezier
import com.kos.figure.FigureEmpty
import com.kos.figure.IFigure
import com.kos.figure.collections.FigureList
import com.kos.figure.composition.FigureOnPath
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import turtoise.TortoiseProgram
import turtoise.TortoiseRunner
import turtoise.TortoiseState
import turtoise.parser.TortoiseParser
import vectors.Vec2
import kotlin.math.atan2

class BezierData(override val tools: Tools) : SaveFigure {

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
            v0 + Vec2.normalize(v0, v1) * -30.0
        } else {
            Vec2.Zero
        }
    }

    val endActionPos = cList.map { bezier ->
        if (bezier.size > 2) {
            val v0 = bezier.last()
            val v1 = bezier[bezier.size - 2]
            v0 + Vec2.normalize(v0, v1) * -30.0
        } else {
            Vec2.Zero
        }
    }

    val currentDistance = mutableDoubleStateOf(1.0)
    val lastSelectedPoint = mutableIntStateOf(-1)

    val figure = mutableStateOf<IFigure>(FigureEmpty)

    private val pathFigure = mutableStateOf<IFigure>(FigureEmpty)

    val pathRast = NumericTextFieldState(0.01) { redraw() }
    val pathOffset = NumericTextFieldState(0.0) { redraw() }
    val pathCount = NumericTextFieldState(100.0, 0) { redraw() }
    val pathFigureText = mutableStateOf("")

    init {
        redraw()
    }

    fun redraw() {
        val fb = FigureBezier(
            c1.value
        )

        val fp = FigureOnPath(
            figure = pathFigure.value,
            path = fb,
            count = pathCount.decimal.toInt(),
            distanceInPercent = pathRast.decimal,
            startOffsetInPercent = pathOffset.decimal,
            reverse = false,
            useNormal = true,
            angle = 0.0,
            pivot = Vec2.Zero
        )

//        val cc = c1.value
//        val pe =  FigureEllipse(cc[0], Vec2.distance(cc[1], cc[0]), Vec2.distance(cc[2], cc[0]), 10.0  )
//
//        val fp2 = FigureOnPath(
//            figure = pathFigure.value,
//            path = pe,
//            count = pathCount.decimal.toInt(),
//            distanceInPercent = pathRast.decimal,
//            startOffsetInPercent = pathOffset.decimal,
//            reverse = false,
//            useNormal = true,
//            angle = 0.0,
//            pivot = Vec2.Zero
//        )
        figure.value = FigureList(listOf(fb, fp))
    }

    override suspend fun createFigure(): IFigure = figure.value

    fun print(): String {
        var st = c1.value.first()
        return "b ${
            c1.value.drop(1).flatMapIndexed { i, v ->
                val r = listOf(v.x - st.x, v.y - st.y)
                if ((i + 1) % 3 == 0) st = v
                r
            }.joinToString(" ")
        }"
    }

    fun newBezier() {
        cList.value = listOf(
            Vec2(0.0, 0.0), Vec2(80.0, 20.0), Vec2(20.0, 80.0), Vec2(100.0, 100.0),
        )
        redraw()
    }

    fun newBezier(line: String) {
        var pred = Vec2.Zero
        println(line)
        val p = line
            .split(' ', ',', ';', '(', ')', '[', ']', ':')
            .filter { it.isNotEmpty() }
            .mapNotNull {
                println(it)
                it.toDoubleOrNull()
            }
            .windowed(2, 2) { l -> Vec2(l[0], l[1]) }
            .windowed(3, 3) { l ->
                val r = l.map {
                    it + pred
                }
                pred = r.last()
                println(r)
                r
            }.flatten()
        cList.value = listOf(Vec2.Zero) + p
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

            val p = when (index % 3) {
                0 -> 0
                1 -> -2
                2 -> 2
                else -> 0
            }
            if (p != 0) {
                val pp = index + p
                val p3 = index + p / 2
                if (pp >= 0 && pp < r.size) {
                    val d = Vec2.distance(r[p3], r[pp])
                    val a = r[p3] - newPosition
                    val d2 = Vec2.distance(r[p3], newPosition)
                    if (d2 > 0.0) {
                        val position2 = r[p3] + a * d / d2
                        cList.value = r.mapIndexed { i, vec ->
                            if (i == index) newPosition else
                                if (i == pp) position2 else
                                    vec
                        }
                        redraw()
                    }
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

            val p = when (index % 3) {
                0 -> 0
                1 -> -2
                2 -> 2
                else -> 0
            }
            if (p != 0) {
                val pp = index + p
                val p3 = index + p / 2
                if (pp >= 0 && pp < r.size) {

                    val n2 = r[pp] - r[p3]
                    val n1 = r[p3] - r[index]
                    val nn = r[p3] - newPosition

                    val t1 = atan2(n1.y, n1.x)
                    val tn = atan2(nn.y, nn.x)

                    val dn = tn - t1

                    val position2 = r[p3] + n2.rotate(dn)

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
            val p = when (index % 3) {
                0 -> 0
                1 -> 1
                2 -> -1
                else -> 0
            }

            if (p != 0) {
                val st = index - p
                val ep = index + 2 * p
                val c1 = Vec2.lerp(r[st], r[ep], 0.25)
                val c2 = Vec2.lerp(r[st], r[ep], 0.75)
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

        val p = if (list.size > 2) {
            val v0 = list.first()
            val v1 = list[1]
            Vec2.normalize(v0, v1) * -30.0
        } else {
            Vec2(-100.0, 0.0)
        }

        cList.value =
            listOf(p * 3.0, p * 2.0, p).map { it + f } + list
        redraw()
    }

    fun addEndBezier() {
        val list = c1.value
        val f = list.lastOrNull() ?: Vec2.Zero

        val p = if (list.size > 2) {
            val v0 = list.last()
            val v1 = list[list.size - 2]
            Vec2.normalize(v0, v1) * -30.0
        } else {
            Vec2(100.0, 0.0)
        }

        cList.value = list + listOf(p, p * 2.0, p * 3.0).map { it + f }
        redraw()
    }

    fun addSegment(list: List<Vec2>, index: Int) {
        if (list.size<4)
            return

        val c1v = c1.value
        if (index*3>c1v.size || index<0)
            return

        val p1 = c1v[index * 3]-list.first()
        val sdvinem = list.map { it + p1}

        val ap = sdvinem.last()-sdvinem.first()

        cList.value = c1v.take(index * 3 + 1) + sdvinem.drop(1) +
                c1v.drop(index * 3 + 1).map { it + ap }
        redraw()
    }

    fun deleteStart() {
        if (c1.value.size > 4) {
            cList.value = c1.value.drop(3)
            redraw()
        }
    }

    fun deleteEnd() {
        if (c1.value.size > 4) {
            cList.value = c1.value.dropLast(3)
            redraw()
        }
    }

    fun createFigure(lines: String) {
        val program = tortoiseProgram(lines)
        val t = TortoiseRunner(program)
        val state = TortoiseState()
        val dr = t.draw(state, tools.ds())
        pathFigure.value = dr
        redraw()
    }

    private fun tortoiseProgram(lines: String): TortoiseProgram {
        val f = lines.split("\n").map { line ->
            TortoiseParser.extractTortoiseCommands(line)
        }

        val (c, a) = f.partition { it.first == "" }
        val k = tools.algorithms()
        return TortoiseProgram(
            commands = c.map { it.second },
            algorithms = (k + a).toMap()
        )
    }

    fun addSegment(index: Int, insertIndex:Int) {
        val spisokPoints = c1.value.drop(index * 3).take(4)
        return addSegment(spisokPoints, insertIndex/3)
    }

    fun removeSegment(pointIndex:Int){
        val index = pointIndex/3
        val c1v = c1.value
        if (index*3>c1v.size || index<0)
            return

        val di = index * 3 + 3
        val ap = if (di< c1v.size) { c1v[di]-c1v[index * 3] } else Vec2.Zero

        cList.value =  c1v.take(index * 3 + 1) +
                c1v.drop(di+1).map { it - ap }
        redraw()
    }

}
