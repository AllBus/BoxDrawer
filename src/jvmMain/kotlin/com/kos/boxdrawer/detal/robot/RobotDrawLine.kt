package com.kos.boxdrawer.detal.robot

import androidx.compose.ui.text.AnnotatedString
import turtoise.DrawerSettings
import turtoise.TortoiseBlock
import turtoise.TortoiseCommand
import turtoise.TurtoiseParserStackItem

class RobotDrawLine(): IRobotCommand  {

    override fun draw(ds: DrawerSettings): TortoiseBlock {
        return TortoiseBlock(
            (1..4).flatMap {
                (1..4).flatMap {

                    listOfNotNull(
                        TortoiseCommand.Move(0.0),
                        TortoiseCommand.Line(0.0),
                        TortoiseCommand.Line(40.0),
                        TortoiseCommand.AngleAdd(30.0),
                        TortoiseCommand.Line(20.0),
                        TortoiseCommand.AngleAdd(150.0),
                        TortoiseCommand.Line(40.0),
                        TortoiseCommand.AngleAdd(30.0),
                        TortoiseCommand.Line(20.0),
                        TortoiseCommand.Circle(2.5),
                        TortoiseCommand.Split(),
                        TortoiseCommand.AngleAdd(60.0),
                        TortoiseCommand.Move(20.0),
                        TortoiseCommand.AngleAdd(90.0),

                        )
                }+ listOf(TortoiseCommand.Move(60.0,4*20.0),
                )
            }+ listOf(TortoiseCommand.Circle (10.0),
                TortoiseCommand.Move(20.0,20.0),)
                    +(1..2).flatMap {
                listOf(TortoiseCommand.Move(-12.0,6.0))+
                        (1..2).flatMap {
                            listOf(
                                TortoiseCommand.Move(6.0,0.0),
                                TortoiseCommand.Line(0.0),
                                TortoiseCommand.Line(5.0),
                                TortoiseCommand.AngleAdd(90.0),
                                TortoiseCommand.Line(5.0),
                                TortoiseCommand.AngleAdd(90.0),
                                TortoiseCommand.Line(5.0),
                                TortoiseCommand.AngleAdd(90.0),
                                TortoiseCommand.Line(5.0),
                                TortoiseCommand.AngleAdd(90.0),
                                TortoiseCommand.Split(),



                                )}}
        )
    }
    fun drawcir(ds: DrawerSettings): TortoiseBlock {
        return TortoiseBlock(
            listOfNotNull(
                TortoiseCommand.Circle(60.0),
                TortoiseCommand.Move(0.0),
                TortoiseCommand.Angle(90.0),
                TortoiseCommand.Line(-60.0),
                TortoiseCommand.Line(120.0),
                TortoiseCommand.ClosePolygon(),
                TortoiseCommand.Move(-60.0),
                TortoiseCommand.Angle(0.0),
                TortoiseCommand.Line(-60.0),
                TortoiseCommand.Line(120.0),
                TortoiseCommand.ClosePolygon(),
                TortoiseCommand.Move(-60.0),
                TortoiseCommand.Angle(45.0),
                TortoiseCommand.Line(-60.0),
                TortoiseCommand.Line(120.0),
                TortoiseCommand.Circle(10.0),
                TortoiseCommand.Move(-120.0),
                TortoiseCommand.Circle(10.0),
                TortoiseCommand.Move(60.0),


                )+
                    (1..3).flatMap{
                        listOf(
                            TortoiseCommand.Move(30.0),
                            TortoiseCommand.Rectangle(20.0, 20.0)
                        )
                    }+
                    listOf(
                        TortoiseCommand.Move(-3*30.0),
                        TortoiseCommand.Angle(90.0),
                        TortoiseCommand.ClosePolygon(),
                    )
                    +
                    (1..2).flatMap{
                        listOf(
                            TortoiseCommand.Move(20.0),
                            TortoiseCommand.Angle(0.0),
                            TortoiseCommand.Line(-10.0),
                            TortoiseCommand.Line(20.0),
                            TortoiseCommand.ClosePolygon(),
                            TortoiseCommand.Move(-10.0),
                            TortoiseCommand.Angle(90.0)
                        )
                    }+
                    listOf(TortoiseCommand.Move(-40.0),
                        TortoiseCommand.Angle(270.0),
                    )
                    +
                    (1..2).flatMap{
                        listOf(
                            TortoiseCommand.Move(20.0),
                            TortoiseCommand.Angle(0.0),
                            TortoiseCommand.Line(-10.0),
                            TortoiseCommand.Line(20.0),
                            TortoiseCommand.ClosePolygon(),
                            TortoiseCommand.Move(-10.0),
                            TortoiseCommand.Angle(270.0)
                        )
                    }
        )
    }

    object Factory: IRobotCommandFactory {
        override fun create(args: List<String>, item: TurtoiseParserStackItem): IRobotCommand {
            return RobotDrawLine()
        }

        override val names: List<String>
            get() = listOf("line")

        override fun help(): AnnotatedString {
            return AnnotatedString("")
        }

    }


}

