package com.kos.boxdrawer.detal.robot

import androidx.compose.ui.text.AnnotatedString
import turtoise.*
import turtoise.memory.DoubleMemoryKey
import turtoise.memory.MemoryKey
import turtoise.memory.MemoryKey.Companion.ifEmpty

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
        override fun create(args: List<MemoryKey>, item: TurtoiseParserStackItem): IRobotCommand {
            return RobotCircle(
                args.getOrElse(0) { MemoryKey.EMPTY },
                args.getOrElse(1) { MemoryKey.EMPTY },
                args.getOrElse(2) { MemoryKey.EMPTY })
        }

        override val names: List<String>
            get() = listOf("c")

        override fun help(): AnnotatedString {
            return TortoiseParser.helpName("c", "r hw hh", "нарисовать окружность радиусу r")
        }
    }
}