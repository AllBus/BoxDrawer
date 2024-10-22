package com.kos.boxdrawer.detal.splash

import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_1
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_2
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_NONE
import com.kos.figure.collections.FigurePoints
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData
import turtoise.help.HelpDataParam
import turtoise.parser.TPArg
import vectors.Vec2

class SplashPoints: ISplashDetail {
    override val names: List<String>
        get() = listOf("points")

    override fun help(): HelpData = HelpData(
        argument = "points radius px py *",
        description = "Рисование точек",
        params = listOf(
            HelpDataParam("radius", "Радиус", FIELD_1),
            HelpDataParam("px", "координаты точек по x", FIELD_2),
            HelpDataParam("py", "координаты точек по y", FIELD_NONE),
        ),
        creator = TPArg.create("points",
            TPArg("radius", FIELD_1),
            TPArg.oneOrMore("p",
                TPArg("xy", FIELD_2)
            )
        )
    )

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        val memory = figureExtractor.memory
        val r = com[1,10.0, memory]
        val coords = mutableListOf<Vec2>()
        for (i in 3 until com.size step 2) {
            val a = Vec2(
                com[i-1, memory],
                com[i, memory],
            ).rotate(builder.angle) + builder.xy
            coords+=a
        }
        builder.add(FigurePoints(coords.toList(), r))
    }
}