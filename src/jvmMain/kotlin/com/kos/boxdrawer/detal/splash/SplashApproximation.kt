package com.kos.boxdrawer.detal.splash

import com.kos.ariphmetica.Calculator
import com.kos.ariphmetica.math.algorithms.CopositeFunction
import com.kos.ariphmetica.math.algorithms.OutExpression
import com.kos.ariphmetica.math.algorithms.Replacement
import com.kos.ariphmetica.math.terms.MathTerm
import com.kos.boxdrawer.template.TemplateMemory
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_2
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_INT
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_NONE
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_TEXT
import com.kos.figure.FigurePolyline
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData
import turtoise.help.HelpDataParam
import turtoise.memory.TortoiseMemory
import turtoise.memory.keys.MemoryKey
import turtoise.parser.TPArg
import turtoise.parser.TortoiseParserStackBlock
import vectors.Vec2
import kotlin.math.abs

open class SplashApproximation : ISplashDetail {
    override val names: List<String>
        get() = listOf("ap", "apx", "approximation", "apc")

    override fun help(): HelpData {
        return HelpData(
            "ap (fx(t)) (fy(t)) [a=1;b=2]? (tstart tend count)",
            "Построить аппроксимацию фунции плоской кривой через ломанную из point точек",
            listOf(
                HelpDataParam("fx(t)", "Функция координаты x от t", FIELD_TEXT),
                HelpDataParam("fy(t)", "Функция координаты y от t", FIELD_TEXT),
                HelpDataParam("repl", "Подстановка значений в выражение функций", FIELD_TEXT),
                HelpDataParam("tstart", "Точка начала", FIELD_2),
                HelpDataParam("tend", "Точка конца", FIELD_NONE),
                HelpDataParam("count", "Количество точек", FIELD_INT),
            ),
            creator = TPArg.create(
                "ap",
                TPArg.block(TPArg("fx(t)", FIELD_TEXT)),
                TPArg.block(TPArg("fy(t)", FIELD_TEXT)),
                TPArg.noneOrOne("app",
                    TPArg.blockBrace('[', TPArg("repl", FIELD_TEXT))
                ),
                TPArg.block(
                    TPArg("tse", FIELD_2),
                    TPArg("count", FIELD_INT)
                ),
            )
        )
    }

    val MAX_STEP = 20
    val eps = 0.00001
    val tt = parse("t")

    fun findPeregib(
        predt: Double,
        nextt: Double,
        vp: Double,
        vn: Double,
        fdx: MathTerm,
        step: Int
    ): Pair<Double, Double> {
        val pc = (nextt + predt) / 2.0

        val tc = parse("${pc}")
        val vc = calc(fdx, tt, tc)
        return when {
            step <= 0 -> pc to vc
            abs(vc) < eps -> pc to vc
            vp * vc < 0.0 -> findPeregib(predt, pc, vp, vc, fdx, step - 1)
            vc * vn < 0.0 -> findPeregib(pc, nextt, vc, vn, fdx, step - 1)
            else -> pc to vc
        }
    }

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        val fx = com.takeBlock(1)?.innerLine.orEmpty()
        val fy = com.takeBlock(2)?.innerLine.orEmpty()
        var comi = 3
        val podValues = com.takeBlock(3)?.let { block ->
            if (block is TortoiseParserStackBlock && block.skobka == '['){
                comi++
                extractValues(block.innerLine, figureExtractor.memory)
            } else
                null
        }.orEmpty()
        com.takeBlock(comi)?.let { a ->
            val ts = figureExtractor.valueAt(a, 0, 0.0)
            val te = figureExtractor.valueAt(a, 1, 1.0)
            val co = figureExtractor.valueAt(a, 2, 30.0).toInt().coerceIn(1, 1000)

            if (fx.isNotEmpty() && fy.isNotEmpty()) {
                try {
                    val fxt = podstanovka(fx,podValues)
                    val fyt = podstanovka(fy,podValues)
                    val fdxt = diff("($fx)'t")
                    val fdyt = diff("($fy)'t")

                    val points = (0..co).mapNotNull { i ->
                        val t = ts + (te - ts) * i * 1.0 / co
                        val tv = parse(t)
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

                    buildLine(points, builder)
//                    if (points.size>=2) {

//                        builder.addProduct( FigureList(
//                            points.map { p ->
//                                FigureLine(p.first, p.first+p.second)
//                            }
//                        ))
//                        var predx = -1.0
//                        var predy = -1.0
//
//                        var po= mutableListOf<Vec2>()
//                        var po2= mutableListOf<Vec2>()
//                        for (i in 0 until points.size-1) {
//                            val pl = points[0 + i]
//                            val pr = points[1 + i]
//
//                            val d= pl.second //first-pl.first
//                            val sx =  Math.signum(d.x)
//                            val sy =  Math.signum(d.y)
//                            if (sx!= predx || sy!=predy){
//
//                                val t = ts + (te - ts) * i*1.0 / co
//                                val t2 = ts + (te - ts) * (i+1)*1.0 / co
//
//                                val py = findPeregib(t, t2, pl.second.y, pr.second.y, fdyt, MAX_STEP)
//
//                                if (sx != predx) {
//                                    val px =
//                                        findPeregib(t, t2, pl.second.x, pr.second.x, fdxt, MAX_STEP)
//                                    po2+= (Vec2(
//                                        calc(fxt, tt, parse("${px.first}")),
//                                        calc(fyt, tt, parse("${px.first}"))
//                                    )+  Vec2(
//                                        px.second,
//                                        calc(fdyt, tt, parse("${px.first}"))
//                                    ))
//
//                                }
//                                if (sy!=predy) {
//                                    po2+= (Vec2(
//                                        calc(fxt, tt, parse("${py.first}")),
//                                        calc(fyt, tt, parse("${py.first}"))
//                                    )+ Vec2(
//                                        calc(fdxt, tt, parse("${py.first}")),
//                                        py.second,
//                                    ))
//                                }
//                                po.add(pl.first)
//
//                                predx = sx
//                                predy = sy
//                            }
//
//                        }
//                        builder.addProduct( FigureList(
//                            po.map { p ->
//                                FigureCircle(p, 5.0, true)
//                            }
//                        ))
//
//                        builder.addProduct(
//                        FigureCreator.colorDxf(3,
//                            FigureList(
//                            po2.map { p ->
//                                FigureCircle(p, 3.0, true)
//                            }
//                        )))

//                        builder.addProduct(
//                            FigureCreator.colorDxf(
//                                6,
//                                FigurePolyline( listOf(points[0].first, points[0].first)+
//                                        points.flatMap{ listOf(it.first-it.second, it.first, it.first+it.second)}+
//                                        listOf(points.last().first, points.last().first)
//                                )
//                            )
//                        )

//                        val pt = mutableListOf<Vec2>()
//                        for (i in 0 until points.size-1){
//                            val pl = points[0+i]
//                            val pr = points[1+i]
//
//                            val d = Vec2.distance(pl.first,pr.first)
//                            if (d>0) {
//                                val ld = pl.second.magnitude
//                                val rd = pr.second.magnitude
//
//                                if (ld+rd>0) {
//                                    val norm = (ld + rd) / d
//                                    pt.addAll(listOf(
//                                        pl.first,
//                                        pl.first + pl.second,
//                                        pr.first- pr.second
//                                    )
//                                    )
//                                }
//                            }
//                        }
//                        pt.add(points.last().first)
//                        if (pt.size>=4) {
//                            builder.addProduct(
//
//
//                                FigureCreator.colorDxf(
//                                    3,
//                                    FigureBezier(
//                                        pt
//                                    )
//                                )
//                            )
//                        }
//                    }
                } catch (e: Exception) {

                }
            }
        }
    }

    fun buildLine(
        points: List<Pair<Vec2, Vec2>>,
        builder: TortoiseBuilder
    ) {
        if (points.size >= 2) {
            builder.addProduct(FigurePolyline(points.map { it.first }))
        }
    }

    fun calc(line: MathTerm, t: MathTerm, tvalue: Double): Double {
        return calc(line, t, parse("$tvalue"))
    }

    fun calc(line: MathTerm, t: MathTerm, tvalue: MathTerm): Double {
        val r =
            OutExpression.apply(Calculator.fullCalc(Replacement.replace(line, t, tvalue))) as String
        val s = if (r.startsWith("(") && r.endsWith(")")) {
            r.drop(1).dropLast(1)
        } else r
        // println(s)
        return s.toDoubleOrNull() ?: 0.0
    }

    fun calc(line: MathTerm): Double {
        val r =
            OutExpression.apply(Calculator.fullCalc(line)) as String
        val s = if (r.startsWith("(") && r.endsWith(")")) {
            r.drop(1).dropLast(1)
        } else r
        return s.toDoubleOrNull() ?: 0.0
    }

    fun parse(value: Double): MathTerm {
        return Calculator.parseWithSpace("$value")
    }

    fun parse(line: String): MathTerm {
        return Calculator.parseWithSpace(line)
    }

    fun calc(line: String): MathTerm {
        val dif = parse(line)
        return Calculator.fullCalc(CopositeFunction.compose(dif))
    }

    fun diff(line: String): MathTerm {
        val dif = parse(line)
        return Calculator.fullCalc(Calculator.diff(CopositeFunction.compose(dif)))
    }

    fun extractValues(values: String, memory: TortoiseMemory): List<Pair<MathTerm, MathTerm>>{
        try {
            val ss = values.split(';')
            val repl = ss.mapNotNull { t ->
                val ti = t.indexOf('=')
                if (ti > 0) {
                    val v = t.substring(0, ti)
                    val n = t.substring(ti + 1)
                    val n2 = if (n.startsWith("@")){
                        "${memory.value(MemoryKey(n.drop(1)), 0.0)}"
                    } else
                        n
                    parse(v) to parse(n2)
                } else
                    null
            }
            return repl
        }catch (e:Exception){
            return emptyList()
        }
    }

    fun podstanovka(line: String ,values:List<Pair<MathTerm, MathTerm>>): MathTerm {
        val dif = parse(line)
        return podstanovka(Calculator.fullCalc(dif), values)
    }

    fun podstanovka(line: MathTerm ,values:List<Pair<MathTerm, MathTerm>>): MathTerm {
        return if (values.isNotEmpty()) {
            values.reversed().fold(line) { d, v ->
                //  println("${v.first} : ${d}")
                Replacement.replace(d, v.first, v.second) }
        } else
            line
    }

//    private var memory : List<Pair<String, List<Pair<Vec2 , Vec2>>>>  =  mutableListOf()
//
//    fun findInMemory(block:TortoiseParserStackBlock){
//
//    }
}

