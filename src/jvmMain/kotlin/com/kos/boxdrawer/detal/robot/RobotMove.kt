package com.kos.boxdrawer.detal.robot

import androidx.compose.ui.text.AnnotatedString
import turtoise.*

class RobotMove(
    val x: String,
    val y: String,
): IRobotCommand {
    override fun draw(ds: DrawerSettings): TortoiseBlock {
        return TortoiseBlock(
            listOf(
                TortoiseCommand.Move(x, y)
            )
        )
    }

    object Factory: IRobotCommandFactory {
        override fun create(args: List<String>, item: TurtoiseParserStackItem): IRobotCommand {
            return RobotMove(args.getOrElse(0) { "" }, args.getOrElse(1) { "" })
        }

        override val names: List<String>
            get() = listOf("m")

        override fun help(): AnnotatedString {
            return TortoiseParser.helpName("m", "x y", "переместить позицию")
        }
    }
}