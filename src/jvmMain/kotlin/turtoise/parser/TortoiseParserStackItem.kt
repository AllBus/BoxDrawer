package turtoise.parser

import turtoise.memory.keys.MemoryKey

abstract class TortoiseParserStackItem(
//    /** Тип скобок '(','[','{'*/
//    val skobka: Char = ' ',
//    val argument: String = "",
) {

    companion object {
        const val ARGUMENT_NAME = '~'
    }

    abstract fun isArgument(): Boolean
    abstract val argument: MemoryKey
    abstract val value: MemoryKey
    abstract val name: MemoryKey
    abstract val line: String
    abstract val innerLine: String

    abstract val inner: List<TortoiseParserStackItem>
    abstract val blocks: List<TortoiseParserStackItem>

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

    abstract fun getInnerAtName(name: String): TortoiseParserStackItem?

    abstract fun arguments(): List<MemoryKey>

    abstract val size: Int

    fun<T> map(block: (MemoryKey) -> T): List<T>{
        val result = mutableListOf<T>()
        for (i in 0 until size){
            val v = get(i)
            if (v != null){
                result.add(block(v))
            }
        }
        return result.toList()
    }

    fun<T> mapValues(block: (MemoryKey) -> T): List<T>{
        val result = mutableListOf<T>()
        for (i in 1 until size){
            val v = get(i)
            if (v != null){
                result.add(block(v))
            }
        }
        return result.toList()
    }
}

