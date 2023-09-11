package com.kos.boxdrawer.detal.robot

import turtoise.DrawerSettings
import turtoise.TortoiseBlock
import turtoise.TortoiseCommand

class RobotRect(
    params: List<String>
): RobotCommandWithParams(params) {

    override fun draw(ds: DrawerSettings): TortoiseBlock {
        val width = this[0,0.0]
        val height = this[1,0.0]
        val zigWidth  = this[2,ds.boardWeight]
        val zigHeight = this[3,zigWidth]

        var h = if (height == 0.0)
            width else height

        val w2 = width/2
        val h2 = height/2

        return TortoiseBlock(
            listOf(
                TortoiseCommand.PolylineDouble(
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
            )
        )
    }
}