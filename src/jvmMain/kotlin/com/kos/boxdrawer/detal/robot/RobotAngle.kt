package com.kos.boxdrawer.detal.robot

import turtoise.*
import turtoise.help.HelpInfoCommand
import turtoise.memory.keys.MemoryKey
import turtoise.parser.TortoiseParser
import turtoise.parser.TortoiseParserStackItem

class RobotAngle(
    val angle: MemoryKey,
): IRobotCommand {
    override fun draw(ds: DrawerSettings): TortoiseBlock {
        return TortoiseBlock(
            listOf(
                TortoiseCommand.Angle(angle)
            )
        )
    }

    object Factory: IRobotCommandFactory {
        override fun create(args: List<MemoryKey>, item: TortoiseParserStackItem): IRobotCommand {
            return RobotAngle(args.getOrElse(0) { MemoryKey.EMPTY })
        }

        override val names: List<String>
            get() = listOf("a")

        override fun help(): HelpInfoCommand {
            return TortoiseParser.helpName("a", "a", "повернуть направление движение на угол a ")
        }
    }
}