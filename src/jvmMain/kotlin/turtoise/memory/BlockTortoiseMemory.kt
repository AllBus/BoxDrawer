package turtoise.memory

import turtoise.TurtoiseParserStackItem

class BlockTortoiseMemory(
    val block: TurtoiseParserStackItem
) : SimpleTortoiseMemory() {

    override fun value(variable: String, defaultValue: Double): Double {
        return super.value(
            variable = block.get(variable) ?: variable,
            defaultValue = defaultValue
        )
    }
}

class TwoBlockTortoiseMemory(
    val block: TurtoiseParserStackItem,
    val defaultBlock: TurtoiseParserStackItem,
) : SimpleTortoiseMemory() {

    override fun value(variable: String, defaultValue: Double): Double {
        return super.value(
            variable = block.get(variable) ?: defaultBlock.get(variable) ?: variable,
            defaultValue = defaultValue
        )
    }
}