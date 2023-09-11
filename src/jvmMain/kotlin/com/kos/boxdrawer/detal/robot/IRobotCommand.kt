package com.kos.boxdrawer.detal.robot

import turtoise.DrawerSettings
import turtoise.TortoiseBlock
import turtoise.TortoiseCommand

interface IRobotCommand {
    fun draw(ds: DrawerSettings):TortoiseBlock
}

abstract class RobotCommandWithParams(val params: List<String>): IRobotCommand{

    operator fun get(index:Int):String{
        return  params.getOrElse(index){""}
    }
    operator fun get(index:Int, value: Double):Double{
        return params.getOrElse(index){""}.toDoubleOrNull()?: value
    }
}

class RobotCircle(
    val radius: String,
    val holeWidth: String,
    val holeHeight: String,
): IRobotCommand {
    override fun draw(ds: DrawerSettings): TortoiseBlock {
        return TortoiseBlock(
            listOfNotNull(
            TortoiseCommand.Circle(radius),
            TortoiseCommand.Rectangle(holeWidth, holeHeight.ifEmpty { ds.holeWeight.toString() }).takeIf { holeWidth.isNotEmpty() }
        )
        )

    }
}

class RobotMove(
    val x: String,
    val y: String,
): IRobotCommand {
    override fun draw(ds: DrawerSettings): TortoiseBlock {
        return TortoiseBlock(listOf(
            TortoiseCommand.Move(x, y)
        ))
    }

}

class RobotAngle(
    val angle: String,
): IRobotCommand {
    override fun draw(ds: DrawerSettings): TortoiseBlock {
        return TortoiseBlock(listOf(
            TortoiseCommand.Angle(angle)
        ))
    }

}

class RobotEmpty():IRobotCommand{
    override fun draw(ds: DrawerSettings): TortoiseBlock {
        return TortoiseBlock(emptyList())
    }

}