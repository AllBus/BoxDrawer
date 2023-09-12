package com.kos.boxdrawer.detal.robot

import androidx.compose.ui.text.AnnotatedString
import turtoise.*

class RobotCircle(
    val radius: String,
    val holeWidth: String,
    val holeHeight: String,
): IRobotCommand {
    override fun draw(ds: DrawerSettings): TortoiseBlock {
        return TortoiseBlock(
            listOfNotNull(
                TortoiseCommand.Circle(radius),
                TortoiseCommand.Rectangle(holeWidth, holeHeight.ifEmpty { ds.holeWeight.toString() })
                    .takeIf { holeWidth.isNotEmpty() }
            )
        )
    }

    object Factory: IRobotCommandFactory {
        override fun create(args: List<String>, item: TurtoiseParserStackItem): IRobotCommand {
            return RobotCircle(args.getOrElse(0) { "" }, args.getOrElse(1) { "" }, args.getOrElse(2) { "" })
        }

        override val names: List<String>
            get() = listOf("c")

        override fun help(): AnnotatedString {
            return TortoiseParser.helpName("c", "r hw hh", "нарисовать окружность радиусу r")
        }
    }
}