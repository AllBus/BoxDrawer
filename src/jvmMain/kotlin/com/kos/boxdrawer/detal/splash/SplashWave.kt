package com.kos.boxdrawer.detal.splash

import com.kos.boxdrawer.presentation.tabbar.TabBarPreview
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_1
import com.kos.figure.FigureBezier
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData
import turtoise.help.HelpDataParam
import turtoise.parser.TPArg
import vectors.Vec2

class SplashWave : ISplashDetail {
    override val names: List<String>
        get() = listOf("волна")

    override fun help(): HelpData = HelpData(
        "волна w h c",
        "Построить волну",
        params = listOf(
            HelpDataParam("w", "Ширина волны", FIELD_1),
            HelpDataParam("h", "Высота волны", FIELD_1),
            HelpDataParam("c", "Количество волн", FIELD_1),
        ),
        creator = TPArg.create(
            "волна",
            TPArg("w", FIELD_1),
            TPArg("h", FIELD_1),
            TPArg("c", FIELD_1),
        )
    )

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        val memory = figureExtractor.memory
        val w = com[1, memory]
        val h = com[2, memory]
        val c = com[3, memory].toInt()

        for (i in 0 until c) {
            val x = i * w
            val y = h * Math.sin(x)
            val bezier = FigureBezier(listOf(Vec2(x, 0.0), Vec2(x, y), Vec2(x + w, y), Vec2(x + w, 0.0)))
            builder.addProduct(bezier)
        }
    }
}