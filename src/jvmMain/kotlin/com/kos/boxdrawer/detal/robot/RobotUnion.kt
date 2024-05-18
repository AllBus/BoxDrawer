package com.kos.boxdrawer.detal.robot

import turtoise.*
import turtoise.help.HelpInfoCommand
import turtoise.memory.keys.MemoryKey
import turtoise.parser.TortoiseParser
import turtoise.parser.TortoiseParserStackItem

class RobotUnion(
    params: List<MemoryKey>
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

        return TortoiseBlock(
            listOf(
                TortoiseCommand.PolylineDouble(
                    listOf(
                        -w2, -h2,
                        -z2 + zigSdvig, -h2,
                        -z2, -h2 - zigHeight,
                        z2, -h2 - zigHeight,
                        z2 - zigSdvig, -h2,
                        w2, -h2,
                        w2, h2,
                        z2 - zigSdvig, h2,
                        z2, h2 + zigHeight,
                        -z2, h2 + zigHeight,
                        -z2 + zigSdvig, h2,
                        -w2, h2,
                        -w2, -h2,
                    )
                )
            )
        )
    }

    object Factory: IRobotCommandFactory{
        override fun create(args: List<MemoryKey>, item: TortoiseParserStackItem): IRobotCommand {
            return RobotUnion(args)
        }

        override val names: List<String>
            get() = listOf("u", "union")

        override fun help(): HelpInfoCommand {
            return TortoiseParser.helpName("u", "w h zw zh zs", "")
        }

        override val isSimple: Boolean
            get() = false
    }
}