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

class RobotRect(
    params: List<String>
): RobotCommandWithParams(params) {

    override fun draw(ds: DrawerSettings): TortoiseBlock {
        val width = this[0,0.0]
        val height =  this[1,0.0]
        val zigHeight =  this[2,ds.boardWeight]

        var h = if (height == 0.0)
            width else height

        val w2 = width/2
        val h2 = height/2

        return TortoiseBlock(listOf(
            TortoiseCommand.PolylineDouble(listOf(
                -w2, - h2,
                -w2, -h2-zigHeight,
                w2, -h2-zigHeight,
                w2, -h2,
                w2+zigHeight, -h2,
                w2+zigHeight, h2,
                w2, h2,
                w2, h2+zigHeight,
                -w2, h2+zigHeight,
                -w2, h2,
                -w2-zigHeight, h2,
                -w2-zigHeight, -h2,
                -w2, - h2,
            )
            ))
        )
    }
}

class RobotHand(
    params: List<String>,
    val leftForm: List<IRobotCommand>,
    val rightForm: List<IRobotCommand>,

): RobotCommandWithParams(params) {
    override fun draw(ds: DrawerSettings): TortoiseBlock {
        val width = this[0,0.0]
        val height =  this[1,0.0]
        val zigWidth = this[2,0.0]
        val zigHeight = this[3, ds.boardWeight]

        val center1W  = this[4,0.0]
        val center1H  = this[5,ds.boardWeight]
        val center1D  = this[6,0.0]

        val center2W  = this[7,0.0]
        val center2H  = this[8,ds.boardWeight]
        val center2D  = this[9,0.0]
        val height2  = this[10,height]

        val w2 = width/2
        val h2 = height/2
        val z2 = zigWidth/2

        return TortoiseBlock(
            listOf(
            TortoiseCommand.PolylineDouble(listOf(
                -w2, -h2,
                -z2, -h2,
                -z2, -h2+zigHeight,
                z2, -h2+zigHeight,
                z2, - h2,
                w2, -h2,
            )),
            TortoiseCommand.PolylineDouble(listOf(
                -w2, h2,
                -z2, h2,
                -z2, h2-zigHeight,
                z2, h2-zigHeight,
                z2,  h2,
                w2, h2
            )),
            TortoiseCommand.Save(), // 1
            TortoiseCommand.Move(-w2, 0.0),
            TortoiseCommand.Arc(height/2, 90.0, 270.0),
            TortoiseCommand.Move(+center1D, 0.0),
            )+ listOfNotNull(
                TortoiseCommand.Rectangle(center1W, center1H).takeIf { (center1W!= 0.0) },
            ) + leftForm.flatMap { it.draw(ds).commands } +
            listOf(
            TortoiseCommand.Peek(), // 1
            TortoiseCommand.Move(w2, 0.0),
            TortoiseCommand.Arc(height/2, 270.0, 450.0),
            TortoiseCommand.Move(-center2D, 0.0),
            ) + listOfNotNull(
                TortoiseCommand.Rectangle(center2W, center2H).takeIf { (center2W!= 0.0) },
            ) + rightForm.flatMap { it.draw(ds).commands } +
            listOf(
                TortoiseCommand.Load() // 1
            )
        )
    }

}

class RobotUnion(
    params: List<String>
): RobotCommandWithParams(params) {

    override fun draw(ds: DrawerSettings): TortoiseBlock {
        val width = this[0,0.0]
        val height =  this[1,0.0]
        val zigWidth = this[2,0.0]
        val zigHeight = this[3, ds.boardWeight]
        val zigSdvig = this[4, 0.0]

        val w2 = width/2
        val h2 = height/2
        val z2 = zigWidth/2

        return TortoiseBlock(listOf(
            TortoiseCommand.PolylineDouble(listOf(
                -w2,-h2,
                -z2+zigSdvig, -h2,
                -z2, -h2-zigHeight,
                z2, -h2-zigHeight,
                z2-zigSdvig, -h2,
                w2,-h2,
                w2,h2,
                z2-zigSdvig, h2,
                z2, h2+zigHeight,
                -z2, h2+zigHeight,
                -z2+zigSdvig, h2,
                -w2,h2,
                -w2,-h2,
            ))
        ))
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

class RobotHole(
    val width: String,
    val height: String,
): IRobotCommand {
    override fun draw(ds: DrawerSettings): TortoiseBlock {
        return TortoiseBlock(listOf(
            TortoiseCommand.Rectangle(width, height.ifEmpty { ds.holeWeight.toString() })
        ))
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