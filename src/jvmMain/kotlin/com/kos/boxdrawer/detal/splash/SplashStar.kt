package com.kos.boxdrawer.detal.splash

import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_1
import com.kos.figure.FigurePolyline
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData
import turtoise.help.HelpDataParam
import turtoise.parser.TPArg
import vectors.Vec2
import kotlin.math.PI

class SplashStar : ISplashDetail {
    override val names: List<String>
        get() = listOf("star")

    override fun help(): HelpData = HelpData(
        "star с r (rm fi)*",
        "Построить звезду с радиусами r и rm",
        params = listOf(
            HelpDataParam("c", "Количество лучей звезды", FIELD_1),
            HelpDataParam("r", "радиус звезды", FIELD_1),
            HelpDataParam("rm", "радиус внутренней части звезды", FIELD_1),
            HelpDataParam("fi", "поворот внутренних точек относитльно внешней", FIELD_1),
        ),
        creator = TPArg.create(
            "star",
            TPArg("c", FIELD_1),
            TPArg("r", FIELD_1),
            TPArg.oneOrMore("next",
                TPArg("rm", FIELD_1),
                TPArg("fi", FIELD_1),
            )
        )
    )

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        val memory = figureExtractor.memory
        val count = com[1, 5.0, memory].toInt()
        val r = com[2, memory]
        val infos = mutableListOf<StarInfo>()
        for (i in 3 until com.size step 2) {
            val ri = com[i, memory]
            val fi =  Math.toRadians(com[i + 1, memory])
            infos+=StarInfo(ri, fi)
        }

        val result = mutableListOf<Vec2>()
        for (i in 0 until count) {
            val angle = (i * 2* PI / count)
            val x = r * kotlin.math.cos(angle )
            val y = r * kotlin.math.sin(angle)
            val p = Vec2(x, y)
            result.add(p)
            var apangle = angle
            for (info in infos) {
                val rm = info.radius
                apangle += info.fi
                val xm = rm * kotlin.math.cos(apangle)
                val ym = rm * kotlin.math.sin(apangle)
                val mp = Vec2(xm, ym)
                result.add(mp)
            }
        }
        builder.addProduct(FigurePolyline(result, true))
    }

    data class StarInfo(val radius:Double, val fi:Double)
}