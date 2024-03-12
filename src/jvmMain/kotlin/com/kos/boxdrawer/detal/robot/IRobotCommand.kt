package com.kos.boxdrawer.detal.robot

import androidx.compose.ui.text.AnnotatedString
import turtoise.*

interface IRobotCommand {
    fun draw(ds: DrawerSettings):TortoiseBlock

}

interface IRobotCommandFactory{
    fun create(args: List<String>, item: TurtoiseParserStackItem): IRobotCommand

    val names : List<String>

    val isSimple: Boolean get() = true
    fun help() : AnnotatedString
}

abstract class RobotCommandWithParams(val params: List<String>): IRobotCommand{

    operator fun get(index:Int):String{
        return params.getOrElse(index){""}
    }
    operator fun get(index:Int, value: Double):Double{
        return params.getOrElse(index){""}.toDoubleOrNull()?: value
    }
}

