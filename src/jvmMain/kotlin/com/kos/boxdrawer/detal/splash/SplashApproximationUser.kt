package com.kos.boxdrawer.detal.splash

import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_2
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_INT
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_NONE
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_TEXT
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData
import turtoise.help.HelpDataParam
import turtoise.parser.TPArg
import turtoise.parser.TortoiseParserStackBlock
import vectors.Vec2

class SplashApproximationUser : SplashApproximation() {
    override val names: List<String>
        get() = listOf("apx")

    override fun help(): HelpData {
        return HelpData(
            "apx (f(t)) (g(t)) (x(f,g)) (y(f, g)) [a=1;b=2]? (tstart tend count)",
            "Построить аппроксимацию фунции плоской кривой заданной в полярной системе координат через ломанную из point точек",
            listOf(
                HelpDataParam("f(t)", "Функция координаты f от t", FIELD_TEXT),
                HelpDataParam("g(t)", "Функция координаты g от t", FIELD_TEXT),
                HelpDataParam(
                    "x(f,g)",
                    "Функция системы координат координата x от f g",
                    FIELD_TEXT
                ),
                HelpDataParam(
                    "y(f,g)",
                    "Функция системы координат координата y от f g",
                    FIELD_TEXT
                ),
                HelpDataParam("repl", "Подстановка значений в выражение функций", FIELD_TEXT),
                HelpDataParam("tstart", "Точка начала", FIELD_2),
                HelpDataParam("tend", "Точка конца", FIELD_NONE),
                HelpDataParam("count", "Количество точек", FIELD_INT),

                ),
            creator = TPArg.create(
                "apx",
                TPArg.block(TPArg("f(t)", FIELD_TEXT)),
                TPArg.block(TPArg("g(t)", FIELD_TEXT)),
                TPArg.block(TPArg("x(f,g)", FIELD_TEXT)),
                TPArg.block(TPArg("y(f,g)", FIELD_TEXT)),
                TPArg.noneOrOne(
                    "app",
                    TPArg.blockBrace('[', TPArg("repl", FIELD_TEXT))
                ),
                TPArg.block(
                    TPArg("tse", FIELD_2),
                    TPArg("count", FIELD_INT)
                ),
            )
        )
    }

    val pf = parse("f")
    val pg = parse("g")

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        val f = com.takeBlock(1)?.innerLine.orEmpty()
        val g = com.takeBlock(2)?.innerLine.orEmpty()
        val xfg = com.takeBlock(3)?.innerLine.orEmpty()
        val yfg = com.takeBlock(4)?.innerLine.orEmpty()

        var comi = 5
        val podValues = com.takeBlock(5)?.let { block ->
            if (block is TortoiseParserStackBlock && block.skobka == '[') {
                comi++
                extractValues(block.innerLine, figureExtractor.memory)
            } else
                null
        }.orEmpty()

        com.takeBlock(comi)?.let { a ->
            val ts = figureExtractor.valueAt(a, 0, 0.0)
            val te = figureExtractor.valueAt(a, 1, 1.0)
            val co = figureExtractor.valueAt(a, 2, 30.0).toInt().coerceIn(1, 1000)
            if (f.isNotEmpty() && g.isNotEmpty() && xfg.isNotEmpty() && yfg.isNotEmpty()) {
                try {
                    val fxt = podstanovka(f, podValues)
                    val fyt = podstanovka(g, podValues)
                    val xfgt = calc(xfg)
                    val yfgt = calc(yfg)

                    val fdxt = diff("($fxt)'t")
                    val fdyt = diff("($fyt)'t")

                    println("${fxt} --> ${fyt}")
                    println("${xfgt} ->> ${yfgt}")
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

                            val ar = listOf(pf to parse(x), pg to parse(y))
                            val xt = calc(podstanovka(xfgt, ar))
                            val yt = calc(podstanovka(yfgt, ar))

                            val ard = listOf(pf to parse(dx), pg to parse(dy))
                            val xdt = calc(podstanovka(xfgt, ard))
                            val ydt = calc(podstanovka(yfgt, ard))

                            println("${x} -> ${xt} ${y} -> ${yt}")

                            if (!xt.isFinite() || !yt.isFinite())
                                null
                            else
                                Vec2(xt, yt) to Vec2(xdt, ydt)

                        }
                    }
                    buildLine(points, builder)
                } catch (e: Exception) {

                }
            }
        }
    }
}