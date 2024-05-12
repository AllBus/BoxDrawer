package turtoise

import turtoise.memory.keys.MemoryKey

abstract class TurtoiseParserStackItem(
//    /** Тип скобок '(','[','{'*/
//    val skobka: Char = ' ',
//    val argument: String = "",
) {

    companion object {
        const val ARGUMENT_NAME = '~'
    }

    abstract fun isArgument(): Boolean
    open val argument: MemoryKey = MemoryKey.EMPTY
    abstract val name: MemoryKey
    abstract val line: String

    abstract val inner: List<TurtoiseParserStackItem>
    abstract val blocks: List<TurtoiseParserStackItem>

    abstract fun get(index: Int): MemoryKey?

    abstract fun get(index: String): MemoryKey?

    open fun doubleValue(index: Int, defaultValue: Double): Double {
        val sv = stringValue(index)
        return sv?.toDoubleOrNull() ?: defaultValue
    }

    abstract fun stringValue(index: Int): String?
    fun asDouble(text: String?): Double {
        return text?.toDoubleOrNull() ?: 0.0
    }

    fun asDouble(text: String?, defaultValue: Double): Double {
        return text?.toDoubleOrNull() ?: defaultValue
    }

    abstract fun getInnerAtName(name:String): TurtoiseParserStackItem?

    abstract fun arguments(): List<MemoryKey>

    abstract val size: Int
}

class TurtoiseParserStackArgument(
    override val argument: MemoryKey,
) : TurtoiseParserStackItem() {
    override fun isArgument(): Boolean = true


    override val name: MemoryKey = argument

    override val inner: List<TurtoiseParserStackItem>
        get() = emptyList()

    override val blocks: List<TurtoiseParserStackItem>
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

    override fun getInnerAtName(name: String): TurtoiseParserStackItem? {
        return null
    }

    override fun arguments(): List<MemoryKey> {
        return listOf(argument)
    }

    override val size: Int
        get() = 1

    override val line: String get() = argument.name
}

class TurtoiseParserStackBlock(
    /** Тип скобок '(','[','{'*/
    val skobka: Char = ' ',
) : TurtoiseParserStackItem() {
    override fun isArgument(): Boolean = false

    override val inner = mutableListOf<TurtoiseParserStackItem>()
    override val blocks = mutableListOf<TurtoiseParserStackBlock>()

    override val argument: MemoryKey
        get() = arguments().getOrNull(1)?: MemoryKey.EMPTY

    override val name
        get() = inner.firstOrNull()?.argument ?: MemoryKey.BLOCK
    override val line: String
        get() = inner.joinToString(" ", "$skobka", "${closeBrace()}") { it.line }

    fun closeBrace(): Char {
        return TortoiseParser.closeBrace(skobka)
    }

    fun add(argument: String) {
        inner.add(
            TurtoiseParserStackArgument(
                argument = MemoryKey(argument)
            )
        )
    }

    fun add(arguments: List<String>) {
        arguments.forEach { add(it) }
    }

    fun add(argument: TurtoiseParserStackBlock) {
        inner.add(argument)
        blocks.add(argument)
    }

    fun addItems(values: List<TurtoiseParserStackItem>) {
        inner.addAll(values)
        blocks.addAll(values.filterIsInstance<TurtoiseParserStackBlock>())
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
        return if (index.startsWith(".")){
            val i = index.indexOf('.', 1)
            if (i>0){
                val n = index.take(i).drop(1)
                val next = index.drop(i)
                when (val a = getInnerAtName(n)){
                    is TurtoiseParserStackBlock ->
                        a.get(next)
                    else ->
                        null
                }
            } else {
                val n = index.drop(1)

                getInnerAtName(n)?.argument
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

    fun getBlockAtName(name:String): TurtoiseParserStackBlock?{
        val nm = MemoryKey(name)
        return blocks.find {
            it.name == nm
        }
    }

    override fun getInnerAtName(name:String): TurtoiseParserStackItem?{
        val i = name.toIntOrNull()
        return if (i != null)
            inner.getOrNull(i)
        else
            getBlockAtName(name)
    }
}