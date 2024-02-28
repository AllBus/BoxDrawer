package turtoise

import turtoise.memory.TortoiseMemory

class BlockTortoiseCommand(
    override val command: Char,
    val block : TurtoiseParserStackBlock,
): TortoiseCommand  {
    override val size: Int
        get() = block.inner.size

    override fun take(index: Int, defaultValue: Double, memory: TortoiseMemory): Double {
        return block.get(index)?.let { b ->
            memory.value(b, defaultValue)
        }?: 0.0
    }

    override fun takeBlock(index: Int): TurtoiseParserStackItem? {
        return block.blocks.getOrNull(index)
    }


    override fun print(): String {
        return "BlockTortoiseCommand(${TortoiseCommand.commandToName(command)}, ${block.line})"
    }

}