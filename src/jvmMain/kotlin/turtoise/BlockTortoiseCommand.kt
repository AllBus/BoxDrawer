package turtoise

import turtoise.memory.MemoryKeyBuilder
import turtoise.memory.TortoiseMemory
import turtoise.parser.TortoiseParserStackBlock
import turtoise.parser.TortoiseParserStackItem

class BlockTortoiseCommand(
    override val command: Char,
    val block: TortoiseParserStackBlock,
) : TortoiseCommand {
    override val size: Int
        get() = block.inner.size

    override fun take(index: Int, defaultValue: Double, memory: TortoiseMemory): Double {
        return block.get(index)?.let { b ->
            memory.value(b, defaultValue)
        } ?: 0.0
    }

    override fun takeBlock(index: Int): TortoiseParserStackItem? {
        return block.blocks.getOrNull(index)
    }

    override fun assign(memory: TortoiseMemory) {
        val values = block.inner
        if (values.isNotEmpty()) {
            if (values.size == 1) {
                memory.clear(block.name)
            } else {
                memory.assign(block.name, values.drop(1).sumOf {
                    calculateValue(it, memory)
                })
            }
        }
    }

    private fun calculateValue(item: TortoiseParserStackItem, memory: TortoiseMemory): Double {
        return memory.value(
            variable = MemoryKeyBuilder.createMemoryKey(item),
            defaultValue = 0.0,
        )
    }


    override fun print(): String {
        return "BlockTortoiseCommand(${TortoiseCommand.commandToName(command)}, ${block.line})"
    }

}