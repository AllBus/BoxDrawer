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
    params: List<String>
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
            TortoiseCommand.Move(-w2+center1D, 0.0),
            TortoiseCommand.Rectangle(center1W, center1H),
            TortoiseCommand.Move(-center1D, 0.0),
            TortoiseCommand.Arc(height/2, 90.0, 270.0),

            TortoiseCommand.Move(width, 0.0),
            TortoiseCommand.Arc(height/2, 270.0, 450.0),
        ) + (
            if ( center2W!= 0.0)
                listOf(
                    TortoiseCommand.Move(-center2D, 0.0),
                    TortoiseCommand.Rectangle(center2W, center2H),
                    TortoiseCommand.Move(+center2D-w2, 0.0)
                )
            else
                listOf(TortoiseCommand.Move(-w2,0.0))
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
    val height: String,
): IRobotCommand {
    override fun draw(ds: DrawerSettings): TortoiseBlock {
        return TortoiseBlock(listOf(
            TortoiseCommand.Circle(radius)
        ))
    }
}

class RobotHole(
    val width: String,
    val height: String,
): IRobotCommand {
    override fun draw(ds: DrawerSettings): TortoiseBlock {
        return TortoiseBlock(listOf(
            TortoiseCommand.Rectangle(width, if (height.isEmpty()) ds.holeWeight.toString() else height)
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