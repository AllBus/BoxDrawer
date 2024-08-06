package com.kos.boxdrawer.detal.splash

import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_1
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_FIGURE
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_INT
import com.kos.figure.FigureLine
import com.kos.figure.collections.FigureList
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData
import turtoise.help.HelpDataParam
import turtoise.parser.TPArg

class SplashNormal: ISplashDetail {
    override val names: List<String>
        get() = listOf("normal")

    override fun help(): HelpData {
        return HelpData(
            "normal (figure) c l o",
            "Нарисовать нормали по перимеру фигуры",
            listOf(
                HelpDataParam(
                    "figure",
                    "Фигура", FIELD_FIGURE
                ),
                HelpDataParam(
                    "c",
                    "количество раз", FIELD_INT

                ),
                HelpDataParam(
                    "l",
                    "Длина линии нормали"
                ),
                HelpDataParam(
                    "o",
                    "Отступ от фигуры"
                )
            ),
            creator = TPArg.create("normal",
                TPArg.figure("figure"),
                TPArg("c", FIELD_INT),
                TPArg("l", FIELD_1),
                TPArg("o", FIELD_1),
                )
        )
    }

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) { val memory = figureExtractor.memory

        com.takeBlock(1)?.let { block ->
            figureExtractor.figure(block)?.let { f ->
                val paths = figureExtractor.collectPaths(f)

                val c = com[2, 1.0, memory].toInt()
                val l = com[3, 1.0, memory]
                val o = com[4, 0.0, memory]
                val figures = paths.flatMap { p ->
                    if (c <= 1) {
                        listOf(0.5)
                    } else {
                        val cc = c - 1
                        (0 until c).map { i -> i * 1.0 / cc }
                    }.flatMap { i ->
                        (0 until p.edgeCount()).map { e ->
                            p.positionInPath(edge = e, delta = i)
                        }
                    }.map { p ->
                        FigureLine(p.point + p.normal * o, p.point + p.normal * (o + l))
                    }
                }
                builder.addProduct(FigureList(figures))
            }
        }
    }
}