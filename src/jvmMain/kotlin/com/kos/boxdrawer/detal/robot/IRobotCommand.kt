package com.kos.boxdrawer.detal.robot

import turtoise.*
import turtoise.help.HelpInfoCommand
import turtoise.memory.keys.MemoryKey
import turtoise.parser.TortoiseParserStackItem

interface IRobotCommand {
    fun draw(ds: DrawerSettings):TortoiseBlock

}

interface IRobotCommandFactory{
    fun create(args: List<MemoryKey>, item: TortoiseParserStackItem): IRobotCommand

    val names : List<String>

    val isSimple: Boolean get() = true
    fun help() : HelpInfoCommand
}

abstract class RobotCommandWithParams(val params: List<MemoryKey>): IRobotCommand{

    operator fun get(index:Int): MemoryKey {
        return params.getOrElse(index){ MemoryKey.EMPTY}
    }
    operator fun get(index:Int, value: Double):Double{
        return params.getOrElse(index){ MemoryKey.EMPTY}.toDoubleOrNull()?: value
    }
//    operator fun get(index:Int, value: Double):MemoryKey{
//        return MemoryKeyWithDefault( params.getOrElse(index){MemoryKey.EMPTY} , value)
//    }
}

