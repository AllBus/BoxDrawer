package com.kos.boxdrawer.detal.splash

import com.jsevy.jdxf.DXFColor
import com.kos.figure.FigurePolyline
import com.kos.figure.collections.FigureList
import com.kos.figure.composition.FigureColor
import org.jetbrains.skia.Color
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.TortoiseFigureExtractor.Companion.pathAtIndex
import turtoise.TortoiseFigureExtractor.Companion.positionInPath
import turtoise.help.HelpData
import turtoise.memory.keys.MemoryKey
import vectors.Vec2
import kotlin.math.PI

class SplashPaz: ISplashDetail {
    override val names: List<String>
        get() = listOf("paz")

    override fun help(): HelpData =  HelpData(
        "paz (figure) (edge delta le he)+",
        "Нарисовать пазы вдоль пути figure"
    )

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        val memory = figureExtractor.memory
        /* (Figure) (edge delta le he)* */
        com.takeBlock(1)?.let { block ->
            figureExtractor.figure(block)?.let { f ->
                val paths = figureExtractor.collectPaths(f)
                val v = (2 until com.size).mapNotNull { j ->
                    com.takeBlock(j)?.let { item ->
                        val e = memory.value(item.get(0) ?: MemoryKey.ZERO, 0.0)
                        if (item.get(1) == null) {
                            FigureColor(
                                Color.GREEN,
                                DXFColor.getClosestDXFColor(Color.GREEN),
                                pathAtIndex(paths, e.toInt()).toFigure()
                            )

                        } else {
                            val d = memory.value(item.get(1) ?: MemoryKey.ZERO, 0.0)
                            val zigle = item.get(2)?.let { memory.value(it, 15.0) } ?: 15.0
                            val zighe =
                                item.get(3)?.let { memory.value(it, figureExtractor.ds.boardWeight) }
                                    ?: figureExtractor.ds.boardWeight
                            positionInPath(paths, e.toInt(), d)?.let { pos ->
                                FigurePolyline(
                                    listOf(
                                        Vec2(-zigle, 0.0),
                                        Vec2(-zigle, zighe),
                                        Vec2(zigle, zighe),
                                        Vec2(zigle, 0.0),
                                    ).map { pos.point + it.rotate(pos.normal.angle + PI / 2) }
                                )
                            }
                        }
                    }
                }
                builder.addProduct(FigureList(v + f))
            }
        }
    }
}