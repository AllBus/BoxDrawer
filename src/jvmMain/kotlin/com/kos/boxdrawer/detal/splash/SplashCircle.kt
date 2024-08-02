package com.kos.boxdrawer.detal.splash

import turtoise.CornerInfo
import turtoise.FigureCreator
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData
import vectors.Vec2

class SplashCircle: ISplashDetail {
    override val names: List<String>
        get() = listOf("circle")

    override fun help(): HelpData {
        return HelpData(
            "c ax ay ox oy cx cy r",
            "Построить дугу радиуса r из точки O между заданноых радиус векторов OA OC"
        )
    }

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        val memory = figureExtractor.memory
        val a = Vec2(
            com[1, memory],
            com[2, memory],
        ).rotate(builder.angle)+builder.xy
        val o = Vec2(
            com[3, memory],
            com[4, memory],
        ).rotate(builder.angle)+builder.xy
        val b = Vec2(
            com[5, memory],
            com[6, memory],
        ).rotate(builder.angle)+builder.xy
        val r = com[7, memory]

        if (r!=0.0) {
            builder.add(FigureCreator.figureCircle(CornerInfo(a, b, o, true), r))
        }
    }
}