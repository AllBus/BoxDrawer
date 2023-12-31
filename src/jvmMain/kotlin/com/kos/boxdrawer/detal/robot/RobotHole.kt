package com.kos.boxdrawer.detal.robot

import androidx.compose.ui.text.AnnotatedString
import turtoise.*

class RobotHole(
    val width: String,
    val height: String,
): IRobotCommand {
    override fun draw(ds: DrawerSettings): TortoiseBlock {
        return TortoiseBlock(
            listOf(
                TortoiseCommand.Rectangle(width, height.ifEmpty { ds.holeWeight.toString() })
            )
        )
    }

    object Factory: IRobotCommandFactory{
        override fun create(args: List<String>, item: TurtoiseParserStackItem): IRobotCommand {
            return RobotHole(args.getOrElse(0) { "" }, args.getOrElse(1) { "" })
        }

        override val names: List<String>
            get() = listOf("h", "hole")

        override fun help(): AnnotatedString {
            return TortoiseParser.helpName("h", "hw hh", "прямоугольник отверстия шириной hw  высотой hh")
        }
    }
}