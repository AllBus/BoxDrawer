package com.kos.boxdrawer.detal.robot

import androidx.compose.ui.text.AnnotatedString
import turtoise.*
import turtoise.memory.MemoryKey
import turtoise.memory.MemoryKey.Companion.div
import turtoise.memory.MemoryKey.Companion.plus
import turtoise.memory.MemoryKey.Companion.unaryMinus

class RobotRect(
    params: List<MemoryKey>
): RobotCommandWithParams(params) {

    override fun draw(ds: DrawerSettings): TortoiseBlock {
        val width = this[0,0.0]
        val height = this[1,0.0]
        val zigWidth  = this[2,ds.boardWeight]
        val zigHeight = this[3,zigWidth]

        val h = if (height == 0.0)
            width else height

        val w2 = width/2
        val h2 = h/2

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

    object Factory: IRobotCommandFactory{
        override fun create(args: List<MemoryKey>, item: TurtoiseParserStackItem): IRobotCommand {
            return RobotRect(args)
        }

        override val names: List<String>
            get() = listOf("x",)

        override fun help(): AnnotatedString {
            return TortoiseParser.helpName("x", "w h zw zh", "")
        }
    }
}

class RobotHardRect(
    params: List<MemoryKey>
): RobotCommandWithParams(params) {

    override fun draw(ds: DrawerSettings): TortoiseBlock {
        val width = this[0,0.0]
        val height = this[1,0.0]
        val zigWidth  = this[2,0.0]
        val zigHeight = this[3,0.0]
        val zwga = this[4,ds.boardWeight]
        val zhga = this[5,ds.boardWeight]
        val zwgb = this[6,zwga]
        val zhgb = this[7,zhga]

        val h = if (height == 0.0)
            width else height

        val w2 = width/2.0
        val h2 = h/2.0
        val zw2 = zigWidth/2.0
        val zh2 = zigHeight/2.0

        return TortoiseBlock(
            listOf(
                TortoiseCommand.PolylineDouble(
                    listOfNotNull(
                        listOf(
                            -w2, -h2,
                            ),
                        listOf(
                            -zw2, -h2,
                            -zw2, -(h2 + zwga),
                            zw2, -(h2 + zwga),
                            zw2, -h2,
                        ).takeIf { zw2 != 0.0 },
                        listOf(
                            w2, -h2,
                            ),
                        listOf(
                            w2, -zh2,
                            w2 + zhga, -zh2,
                            w2 + zhga, zh2,
                            w2, zh2,
                        ).takeIf { zh2 != 0.0 },
                        listOf(
                            w2, h2,
                            ),
                        listOf(
                            zw2, h2,
                            zw2, h2 + zwgb,
                            -zw2, h2 + zwgb,
                            -zw2, h2,
                        ).takeIf { zw2 != 0.0 },
                        listOf(
                            -w2, h2,
                        ),
                        listOf(
                            -w2, zh2,
                            -(w2 + zhgb), zh2,
                            -(w2 + zhgb), -zh2,
                            -w2, -zh2,
                        ).takeIf { zh2 != 0.0 },
                        listOf(
                            -w2, -h2,
                        )
                    ).flatten()
                )
            )
        )
    }

    object Factory: IRobotCommandFactory{
        override fun create(args: List<MemoryKey>, item: TurtoiseParserStackItem): IRobotCommand {
            return RobotHardRect(args)
        }

        override val names: List<String>
            get() = listOf("r", "rect")

        override fun help(): AnnotatedString {
            return TortoiseParser.helpName("r", "w h zw zh zwga zhga zwgb zhgb", "")
        }
    }
}