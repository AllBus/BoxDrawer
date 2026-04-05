package turtoise.memory

import turtoise.memory.keys.MemoryKey
import turtoise.parser.TortoiseParserStackItem


class FigureTortoiseMemory(
    val block: TortoiseParserStackItem,
    private val superMemory: TortoiseMemory,
) : SimpleTortoiseMemory() {

    override fun value(variable: MemoryKey, defaultValue: Double): Double {
        return block.get(variable.name)?.let { key ->
            superMemory.value(key, defaultValue)
        } ?: super.value(
            variable = variable,
            defaultValue = defaultValue
        )
    }
}