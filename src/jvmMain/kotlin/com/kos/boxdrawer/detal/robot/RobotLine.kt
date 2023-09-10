package com.kos.boxdrawer.detal.robot

import turtoise.DrawerSettings
import turtoise.TortoiseAlgorithm
import turtoise.TortoiseBlock

class RobotLine(
    val line: List<IRobotCommand>
): TortoiseAlgorithm {
    override fun commands(name: String, ds: DrawerSettings): List<TortoiseBlock> {
        return when (name) {
            "robot" -> rotor(ds)
            else -> emptyList<TortoiseBlock>()
        }
    }

    override val names: List<String>
        get() = listOf("robot")

    fun rotor(ds: DrawerSettings): List<TortoiseBlock> {
        return line.map { it.draw(ds) }
    }
}