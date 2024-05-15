package com.kos.boxdrawer.detal.robot

import turtoise.*
import turtoise.help.HelpInfoCommand
import turtoise.memory.keys.MemoryKey

class RobotMove(
    val x: MemoryKey,
    val y: MemoryKey,
): IRobotCommand {
    override fun draw(ds: DrawerSettings): TortoiseBlock {
        return TortoiseBlock(
            listOf(
                TortoiseCommand.Move(x, y)
            )
        )
    }

    object Factory: IRobotCommandFactory {
        override fun create(args: List<MemoryKey>, item: TurtoiseParserStackItem): IRobotCommand {
            return RobotMove(
                args.getOrElse(0) { MemoryKey.EMPTY },
                args.getOrElse(1) { MemoryKey.EMPTY }
            )
        }

        override val names: List<String>
            get() = listOf("m")

        override fun help(): HelpInfoCommand {
            return  TortoiseParser.helpName("m", "x y", "переместить позицию")
        }
    }
}