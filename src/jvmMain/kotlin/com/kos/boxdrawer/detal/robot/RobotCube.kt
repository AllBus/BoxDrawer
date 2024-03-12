package com.kos.boxdrawer.detal.robot

import androidx.compose.ui.text.AnnotatedString
import turtoise.DrawerSettings
import turtoise.TortoiseAlgorithm
import turtoise.TortoiseBlock
import turtoise.TortoiseCommand
import turtoise.TortoiseParser
import turtoise.TurtoiseParserStackItem

class RobotCube(
    val width:Double,
    val height:Double,
    val weight:Double,
    val boardWeight:Double,
    val holeDrop:Double,
    val pazWidth:Double,
): IRobotCommand {

    override fun draw(ds: DrawerSettings): TortoiseBlock {
        return TortoiseBlock(
            listOf(
                rect(width, height, pazWidth, boardWeight)
            )
        )
    }

    fun rect(width:Double, height:Double, zigWidth:Double, zigHeight:Double):TortoiseCommand{
        val w2 = width/2
        val h2 = height/2

        return TortoiseCommand.PolylineDouble(
            listOf(
                -w2, -h2,
                -w2, -h2 - zigHeight,
                w2, -h2 - zigHeight,
                w2, -h2,
                w2 + zigWidth, -h2,
                w2 + zigWidth, h2,
                w2, h2,
                w2, h2 + zigHeight,
                -w2, h2 + zigHeight,
                -w2, h2,
                -w2 - zigWidth, h2,
                -w2 - zigWidth, -h2,
                -w2, -h2,
            )
        )
    }

    object Factory: IRobotCommandFactory{
        override fun create(args: List<String>, item: TurtoiseParserStackItem): IRobotCommand {
            return RobotCube(
                width = item.doubleValue(0, 0.0),
                height = item.doubleValue(1, 0.0),
                weight = item.doubleValue(2, 0.0),
                boardWeight = item.doubleValue(3, 0.0),
                holeDrop = item.doubleValue(4, 0.0),
                pazWidth = item.doubleValue(5, 0.0),
            )
        }

        override val names: List<String>
            get() = listOf("cube")

        override fun help(): AnnotatedString {
            return TortoiseParser.helpName("cube", "", "кубик")
        }
    }
}