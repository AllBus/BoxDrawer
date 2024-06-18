package com.kos.boxdrawer.detal.splash

import turtoise.DrawerSettings
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.TortoiseRunner
import turtoise.help.HelpData
import turtoise.help.HelpInfoCommand
import turtoise.memory.TortoiseMemory

interface ISplashDetail {
    val names: List<String>

    fun help(): HelpData

    fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor,
    )
}