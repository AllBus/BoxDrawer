package com.kos.boxdrawe.presentation

import androidx.compose.runtime.mutableStateOf
import com.kos.ariphmetica.math.terms.MathTerm
import com.kos.boxdrawe.widget.NumericTextFieldState
import com.kos.boxdrawer.detal.bublik.Vec3
import com.kos.boxdrawer.formula.FormulaUtils.calc
import com.kos.boxdrawer.formula.FormulaUtils.extractValues
import com.kos.boxdrawer.formula.FormulaUtils.parse
import com.kos.boxdrawer.formula.FormulaUtils.podstanovka
import com.kos.figure.FigureEmpty
import com.kos.figure.FigurePolyline
import com.kos.figure.IFigure
import com.kos.figure.collections.FigureList
import kotlinx.coroutines.flow.MutableStateFlow
import turtoise.memory.SimpleTortoiseMemory
import vectors.Vec2
import kotlin.math.cos
import kotlin.math.sin

class FormulaData(override val tools: Tools) : SaveFigure {

    val figures = MutableStateFlow<IFigure>(FigureEmpty)

    val selectSystem = MutableStateFlow<Int>(0)

    var lineX = mutableStateOf(false)
    var lineY = mutableStateOf(true)
    var lineZ = mutableStateOf(true)


    val inputXCount = NumericTextFieldState(
        value = 100.0,
        minValue = 1.0,
        maxValue = 10000.0,
        digits = 0,
    ) { v ->
        redrawBox()
    }

    val inputYCount = NumericTextFieldState(
        value = -100.0,
        minValue = -1000000.0,
        maxValue = 1000000.0,
        digits = 3,
    ) { v ->
        redrawBox()
    }

    val inputZCount = NumericTextFieldState(
        value = 100.0,
        minValue = -1000000.0,
        maxValue = 1000000.0,
        digits = 3,
    ) { v ->
        redrawBox()
    }


    fun setSystem(system: Int) {
        selectSystem.value = system
    }

    override suspend fun createFigure(): IFigure {
        return figures.value
    }

    private var fxt: MathTerm = parse("0")
    private var fyt: MathTerm = parse("0")
    private var fzt: MathTerm = parse("0")

    fun recalculate(xv: String, yv: String, zv: String, variables: String) {
        val memory = SimpleTortoiseMemory()
        val fx = xv
        val fy = yv
        val fz = zv


        try {
            val podValues = extractValues(variables, memory)


            fxt = podstanovka(fx, podValues)
            fyt = podstanovka(fy, podValues)
            fzt = if (fz.isNotEmpty()) podstanovka(fz, podValues) else parse("0")

            redrawBox()

            // val fdxt = diff("($fx)'t")
            //  val fdyt = diff("($fy)'t")
            //  val fdzt = diff("($fz)'t")

        } catch (e: Exception) {

        }
    }

    fun redrawBox() {
        try{
            val tt = parse("t")
            val uu = parse("u")
            val vv = parse("v")
            val params = listOf(tt, uu, vv)


            val ts = inputYCount.decimal
            val te = inputZCount.decimal

            val us = 0.0
            val ue = 1.0

            val vs = 0.0
            val ve = 1.0
            val cot = inputXCount.decimal.toInt()
            val cou = 1
            val cov = 1


            val converter = when (selectSystem.value) {
                0 -> ::converCoordinateEuler
                1 -> ::converCoordinateCylinder
                2 -> ::converCoordinateSferic
                else -> ::converCoordinateEuler
            }

            val points = (0..cot).mapNotNull { i ->
                val t = ts + (te - ts) * i * 1.0 / cot
                val tv = parse(t)

                //  println("tv -> $tv : ${OutExpression.apply(tv)}")
                // podstanovka(fxt,  )
                val x = calc(fxt, tt, tv)
                val y = calc(fyt, tt, tv)
                val z = calc(fzt, tt, tv)

                //val dx = calc(fdxt, tt, tv)
                // val dy = calc(fdyt, tt, tv)
                //    println("$x : $y")
                /* Если не вычислена какая то точка то ничего не нарисуем */
                if (!x.isFinite() || !y.isFinite() || !z.isFinite())
                    null else {
                    converter(Vec3(x, y, z))
                }
            }

            figures.value = FigureList(
                listOf(
                    FigurePolyline(
                        points.map { p ->
                            Vec2(p.x, p.y)
                        }
                    )
                )
            )
        }catch (e:Exception){

        }
    }

    fun converCoordinateEuler(coord: Vec3): Vec3 {
        return coord
    }

    fun converCoordinateSferic(a: Vec3): Vec3 {
        return Vec3(a.x * sin(a.y) * cos(a.z), a.x * sin(a.y) * sin(a.z), a.x * cos(a.y))
    }

    fun converCoordinateCylinder(a: Vec3): Vec3 {
        return Vec3(a.x * sin(a.y), a.x * cos(a.y), a.z)
    }
}