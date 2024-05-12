package com.kos.boxdrawer.detal.robot

import androidx.compose.ui.text.AnnotatedString
import turtoise.*
import turtoise.memory.MemoryKey

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

        override fun help(): AnnotatedString {
            return TortoiseParser.helpName("m", "x y", "переместить позицию")
        }
    }
}