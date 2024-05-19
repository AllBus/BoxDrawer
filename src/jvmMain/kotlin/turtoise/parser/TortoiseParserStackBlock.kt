package turtoise.parser

import turtoise.memory.keys.MemoryKey

class TortoiseParserStackBlock(
    /** Тип скобок '(','[','{'*/
    val skobka: Char = '(',
) : TortoiseParserStackItem() {

    constructor(skobka: Char, items: List<TortoiseParserStackItem>) : this(skobka) {
        addItems(items)
    }

    constructor(name: MemoryKey, argument: MemoryKey) : this('(') {
        addItems(
            listOf(
                TortoiseParserStackArgument(name),
                TortoiseParserStackArgument(argument),
            )
        )
    }

    constructor(skobka: Char, name: MemoryKey) : this(skobka) {
        add(name)
    }

    constructor(skobka: Char, name: String) : this(skobka) {
        add(name)
    }

    override fun isArgument(): Boolean = false

    override val inner = mutableListOf<TortoiseParserStackItem>()
    override val blocks = mutableListOf<TortoiseParserStackBlock>()

    override val argument: MemoryKey
        get() = MemoryKey(this)

    override val value: MemoryKey
        get() = inner.getOrNull(1)?.argument ?: MemoryKey.EMPTY

    override val name
        get() = inner.firstOrNull()?.argument ?: MemoryKey.BLOCK

    override val line: String
        get() = inner.joinToString(" ", "$skobka", "${closeBrace()}") { it.line }

    override val innerLine: String
        get() = inner.joinToString(" ") { it.line }

    fun closeBrace(): Char {
        return TortoiseParser.closeBrace(skobka)
    }

    fun add(argument: String) {
        inner.add(
            TortoiseParserStackArgument(
                argument = MemoryKey(argument)
            )
        )
    }

    fun add(argument: MemoryKey) {
        inner.add(
            TortoiseParserStackArgument(
                argument = argument
            )
        )
    }

    fun add(name: String, argument: String) {
        add(TortoiseParserStackBlock(MemoryKey(name), MemoryKey(argument)))
    }

    fun add(name: String, argument: MemoryKey) {
        add(TortoiseParserStackBlock(MemoryKey(name), argument))
    }

    fun add(arguments: List<String>) {
        arguments.forEach { add(it) }
    }

    fun add(name: String, argument: TortoiseParserStackItem) {
        add(
            TortoiseParserStackBlock(
                '(', listOf(
                    TortoiseParserStackArgument(MemoryKey(name)),
                    argument
                )
            )
        )
    }

    fun add(argument: TortoiseParserStackBlock) {
        inner.add(argument)
        blocks.add(argument)
    }

    fun addItems(values: List<TortoiseParserStackItem>) {
        inner.addAll(values)
        blocks.addAll(values.filterIsInstance<TortoiseParserStackBlock>())
    }

    override fun arguments(): List<MemoryKey> {
        return inner.filter { it.isArgument() }.map { it.argument }
    }

    override val size: Int
        get() = inner.size

    override fun get(index: Int): MemoryKey? {
        return if (index < 0 || index >= inner.size) null else inner[index].argument
    }

    override fun get(index: String): MemoryKey? {
        return if (index.startsWith(".")) {
            val i = index.indexOf('.', 1)
            if (i > 0) {
                val n = index.take(i).drop(1)
                val next = index.drop(i)
                when (val a = getInnerAtName(n)) {
                    is TortoiseParserStackBlock ->
                        a.get(next)

                    else ->
                        null
                }
            } else {
                val n = index.drop(1)

                getInnerAtName(n)?.value
            }
        } else null
    }

    override fun stringValue(index: Int): String? {
        if (index < 0 || index >= inner.size) return null
        var c = 0
        for (i in 0 until inner.size) {
            if (inner[i].isArgument()) {
                if (c == index) return inner[i].argument.name
                c++
            }
        }
        return null
    }

    fun getBlockAtName(name: String): TortoiseParserStackBlock? {
        val nm = MemoryKey(name)
        return blocks.find {
            it.name == nm
        }
    }

    override fun getInnerAtName(name: String): TortoiseParserStackItem? {
        val i = name.toIntOrNull()
        return if (i != null)
            inner.getOrNull(i)
        else
            getBlockAtName(name)
    }


}