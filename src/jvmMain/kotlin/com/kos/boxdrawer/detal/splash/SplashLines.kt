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

class SplashLines : ISplashDetail {
    override val names: List<String>
        get() = listOf("v")

    override fun help(): HelpData = HelpData(
        "v (x h)*",
        "нарисовать последоавтельность вертикальных линий"
    )

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        val figures = mutableListOf<IFigure>()
        val res = mutableListOf<Vec2>()
        for (i in 1 until com.size step 2) {
            val a = com[i, figureExtractor.memory]
            val b = -com[i + 1, figureExtractor.memory]
            figures+=FigureLine(Vec2(a, 0.0), Vec2(a, b))
            res+=Vec2(a, b)
         }
        if (res.size>=2) {
            figures+=FigurePolyline(res)
        }
        builder.addProduct(figures.toFigure())
    }
}