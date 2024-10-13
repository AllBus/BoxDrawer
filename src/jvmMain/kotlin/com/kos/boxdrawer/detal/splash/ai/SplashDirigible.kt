package com.kos.boxdrawer.detal.splash.ai

import com.kos.boxdrawer.detal.splash.ISplashDetail
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_2
import com.kos.figure.FigureBezier
import com.kos.figure.FigureLine
import turtoise.help.HelpData
import turtoise.help.HelpDataParam
import turtoise.parser.TPArg
import vectors.Vec2
import kotlin.math.PI
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor

class SplashDirigible : ISplashDetail {
    override val names: List<String>
        get() = listOf("dirigible")

    override fun help(): HelpData {
        return HelpData(
            "dirigible ax ay bx by", "Построить дирижабль",
            params = listOf(
                HelpDataParam("a", "Координата начала дирижабля", FIELD_2),
                HelpDataParam("b", "Координата конца дирижабля", FIELD_2),
            ),
            creator = TPArg.create(
                "dirigible",
                TPArg("a", FIELD_2),
                TPArg("b", FIELD_2),
            )
        )
    }

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand, figureExtractor: TortoiseFigureExtractor
    ) {
        val memory = figureExtractor.memory
        val a = Vec2(
            com[1, memory],
            com[2, memory],
        ).rotate(builder.angle) + builder.xy
        val b = Vec2(
            com[3, memory],
            com[4, memory],
        ).rotate(builder.angle) + builder.xy

        val width = (b - a).magnitude * 0.2 // Adjust width as needed
        val height = width * 0.5 // Adjust height as needed

        val center = (a + b) / 2.0
        val direction = (b - a).normalize()
        val perpendicular = direction.rotate(PI / 2)

        val p1 = center - direction * width / 2.0 + perpendicular * height
        val p2 = center + direction * width / 2.0 + perpendicular * height
        val p3 = center + direction * width / 2.0 - perpendicular * height
        val p4 = center - direction * width / 2.0 - perpendicular * height

        // Draw the dirigible body
        builder.add(FigureBezier(listOf(a, p1, p2, b)))
        builder.add(FigureBezier(listOf(b, p3, p4, a)))

        // Draw the gondola (optional)
        val gondolaWidth = width * 0.3
        val gondolaHeight = height * 0.2
        val gondolaCenter = center - perpendicular * height * 0.6
        val gondolaP1 = gondolaCenter - direction * gondolaWidth / 2.0 + perpendicular * gondolaHeight
        val gondolaP2 = gondolaCenter + direction * gondolaWidth / 2.0 + perpendicular * gondolaHeight
        val gondolaP3 = gondolaCenter + direction * gondolaWidth / 2.0 - perpendicular * gondolaHeight
        val gondolaP4 = gondolaCenter - direction * gondolaWidth / 2.0 - perpendicular * gondolaHeight
        builder.add(FigureLine(gondolaP1, gondolaP2))
        builder.add(FigureLine(gondolaP2, gondolaP3))
        builder.add(FigureLine(gondolaP3, gondolaP4))
        builder.add(FigureLine(gondolaP4, gondolaP1))
    }
}