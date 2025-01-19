package com.kos.boxdrawer.detal.splash

import com.kos.figure.FigureLine
import com.kos.figure.FigurePolyline
import com.kos.figure.IFigure
import com.kos.figure.collections.toFigure
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData
import vectors.Vec2

class SplashTrapecija : ISplashDetail {
    override val names: List<String>
        get() = listOf("t")

    override fun help(): HelpData = HelpData(
        "t h tw bw offset?",
        "нарисовать трапецию"
    )

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        val h = -com[1, figureExtractor.memory]
        val tw = com[2, figureExtractor.memory]
        val bw = com[3, figureExtractor.memory]
        val offset = com[4, (bw-tw)/2.0,  figureExtractor.memory]

        val figures = mutableListOf<IFigure>()

        figures+=FigurePolyline(
            listOf(
                Vec2(0.0, h),
                Vec2(tw, h),
                Vec2(bw-offset, 0.0),
                Vec2(0-offset, 0.0),
            ),
            true
        )

        builder.addProduct(figures.toFigure())
    }
}