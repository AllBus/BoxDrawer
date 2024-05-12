package com.kos.boxdrawer.detal.robot

import androidx.compose.ui.text.AnnotatedString
import turtoise.DrawerSettings
import turtoise.TortoiseBlock
import turtoise.TurtoiseParserStackItem
import turtoise.memory.keys.MemoryKey

class RobotEmpty(): IRobotCommand {
    override fun draw(ds: DrawerSettings): TortoiseBlock {
        return TortoiseBlock(emptyList())
    }

    object Factory: IRobotCommandFactory {
        override fun create(args: List<MemoryKey>, item: TurtoiseParserStackItem): IRobotCommand {
            return RobotEmpty()
        }

        override val names: List<String>
            get() = listOf("")

        override fun help(): AnnotatedString {
            return AnnotatedString("")
        }
    }
}