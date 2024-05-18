package com.kos.boxdrawer.detal.box

import com.kos.boxdrawer.detal.robot.IRobotCommand
import com.kos.boxdrawer.detal.robot.IRobotCommandFactory
import turtoise.*
import turtoise.help.HelpInfoCommand
import turtoise.memory.keys.MemoryKey
import turtoise.parser.TortoiseParser
import turtoise.parser.TortoiseParserStackItem

/**
 * TODO:
 */
class CompositeBox(
    private val args: List<List<MemoryKey>>
): IRobotCommand {

    override fun draw(ds: DrawerSettings): TortoiseBlock {

        if (args.size>=1){

            val top = args[0]

            val angle = 180/top.size

            val sides = top.size*2

            val lengths = top + top



        }

        return TortoiseBlock(
            listOf(
             //   TortoiseCommand.Rectangle(width, height.ifEmpty { ds.holeWeight.toString() })
            )
        )
    }

    object Factory: IRobotCommandFactory {
        override fun create(args: List<MemoryKey>, item: TortoiseParserStackItem): IRobotCommand {
            return CompositeBox(item.blocks.map { it.arguments() })
        }

        override val names: List<String>
            get() = listOf("box",)

        override fun help(): HelpInfoCommand {
            return TortoiseParser.helpName("h", "hw hh", "многоугольная коробка")
        }
    }
}