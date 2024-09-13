package com.kos.boxdrawer.detal.splash

import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_2
import turtoise.FigureCreator
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData
import turtoise.help.HelpDataParam
import turtoise.parser.TPArg
import vectors.Vec2

class SplashPoint : ISplashDetail {
    override val names: List<String>
        get() = listOf("point")

    override fun help(): HelpData = HelpData(
        "points xy xy ...",
        "Нарисовать несколько точек с заданными координатами",
        params = listOf(
            HelpDataParam("xy", "Координаты точки", FIELD_2),
        ),
        creator = TPArg.create(
            "points",
            TPArg.oneOrMore(
                "1",
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
        val coordinates =  (1 until com.size step 2).map{ i ->
            val x = com[i+0, memory]
            val y = com[i+1, memory]
            Vec2(x, y).rotate(builder.angle) + builder.xy
        }

        coordinates.forEach { point ->
            builder.add(FigureCreator.figurePoint(point, figureExtractor.ds))
        }
    }
}