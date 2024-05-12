package com.kos.boxdrawer.detal.robot

import androidx.compose.ui.text.AnnotatedString
import turtoise.*
import turtoise.memory.MemoryKey

interface IRobotCommand {
    fun draw(ds: DrawerSettings):TortoiseBlock

}

interface IRobotCommandFactory{
    fun create(args: List<MemoryKey>, item: TurtoiseParserStackItem): IRobotCommand

    val names : List<String>

    val isSimple: Boolean get() = true
    fun help() : AnnotatedString
}

abstract class RobotCommandWithParams(val params: List<MemoryKey>): IRobotCommand{

    operator fun get(index:Int):MemoryKey{
        return params.getOrElse(index){ MemoryKey.EMPTY}
    }
    operator fun get(index:Int, value: Double):Double{
        return params.getOrElse(index){MemoryKey.EMPTY}.toDoubleOrNull()?: value
    }
//    operator fun get(index:Int, value: Double):MemoryKey{
//        return MemoryKeyWithDefault( params.getOrElse(index){MemoryKey.EMPTY} , value)
//    }
}

