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

class SplashApproximationPolar: SplashApproximation(){
    override val names: List<String>
        get() = listOf("apolar", "apc" )

    override fun help(): HelpData {
        return HelpData(
            "apc (ro(t)) (fi(t)) [a=1;b=2]? (tstart tend count)",
            "Построить аппроксимацию фунции плоской кривой заданной в полярной системе координат через ломанную из point точек",
            listOf(
                HelpDataParam("ro(t)", "Функция координаты x от t", FIELD_TEXT),
                HelpDataParam("fi(t)", "Функция координаты y от t", FIELD_TEXT),
                HelpDataParam("repl", "Подстановка значений в выражение функций", FIELD_TEXT),
                HelpDataParam("tstart", "Точка начала", FIELD_2),
                HelpDataParam("tend", "Точка конца", FIELD_NONE),
                HelpDataParam("count", "Количество точек", FIELD_INT),

            ),
            creator = TPArg.create("ap",
                TPArg.block(TPArg("ro(t)", FIELD_TEXT)),
                TPArg.block(TPArg("fi(t)", FIELD_TEXT)),
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

        com.takeBlock(comi)?.let{ a ->
            val ts = figureExtractor.valueAt(a, 0, 0.0)
            val te = figureExtractor.valueAt(a, 1, 1.0)
            val co = figureExtractor.valueAt(a, 2, 30.0).toInt().coerceIn(1, 1000)
            if (fx.isNotEmpty() && fy.isNotEmpty()){
                try {
                    val fxt = podstanovka(fx,podValues)
                    val fyt = podstanovka(fy,podValues)
                    val fdxt = diff("($fx)'t")
                    val fdyt = diff("($fy)'t")

                    val points = (0..co).mapNotNull { i ->
                        val t = ts + (te - ts) * i*1.0 / co
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
                            Vec2.decartFromPolar(Vec2(x, y)) to
                                    Vec2.decartFromPolar(Vec2(dx, dy))
                        }
                    }
                    buildLine(points, builder)
                }catch (e:Exception){

                }
            }
        }
    }
}