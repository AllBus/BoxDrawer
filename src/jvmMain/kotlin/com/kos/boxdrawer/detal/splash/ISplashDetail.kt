package com.kos.boxdrawer.detal.splash

import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData

interface ISplashDetail {
    val names: List<String>

    fun help(): HelpData

    fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor,
    )
}