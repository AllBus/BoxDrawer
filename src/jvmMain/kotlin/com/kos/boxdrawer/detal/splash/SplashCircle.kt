package com.kos.boxdrawer.detal.splash

import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_1
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_2
import turtoise.CornerInfo
import turtoise.FigureCreator
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData
import turtoise.help.HelpDataParam
import turtoise.parser.TPArg
import turtoise.parser.TortoiseParserStackBlock
import vectors.Vec2

class SplashCircle: ISplashDetail {
    override val names: List<String>
        get() = listOf("circle")

    override fun help(): HelpData {
        return HelpData(
            "circle ax ay ox oy cx cy r",
            "Построить дугу радиуса r из точки O между заданных радиус-векторов OA OC",
            params = listOf(
                HelpDataParam("a", "Координата начала дуги", FIELD_2),
                HelpDataParam("o", "Координата центра дуги", FIELD_2),
                HelpDataParam("c", "Координата концв дуги", FIELD_2),
                HelpDataParam("r", "радиус дуги", FIELD_1),
            ),
            creator = TortoiseParserStackBlock(' ', "circle", listOf(
                TPArg("a"),
                TPArg("o"),
                TPArg("c"),
                TPArg("r"),
            ))
        )
    }

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        val memory = figureExtractor.memory
        val a = Vec2(
            com[1, memory],
            com[2, memory],
        ).rotate(builder.angle)+builder.xy
        val o = Vec2(
            com[3, memory],
            com[4, memory],
        ).rotate(builder.angle)+builder.xy
        val b = Vec2(
            com[5, memory],
            com[6, memory],
        ).rotate(builder.angle)+builder.xy
        val r = com[7, memory]

        if (r!=0.0) {
            builder.add(FigureCreator.figureCircle(CornerInfo(a, b, o, true), r))
        }
    }
}