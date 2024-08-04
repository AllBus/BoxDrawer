package com.kos.boxdrawer.detal.splash

import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_1
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_2
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_ANGLE
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_NONE
import turtoise.FigureCreator
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData
import turtoise.help.HelpDataParam
import turtoise.parser.TPArg
import vectors.Vec2

class SplashRound: ISplashDetail {
    override val names: List<String>
        get() = listOf("round")

    override fun help(): HelpData = HelpData(
        argument = "round x y r + x y",
        description = "Нарисовать линию со скруглениями углов",
        params = listOf(
            HelpDataParam("x", "Координата x точки относительно предыдущей точки", FIELD_2),
            HelpDataParam("y", "Координата y точки относительно предыдущей точки", FIELD_NONE),
            HelpDataParam("r", "Радиус скругления", FIELD_1),
        ),
        creator = TPArg.create("round",
            TPArg.oneOrMore(
                TPArg("x"),
                TPArg("r"),
            )
        )
    )

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        val s = com.size
        val points = mutableListOf<Vec2>()
        val radius = mutableListOf<Double>()
        var ei =  0
        val a =builder.angle
        points.add(builder.xy)

        for (i in 1 until s-2 step  3){
            points.add(
                points.last()+ Vec2(
                    com[i + 0, figureExtractor.memory],
                    com[i + 1, figureExtractor.memory]
                ).rotate(a)
            )
            radius.add( com[i+2, figureExtractor.memory])
            ei = i
        }
        if ((s-1) %3!=0) {
            points.add(
                points.last() + Vec2(
                    com[ei + 3, figureExtractor.memory],
                    com[ei + 4, figureExtractor.memory]
                ).rotate(a)
            )
        }
        //println(">>>> ${radius.joinToString(", ")}")
        //println(">> ${points.joinToString(", ")}")

        val pp = points
        if (pp.size>=2) {
            builder.add(FigureCreator.roundedLine(pp, radius))
            builder.state.moveTo(pp.last())
        }
    }
}