package turtoise.memory

import turtoise.parser.TortoiseParserStackItem
import turtoise.memory.keys.MemoryKey

class BlockTortoiseMemory(
    val block: TortoiseParserStackItem
) : SimpleTortoiseMemory() {

    override fun value(variable: MemoryKey, defaultValue: Double): Double {
        return super.value(
            variable = block.get(variable.name)?: variable,
            defaultValue = defaultValue
        )
    }
}

class TwoBlockTortoiseMemory(
    val block: TortoiseParserStackItem,
    val defaultBlock: TortoiseParserStackItem,
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