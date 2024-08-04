package com.kos.boxdrawer.detal.splash

import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_FIGURE
import com.kos.figure.collections.FigureList
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData
import turtoise.help.HelpDataParam
import turtoise.parser.TPArg

class SplashObruch : ISplashDetail {
    override val names: List<String>
        get() = listOf("o")

    override fun help(): HelpData = HelpData(
        "o (figure) h+",
        "Нарисовать фигуру сдвунутую на h от текущей фигуры",
        listOf(
            HelpDataParam(
                "figure",
                "Фигура",
                FIELD_FIGURE
            ),
            HelpDataParam(
                "h",
                ""
            ),
        ),
        creator = TPArg.create(
            "o",
            TPArg.figure("figure"),
            TPArg.oneOrMore(
                TPArg("h"),
            )
        )
    )

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        com.takeBlock(1)?.let { block ->
            figureExtractor.figure(block)?.let { f ->
                val paths = figureExtractor.collectPaths(f)
                if (paths.isNotEmpty()) {
                    (2 until com.size).map { ind ->
                        val h = com.takeBlock(ind)?.let { item ->
                            figureExtractor.valueAt(item, 0, figureExtractor.ds.boardWeight)
                        } ?: figureExtractor.ds.boardWeight
                        builder.addProduct(
                            FigureList(
                                paths.map { p ->
                                    p.duplicationAtNormal(h)
                                }
                            )
                        )
                    }
                }
            }
        }
    }
}