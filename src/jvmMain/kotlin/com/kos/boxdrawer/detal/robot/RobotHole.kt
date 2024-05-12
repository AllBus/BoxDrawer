package com.kos.boxdrawer.detal.robot

import androidx.compose.ui.text.AnnotatedString
import turtoise.*
import turtoise.memory.keys.DoubleMemoryKey
import turtoise.memory.keys.MemoryKey
import turtoise.memory.keys.MemoryKey.Companion.ifEmpty

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
        override fun create(args: List<MemoryKey>, item: TurtoiseParserStackItem): IRobotCommand {
            return RobotHole(
                args.getOrElse(0) { MemoryKey.EMPTY },
                args.getOrElse(1) { MemoryKey.EMPTY }
            )
        }

        override val names: List<String>
            get() = listOf("h", "hole")

        override fun help(): AnnotatedString {
            return TortoiseParser.helpName("h", "hw hh", "прямоугольник отверстия шириной hw  высотой hh")
        }
    }
}