package com.kos.boxdrawer.detal.robot

import turtoise.DrawerSettings
import turtoise.TortoiseBlock
import turtoise.TortoiseCommand

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
}