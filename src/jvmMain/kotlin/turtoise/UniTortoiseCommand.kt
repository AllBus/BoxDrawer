package turtoise

import turtoise.memory.MemoryKey
import turtoise.memory.TortoiseMemory

class UniTortoiseCommand(
    override val command: Char,
    private val values: List<MemoryKey>,
) : TortoiseCommand {

    override val size: Int
        get() = values.size

    override fun take(index: Int, defaultValue: Double, memory: TortoiseMemory): Double {
        if (index >= 0 && index < values.size)
            return memory.value(values[index], defaultValue)
        return defaultValue
    }

    override fun assign(memory: TortoiseMemory) {
        if (values.isNotEmpty()) {
            if (values.size == 1) {
                memory.clear(values.first())
            } else {
                memory.assign(values.first(), values.drop(1).sumOf { memory.value(it, 0.0) })
            }
        }
    }

    override fun print(): String {
        val args = values.joinToString(", ") { "\"$it\"" }
        return when (values.size) {
            1 -> {
                when (command) {
                    TortoiseCommand.TURTOISE_MOVE -> "Move($args)"
                    TortoiseCommand.TURTOISE_LINE -> "Line($args)"
                    TortoiseCommand.TURTOISE_CIRCLE -> "Circle($args)"
                    TortoiseCommand.TURTOISE_ANGLE -> "Angle($args)"
                    TortoiseCommand.TURTOISE_ANGLE_ADD -> "AngleAdd($args)"
                    else -> "UniTortoiseCommand(${TortoiseCommand.commandToName(command)}, $args)"
                }
            }

            2 ->
                when (command) {
                    TortoiseCommand.TURTOISE_MOVE -> "Move($args)"
                    TortoiseCommand.TURTOISE_LINE -> "Line($args)"
                    TortoiseCommand.TURTOISE_RECTANGLE -> "Rectangle($args)"
                    else -> "UniTortoiseCommand(${TortoiseCommand.commandToName(command)}, $args)"
                }

            else -> "UniTortoiseCommand(${TortoiseCommand.commandToName(command)}, $args)"
        }
    }
}