package com.kos.boxdrawer.detal.robot

import androidx.compose.ui.text.AnnotatedString
import turtoise.DrawerSettings
import turtoise.TortoiseAlgorithm
import turtoise.TortoiseBlock
import turtoise.TortoiseCommand
import turtoise.TortoiseParser
import turtoise.TurtoiseParserStackItem
import turtoise.memory.DividerMemoryKey
import turtoise.memory.MemoryKey
import turtoise.memory.MemoryKey.Companion.EMPTY
import turtoise.memory.MemoryKey.Companion.div
import turtoise.memory.MemoryKey.Companion.plus
import turtoise.memory.MemoryKey.Companion.unaryMinus

class RobotCube(
    val width:MemoryKey,
    val height:MemoryKey,
    val weight:MemoryKey,
    val boardWeight:MemoryKey,
    val holeDrop:MemoryKey,
    val pazWidth:MemoryKey,
): IRobotCommand {

    override fun draw(ds: DrawerSettings): TortoiseBlock {
        return TortoiseBlock(
            listOf(
                rect(width, height, pazWidth, boardWeight)
            )
        )
    }

    fun rect(width:MemoryKey, height:MemoryKey, zigWidth:MemoryKey, zigHeight:MemoryKey):TortoiseCommand{
        val w2 = width / 2.0
        val h2 = height /2.0

        return TortoiseCommand.Polyline(
            listOf(
                -w2, -h2,
                -w2, -(h2 + zigHeight),
                w2, -(h2 + zigHeight),
                w2, -h2,
                w2 + zigWidth, -h2,
                w2 + zigWidth, h2,
                w2, h2,
                w2, h2 + zigHeight,
                -w2, h2 + zigHeight,
                -w2, h2,
                -(w2 + zigWidth), h2,
                -(w2 + zigWidth), -h2,
                -w2, -h2,
            )
        )
    }

    object Factory: IRobotCommandFactory{
        override fun create(args: List<MemoryKey>, item: TurtoiseParserStackItem): IRobotCommand {
            return RobotCube(
                width = item.get(0)?: EMPTY,
                height = item.get(1)?: EMPTY,
                weight = item.get(2)?: EMPTY,
                boardWeight = item.get(3)?: EMPTY,
                holeDrop = item.get(4)?: EMPTY,
                pazWidth = item.get(5)?: EMPTY,
            )
        }

        override val names: List<String>
            get() = listOf("cube")

        override fun help(): AnnotatedString {
            return TortoiseParser.helpName("cube", "", "кубик")
        }
    }
}