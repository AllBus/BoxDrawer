package com.kos.boxdrawer.detal.splash

import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_2
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_ANGLE
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_FIGURE
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_INT
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_NONE
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData
import turtoise.help.HelpDataParam
import turtoise.parser.TPArg
import vectors.Vec2

class SplashEdit : ISplashDetail {
    override val names: List<String>
        get() = listOf("edit", "e")

    override fun help(): HelpData = HelpData(
        "edit (figure) (m e x y a)* (r e+)",
        "Редактировать фигуру",
        listOf(
            HelpDataParam(
                "figure",
                "Фигура",
                FIELD_FIGURE
            ),
            HelpDataParam(
                "e",
                "", FIELD_INT
            ),
            HelpDataParam(
                "x",
                "", FIELD_2
            ),
            HelpDataParam(
                "y",
                "", FIELD_NONE
            ),
            HelpDataParam(
                "a",
                "", FIELD_ANGLE
            ),
        ),
        creator = TPArg.create(
            "edit",
            TPArg.figure("figure"),
            TPArg.multiVariant(
                "mul",
                TPArg.item(
                    "m",
                    TPArg("e", FIELD_INT),
                    TPArg("xy", FIELD_2),
                    TPArg("a", FIELD_ANGLE),
                ),
            ),
            TPArg.item(
                "r",
                TPArg.oneOrMore(
                    "re",
                    TPArg("e", FIELD_INT),
                )
            ),
        )
    )

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        com.takeBlock(1)?.let { block ->
            figureExtractor.figure(block)?.let { f ->
                figureExtractor.collectPolygons(f).firstOrNull()?.let { path ->
                    val points = path.points.toMutableList()
                    (2 until com.size).mapNotNull { j ->
                        com.takeBlock(j)?.let { item ->
                            when (item.name.name) {
                                "m" -> {
                                    val e = figureExtractor.valueAt(item, 1).toInt()
                                    val x = figureExtractor.valueAt(item, 2)
                                    val y = figureExtractor.valueAt(item, 3)
                                    val a = figureExtractor.valueAt(item, 4)
                                    if (e >= 0 && e < points.size) {
                                        points[e] = points[e] + Vec2(x, y).rotate(a)
                                    }
                                }

                                "r" -> {
                                    (1 until item.size).map { i ->
                                        figureExtractor.valueAt(item, i).toInt()
                                    }.sorted().reversed().forEach { ind ->
                                        if (ind >= 0 && ind < points.size) {
                                            points.removeAt(ind)
                                        }
                                    }
                                }

                                else -> {}
                            }
                        }
                    }
                    builder.addProduct(path.create(points.toList()))
                }
            }
        }
    }
}