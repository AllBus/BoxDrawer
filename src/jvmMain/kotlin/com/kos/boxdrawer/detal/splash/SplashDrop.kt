package com.kos.boxdrawer.detal.splash

import com.kos.figure.IFigurePath
import com.kos.figure.collections.FigureList
import com.kos.figure.composition.FigureWithPosition
import com.kos.figure.composition.PositionOnFigure
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData

open class SplashDrop : ISplashDetail {
    override val names: List<String>
        get() = listOf("drop")

    override fun help(): HelpData =  HelpData(
        "drop (figure) (edge offset width bias)+",
        "Нарисовать часть пути за исключением кусочков"
    )

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        com.takeBlock(1)?.let { block ->
            figureExtractor.figure(block)?.let { f ->
                val paths = figureExtractor.collectPaths(f)
                val edges = (2 until com.size).mapNotNull { j ->
                    com.takeBlock(j)?.let { item ->
                        val e = figureExtractor.valueAt(item, 0).toInt()
                        val x = figureExtractor.valueAt(item, 1)
                        val wi = figureExtractor.valueAt(item, 2, 15.0)
                        val bias = figureExtractor.valueAt(item, 3, 0.5)
                        PositionOnFigure(e, x, wi, bias)
                    }
                }

                var currentEdge = 0
                val fg = paths.mapIndexed { i, p ->
                    val pe = p.edgeCount()
                    val f = figureWithPosition(p, edges, currentEdge, pe)
                    currentEdge += pe
                    f
                }
                builder.addProduct(FigureList(fg))
            }
        }
    }

    open fun isDrop() = true

    private fun figureWithPosition(
        p: IFigurePath,
        edges: List<PositionOnFigure>,
        currentEdge: Int,
        pe: Int
    ) = FigureWithPosition(
        path = p,
        positions = edges.filter { it.edge >= currentEdge && it.edge < pe + currentEdge }.map {
            it.copy(edge = it.edge - currentEdge)
        },
        isDrop = isDrop(),
    )

}

class SplashTake: SplashDrop(){

    override val names: List<String>
        get() = listOf("take")

    override fun help(): HelpData = HelpData(
        "take (figure) (edge offset width bias)+",
        "Нарисовать часть пути только из заданных кусочков"
    )

    override fun isDrop(): Boolean = false
}