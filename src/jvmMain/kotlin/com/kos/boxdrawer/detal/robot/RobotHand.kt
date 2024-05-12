package com.kos.boxdrawer.detal.robot

import androidx.compose.ui.text.AnnotatedString
import turtoise.*
import turtoise.memory.MemoryKey

class RobotHand(
    params: List<MemoryKey>,
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
                TortoiseCommand.PolylineDouble(
                    listOf(
                        -w2, -h2,
                        -z2, -h2,
                        -z2, -h2 + zigHeight,
                        z2, -h2 + zigHeight,
                        z2, -h2,
                        w2, -h2,
                    )
                ),
                TortoiseCommand.PolylineDouble(
                    listOf(
                        -w2, h2,
                        -z2, h2,
                        -z2, h2 - zigHeight,
                        z2, h2 - zigHeight,
                        z2, h2,
                        w2, h2
                    )
                ),
                TortoiseCommand.Save(), // 1
                TortoiseCommand.Move(-w2, 0.0),
                TortoiseCommand.Arc(height / 2, 90.0, 270.0),
                TortoiseCommand.Move(+center1D, 0.0),
            ) + listOfNotNull(
                TortoiseCommand.Rectangle(center1W, center1H).takeIf { (center1W != 0.0) },
            ) + leftForm.flatMap { it.draw(ds).commands } +
                    listOf(
                        TortoiseCommand.Peek(), // 1
                        TortoiseCommand.Move(w2, 0.0),
                        TortoiseCommand.Arc(height / 2, 270.0, 450.0),
                        TortoiseCommand.Move(-center2D, 0.0),
                    ) + listOfNotNull(
                TortoiseCommand.Rectangle(center2W, center2H).takeIf { (center2W != 0.0) },
            ) + rightForm.flatMap { it.draw(ds).commands } +
                    listOf(
                        TortoiseCommand.Load() // 1
                    )
        )
    }

    object Factory: IRobotCommandFactory{
        override fun create(args: List<MemoryKey>, item: TurtoiseParserStackItem): IRobotCommand {
            return  RobotHand(
                args,
                item.blocks.firstOrNull()?.let { b -> RobotLine.parseRobot(b, true) } ?: emptyList(),
                item.blocks.getOrNull(1)?.let { b -> RobotLine.parseRobot(b, true) } ?: emptyList(),
            )
        }

        override val names: List<String>
            get() = listOf("line", "l", "connect")

        override fun help(): AnnotatedString {
            return TortoiseParser.helpName("l", "w h zw zh c1w c1h c1d (lcom*) c2w c2h c2d (rcom*)", "")
        }

        override val isSimple: Boolean
            get() = false
    }
}