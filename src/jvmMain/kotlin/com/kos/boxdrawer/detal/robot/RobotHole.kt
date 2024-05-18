package com.kos.boxdrawer.detal.robot

import turtoise.*
import turtoise.help.HelpInfoCommand
import turtoise.memory.keys.DoubleMemoryKey
import turtoise.memory.keys.MemoryKey
import turtoise.memory.keys.MemoryKey.Companion.ifEmpty
import turtoise.parser.TortoiseParser
import turtoise.parser.TortoiseParserStackItem

class RobotHole(
    val width: MemoryKey,
    val height: MemoryKey,
): IRobotCommand {
    override fun draw(ds: DrawerSettings): TortoiseBlock {
        return TortoiseBlock(
            listOf(
                TortoiseCommand.Rectangle(width, height.ifEmpty { DoubleMemoryKey(ds.holeWeight) })
            )
        )
    }

    object Factory: IRobotCommandFactory{
        override fun create(args: List<MemoryKey>, item: TortoiseParserStackItem): IRobotCommand {
            return RobotHole(
                args.getOrElse(0) { MemoryKey.EMPTY },
                args.getOrElse(1) { MemoryKey.EMPTY }
            )
        }

        override val names: List<String>
            get() = listOf("h", "hole")

        override fun help(): HelpInfoCommand {
            return TortoiseParser.helpName("h", "hw hh", "прямоугольник отверстия шириной hw  высотой hh")
        }
    }
}