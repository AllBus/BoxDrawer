package turtoise.parser

import turtoise.memory.keys.MemoryKey

class TortoiseParserStackArgument(
    override val argument: MemoryKey,
) : TortoiseParserStackItem() {
    override fun isArgument(): Boolean = true

    override val value: MemoryKey get() = argument
    override val name: MemoryKey get() = argument

    override val inner: List<TortoiseParserStackItem>
        get() = emptyList()

    override val blocks: List<TortoiseParserStackItem>
        get() = emptyList()

    override fun get(index: Int): MemoryKey? {
        return if (index == 0) argument else null
    }

    override fun get(index: String): MemoryKey? {
        return if (index.isEmpty() || index == "0" || index == ".")
            argument else null
    }

    override fun stringValue(index: Int): String? {
        return if (index == 0) argument.name else null
    }

    override fun getInnerAtName(name: String): TortoiseParserStackItem? {
        return null
    }

    override fun arguments(): List<MemoryKey> {
        return listOf(argument)
    }

    override val size: Int
        get() = 1

    override val line: String get() = argument.name
    override val innerLine: String get() = argument.name
}