package com.kos.boxdrawer.detal.robot

import androidx.compose.ui.text.AnnotatedString
import turtoise.*

class RobotAngle(
    val angle: String,
): IRobotCommand {
    override fun draw(ds: DrawerSettings): TortoiseBlock {
        return TortoiseBlock(
            listOf(
                TortoiseCommand.Angle(angle)
            )
        )
    }

    object Factory: IRobotCommandFactory {
        override fun create(args: List<String>, item: TurtoiseParserStackItem): IRobotCommand {
            return RobotAngle(args.getOrElse(0) { "" })
        }

        override val names: List<String>
            get() = listOf("a")

        override fun help(): AnnotatedString {
            return TortoiseParser.helpName("a", "a", "повернуть направление движение на угол a ")
        }
    }
}