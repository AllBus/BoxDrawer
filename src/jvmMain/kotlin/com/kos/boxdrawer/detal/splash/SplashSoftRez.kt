package com.kos.boxdrawer.detal.splash

import com.kos.boxdrawer.detal.soft.SoftRez
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData

class SplashSoftRez: ISplashDetail {
    override val names: List<String>
        get() = listOf("rez")

    override fun help(): HelpData =  HelpData(
        "rez width height delta dlina soedinenie isFirstSmall",
        "Нарисовать мягкий рез"
    )

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        val memory = figureExtractor.memory
        val width = com[1, memory]
        val height = com[2, memory]
        val delta = com[3, 5.2, memory]
        val dlina = com[4, 18.0, memory]
        val soedinenie = com[5, 6.0, memory]
        val firstSmall = com[6, memory]

        val figures = SoftRez().drawRez(
            width,
            height,
            delta,
            dlina,
            soedinenie,
            firstSmall > 0
        )
        builder.addProduct(figures)
    }
}