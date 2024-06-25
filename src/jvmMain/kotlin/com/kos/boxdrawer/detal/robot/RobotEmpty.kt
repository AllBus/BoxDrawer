package com.kos.boxdrawer.detal.robot

import turtoise.DrawerSettings
import turtoise.TortoiseBlock
import turtoise.help.HelpInfoCommand
import turtoise.memory.keys.MemoryKey
import turtoise.parser.TortoiseParserStackItem

class RobotEmpty() : IRobotCommand {
    override fun draw(ds: DrawerSettings): TortoiseBlock {
        return TortoiseBlock(emptyList())
    }

    object Factory : IRobotCommandFactory {
        override fun create(args: List<MemoryKey>, item: TortoiseParserStackItem): IRobotCommand {
            return RobotEmpty()
        }

        override val names: List<String>
            get() = listOf("")

        override fun help(): HelpInfoCommand {
            return HelpInfoCommand("", emptyList())
        }
    }
}