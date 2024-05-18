package com.kos.boxdrawer.detal.robot

import turtoise.*
import turtoise.help.HelpInfoCommand
import turtoise.memory.keys.DoubleMemoryKey
import turtoise.memory.keys.MemoryKey
import turtoise.memory.keys.MemoryKey.Companion.ifEmpty
import turtoise.parser.TortoiseParser
import turtoise.parser.TortoiseParserStackItem

class RobotCircle(
    val radius: MemoryKey,
    val holeWidth: MemoryKey,
    val holeHeight: MemoryKey,
): IRobotCommand {
    override fun draw(ds: DrawerSettings): TortoiseBlock {
        return TortoiseBlock(
            listOfNotNull(
                TortoiseCommand.Circle(radius),
                TortoiseCommand.Rectangle(holeWidth, holeHeight.ifEmpty { DoubleMemoryKey(ds.holeWeight) })
                    .takeIf { holeWidth.isNotEmpty() }
            )
        )
    }

    object Factory: IRobotCommandFactory {
        override fun create(args: List<MemoryKey>, item: TortoiseParserStackItem): IRobotCommand {
            return RobotCircle(
                args.getOrElse(0) { MemoryKey.EMPTY },
                args.getOrElse(1) { MemoryKey.EMPTY },
                args.getOrElse(2) { MemoryKey.EMPTY })
        }

        override val names: List<String>
            get() = listOf("c")

        override fun help(): HelpInfoCommand {
            return  TortoiseParser.helpName("c", "r hw hh", "нарисовать окружность радиусу r")
        }
    }
}