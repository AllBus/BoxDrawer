package com.kos.boxdrawer.detal.splash

import turtoise.DrawerSettings
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseRunner
import turtoise.help.HelpInfoCommand
import turtoise.memory.TortoiseMemory

interface ISplashDetail {
    val names: List<String>

    fun help(): HelpInfoCommand

    fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        ds: DrawerSettings,
        maxStackSize: Int,
        memory: TortoiseMemory,
        runner: TortoiseRunner,
    )
}