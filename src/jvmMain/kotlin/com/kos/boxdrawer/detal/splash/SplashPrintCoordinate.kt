package com.kos.boxdrawer.detal.splash

import com.kos.figure.FigureText
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData

class SplashPrintCoordinate:ISplashDetail {
    override val names: List<String>
        get() = listOf(
            "printcoord",
            "printc",
            "coord",
            "coordinate",
            "print_coord",
            "print_coordinate"
        )

    override fun help() = HelpData(
    "printс x",
    "Написать текущую координату"
    )


    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        builder.addProduct(FigureText(builder.xy.toString()))
    }
}