package turtoise.memory

import turtoise.TurtoiseParserStackItem
import turtoise.memory.keys.MemoryKey

class BlockTortoiseMemory(
    val block: TurtoiseParserStackItem
) : SimpleTortoiseMemory() {

    override fun value(variable: MemoryKey, defaultValue: Double): Double {
        return super.value(
            variable = block.get(variable.name)?: variable,
            defaultValue = defaultValue
        )
    }
}

class TwoBlockTortoiseMemory(
    val block: TurtoiseParserStackItem,
    val defaultBlock: TurtoiseParserStackItem,
) : SimpleTortoiseMemory() {

    override fun value(variable: MemoryKey, defaultValue: Double): Double {
        return super.value(
            variable = (
                    block.get(variable.name) ?:
                    defaultBlock.get(variable.name)
                    ) ?: variable,
            defaultValue = defaultValue
        )
    }
}