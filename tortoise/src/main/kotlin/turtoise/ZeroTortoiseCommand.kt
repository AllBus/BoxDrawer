package turtoise

import turtoise.memory.TortoiseMemory

class ZeroTortoiseCommand(
    override val command: Char,
) : TortoiseCommand {
    override val size: Int
        get() = 0

    override fun take(index: Int, defaultValue: Double, memory: TortoiseMemory): Double {
        return defaultValue
    }

    override fun print(): String {
        return when (command){
            TortoiseCommand.TURTOISE_CLOSE -> "ClosePolygon()"
            TortoiseCommand.TURTOISE_MOVE -> "Move(0.0)"
            TortoiseCommand.TURTOISE_SPLIT -> "Split()"
            TortoiseCommand.TURTOISE_SAVE ->  "Save()"
            TortoiseCommand.TURTOISE_LOAD -> "Load()"
            TortoiseCommand.TURTOISE_PEEK -> "Peek()"
            TortoiseCommand.TURTOISE_CLEAR -> "Clear()"
            TortoiseCommand.TURTOISE_END_LOOP -> "EndLoop()"
            else -> "ZeroTortoiseCommand(${TortoiseCommand.commandToName(command)})"
        }
    }
}