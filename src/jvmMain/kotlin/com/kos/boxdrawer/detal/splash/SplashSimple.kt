package com.kos.boxdrawer.detal.splash

import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_FIGURE
import com.kos.figure.complex.FigureSimplefication
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData
import turtoise.help.HelpDataParam
import turtoise.parser.TPArg

class SplashSimple: ISplashDetail {
    override val names: List<String>
        get() = listOf("simple")

    override fun help(): HelpData = HelpData(
        "simple",
        "Упрощённое отображение детали",
        listOf(
            HelpDataParam(
                "figure",
                "Фигура",
                FIELD_FIGURE
            ),
        ),
        creator = TPArg.create(
            "simple",
            TPArg.figure("figure"),
        )
    )

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        figureExtractor.figure(com.takeBlock(1))?.let { f ->
            builder.addProduct(FigureSimplefication(f))
        }
    }
}