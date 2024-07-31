package com.kos.boxdrawer.detal.splash

import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData
import turtoise.help.HelpDataParam

class SplashLine: ISplashDetail {
    override val names: List<String>
        get() = listOf("line")

    override fun help(): HelpData =  HelpData(
        "line (x y)+",
        "Нарисовать линию задавая сдвиг относительно предудыщей точки",
        listOf(

            HelpDataParam(
                "x",
                ""
            ),
            HelpDataParam(
                "y",
                ""
            ),
        )
    )

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        builder.startPoint()
        for (i in 1 until com.size step 2) {
            val a = com[i, figureExtractor.memory]
            val b = com[i + 1, figureExtractor.memory]
            builder.state.move(a, -b)
            builder.add(builder.xy)
        }
    }
}

