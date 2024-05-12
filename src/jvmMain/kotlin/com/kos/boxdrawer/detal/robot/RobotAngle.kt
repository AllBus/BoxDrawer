package com.kos.boxdrawer.detal.robot

import androidx.compose.ui.text.AnnotatedString
import turtoise.*
import turtoise.memory.MemoryKey

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
        override fun create(args: List<MemoryKey>, item: TurtoiseParserStackItem): IRobotCommand {
            return RobotAngle(args.getOrElse(0) { MemoryKey.EMPTY })
        }

        override val names: List<String>
            get() = listOf("a")

        override fun help(): AnnotatedString {
            return TortoiseParser.helpName("a", "a", "повернуть направление движение на угол a ")
        }
    }
}