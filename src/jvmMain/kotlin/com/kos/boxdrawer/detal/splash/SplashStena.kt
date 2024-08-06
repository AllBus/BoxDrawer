package com.kos.boxdrawer.detal.splash

import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_1
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_FIGURE
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_INT
import com.kos.figure.FigurePolyline
import com.kos.figure.collections.FigureList
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData
import turtoise.help.HelpDataParam
import turtoise.parser.TPArg
import vectors.Vec2

class SplashStena : ISplashDetail {
    override val names: List<String>
        get() = listOf("stena")

    override fun help(): HelpData = HelpData(
        "stena (Figure) (he we) (edge hee?)*",
        "Нарисовать стенки вдоль пути Figure",
        listOf(
            HelpDataParam(
                "figure",
                "Фигура",
                FIELD_FIGURE
            ),
            HelpDataParam(
                "he",
                "", FIELD_1
            ),
            HelpDataParam(
                "we",
                "", FIELD_1
            ),
            HelpDataParam(
                "edge",
                "", FIELD_INT
            ),
            HelpDataParam(
                "hee",
                "", FIELD_1
            ),
        ),
        creator = TPArg.create(
            "stena",
            TPArg.figure("figure"),
            TPArg.block(
                TPArg("he"),
                TPArg("we"),
            ),
            TPArg.oneOrMore(
                TPArg.block(
                    TPArg("edge"),
                    TPArg.noneOrOne(
                        TPArg("hee")
                    ),
                )
            )
        )
    )

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        val memory = figureExtractor.memory
        /* (Figure) he (edge he)* */
        com.takeBlock(1)?.let { block ->
            figureExtractor.figure(block)?.let { f ->
                val paths = figureExtractor.collectPaths(f)
                val (h, we) = com.takeBlock(2)?.let { item ->
                    val h = (item.get(0)?.let { im -> memory.value(im, 0.0) } ?: 10.0)
                    val w = (item.get(1)?.let { im ->
                        memory.value(im, figureExtractor.ds.boardWeight)
                    } ?: figureExtractor.ds.boardWeight)
                    h to w
                } ?: (10.0 to figureExtractor.ds.boardWeight)

                val heights = (3 until com.size).mapNotNull { j ->
                    com.takeBlock(j)?.let { item ->
                        val e = (item.get(0)?.let { memory.value(it, 0.0) } ?: 0.0).toInt()
                        val h1 = (item.get(1)?.let { memory.value(it, 0.0) } ?: 0.0)
                        e to h1
                    }
                }.toMap()

                var xc = 0.0
                var edge = 0
                val v = paths.flatMap { path ->
                    val er = edge
                    edge += path.edgeCount()
                    (0 until path.edgeCount()).map { e ->
                        val he = heights.get(e + er) ?: h
                        val le = path.pathLength(e)
                        val f = FigurePolyline(
                            listOf(
                                Vec2(xc, 0.0),
                                Vec2(xc + le, 0.0),
                                Vec2(xc + le, he),
                                Vec2(xc, he),
                            ),
                            close = true
                        )
                        xc += le
                        f
                    }
                }

                builder.addProduct(FigureList(v))
            }
        }
    }
}