package com.kos.boxdrawer.detal.splash

import androidx.compose.ui.graphics.PointMode
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_2
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_FIGURE
import com.kos.figure.FigurePolyline
import com.kos.figure.algorithms.findIntersections
import com.kos.figure.composition.FigureTranslate
import turtoise.FigureCreator
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData
import turtoise.help.HelpDataParam
import turtoise.parser.TPArg
import vectors.Vec2

class SplashFigureAtPoints : ISplashDetail {

    override val names: List<String>
        get() = listOf("figureAtPoints", "atPoints")

    override fun help(): HelpData = HelpData(
        "figureAtPoints (figure) xy+", // Updated command syntax
        "Нарисовать фигуру в заданных точках",
        listOf(
            HelpDataParam(
                "figure", "Фигура",
                FIELD_FIGURE
            ),
            HelpDataParam(
                "xy",
                "Координаты точки",
                FIELD_2
            ),
        ),
        creator = TPArg.create(
            "figureAtPoints",
            TPArg.figure("figure"),
            TPArg.oneOrMore("points", TPArg("xy", FIELD_2))
        )
    )

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        com.takeBlock(1)?.let { block ->
            figureExtractor.figure(block)?.let { figure ->
                val memory = figureExtractor.memory

                val coordinates = (2 until com.size step 2).map { i ->
                    val x = com[i + 0, memory]
                    val y = com[i + 1, memory]
                    Vec2(x, y).rotate(builder.angle)
                }
                (if (coordinates.isEmpty()){
                    listOf(Vec2.Zero)
                }else {
                    coordinates
                }).forEach { point ->
                    builder.addProduct(FigureTranslate(figure, point))
                }
            }
        }
    }
}

class SplashIntersectFigures : ISplashDetail {
    override val names: List<String>
        get() = listOf("intersectFigures")

    override fun help(): HelpData = HelpData(
            "intersectFigures (figure1) (figure2)",
            "Найти точки пересечения двух фигур и нарисовать их",
            listOf(
                HelpDataParam(
                    "figure1", "Первая фигура", FIELD_FIGURE
                ),
                HelpDataParam(
                    "figure2", "Вторая фигура",
                    FIELD_FIGURE
                ),
            ),
            creator = TPArg.create(
                "intersectFigures",
                TPArg.figure("figure1"),
                TPArg.figure("figure2")
            )
        )

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        com.takeBlock(1)?.let { block1 ->
            com.takeBlock(2)?.let { block2 ->
                figureExtractor.figure(block1)?.let { figure1 ->
                    figureExtractor.figure(block2)?.let { figure2 ->
                        val path1 = figureExtractor.collectPaths(figure1)
                        val path2 = figureExtractor.collectPaths(figure2)

                        if (path1.isNotEmpty() && path2.isNotEmpty()){
                            val polygon1 = (path1[0] as? FigurePolyline)
                            val polygon2 = (path2[0] as? FigurePolyline)
                            if (polygon1 != null && polygon2 != null){
                                val intersectionPoints = findIntersections(polygon1.points, polygon2.points)
                                intersectionPoints.forEach { point ->
                                    builder.addProduct(FigureCreator.figurePoint(point, figureExtractor.ds))
                                }
                            }

                        }

                    }
                }
            }
        }
    }

}