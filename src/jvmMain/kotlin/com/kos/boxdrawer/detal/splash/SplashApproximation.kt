package com.kos.boxdrawer.detal.splash

import com.kos.ariphmetica.Calculator
import com.kos.ariphmetica.math.algorithms.CopositeFunction
import com.kos.ariphmetica.math.algorithms.OutExpression
import com.kos.ariphmetica.math.algorithms.Replacement
import com.kos.ariphmetica.math.terms.MathTerm
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_2
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_INT
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_NONE
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_TEXT
import com.kos.figure.FigureBezier
import com.kos.figure.FigurePolyline
import turtoise.FigureCreator
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData
import turtoise.help.HelpDataParam
import turtoise.parser.TPArg
import vectors.Vec2

class SplashApproximation : ISplashDetail{
    override val names: List<String>
        get() = listOf("ap", "apx","approximation")

    override fun help(): HelpData {
        return HelpData(
            "ap (fx(t)) (fy(t)) (tstart tend count)",
            "Построить аппроксимацию фунции плоской кривой через ломанную из point точек",
            listOf(
                HelpDataParam("fx(t)", "Функция координаты x от t", FIELD_TEXT),
                HelpDataParam("fy(t)", "Функция координаты y от t", FIELD_TEXT),
                HelpDataParam("tstart", "Точка начала",FIELD_2),
                HelpDataParam("tend", "Точка конца", FIELD_NONE),
                HelpDataParam("count", "Количество точек", FIELD_INT),
            ),
            creator = TPArg.create("ap",
                TPArg.block(TPArg("fx(t)", FIELD_TEXT)),
                TPArg.block(TPArg("fy(t)", FIELD_TEXT)),
                TPArg.block(
                    TPArg("tse", FIELD_2),
                    TPArg("count", FIELD_INT)
                    ),
            )
        )
    }

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        val fx = com.takeBlock(1)?.innerLine.orEmpty()
        val fy = com.takeBlock(2)?.innerLine.orEmpty()
        com.takeBlock(3)?.let{ a ->
            val ts = figureExtractor.valueAt(a, 0, 0.0)
            val te = figureExtractor.valueAt(a, 1, 1.0)
            val co = figureExtractor.valueAt(a, 2, 30.0).toInt().coerceIn(1, 1000)
            if (fx.isNotEmpty() && fy.isNotEmpty()){
                try {
                    val fxt = calc(fx)
                    val fyt = calc(fy)
                    val fdxt = diff("($fx)'t")
                    val fdyt = diff("($fy)'t")
                    val tt = parse("t")
                    println("$fdxt")

                    val points = (0..co).mapNotNull { i ->
                        val t = ts + (te - ts) * i*1.0 / co
                        val tv = parse("$t")
                      //  println("tv -> $tv : ${OutExpression.apply(tv)}")
                        val x = calc(fxt, tt, tv)
                        val y = calc(fyt, tt, tv)

                        val dx = calc(fdxt, tt, tv)
                        val dy = calc(fdyt, tt, tv)
                    //    println("$x : $y")
                        /* Если не вычислена какая то точка то ничего не нарисуем */
                        if (!x.isFinite() || !y.isFinite())
                            null else {

                            Vec2(x, y) to
                            Vec2(dx, dy)
                        }
                    }
                    if (points.size>=2) {
                        builder.addProduct(FigurePolyline(points.map { it.first }))
                        builder.addProduct(
                            FigureCreator.colorDxf(
                                6,
                                FigurePolyline( listOf(points[0].first, points[0].first)+
                                        points.flatMap{ listOf(it.first-it.second, it.first, it.first+it.second)}+
                                        listOf(points.last().first, points.last().first)
                                )
                            )
                        )
                        builder.addProduct(
                            FigureCreator.colorDxf(
                                3,
                                FigureBezier( listOf(points[0].first, points[0].first)+
                                        points.flatMap{ listOf(it.first-it.second, it.first, it.first+it.second)}+
                                        listOf(points.last().first, points.last().first)
                                )
                            )
                        )
                    }
                }catch (e:Exception){

                }
            }
        }
    }

    fun calc(line:MathTerm, t:MathTerm, tvalue: MathTerm):Double{
        val r = OutExpression.apply(Calculator.fullCalc( Replacement.replace(line, t, tvalue))) as String
        val s = if (r.startsWith("(") && r.endsWith(")")){
            r.drop(1).dropLast(1)
        } else r
       // println(s)
        return s.toDoubleOrNull()?:0.0
    }

    fun parse(line:String):MathTerm{
        return Calculator.parseWithSpace(line)
    }
    fun calc(line:String):MathTerm{
        val dif = parse(line)
        return Calculator.fullCalc( CopositeFunction.compose(dif))
    }
    fun diff(line:String):MathTerm{
        val dif = parse(line)
        return Calculator.fullCalc(Calculator.diff( CopositeFunction.compose(dif)))
    }
}