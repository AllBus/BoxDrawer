package turtoise.memory.keys

import turtoise.parser.TortoiseParserStackBlock
import kotlin.math.PI

//@JvmInline
//value
interface MemoryKey {
    val name: String
    fun isNotEmpty(): Boolean
    fun toDoubleOrNull(): Double?
    fun prefix(): Char
    fun drop(): MemoryKey
    fun isCalculator(): Boolean

    companion object {
        val EMPTY = SpecialMemoryKey("")
        val BLOCK = SpecialMemoryKey("%%")
        val ZERO = DoubleMemoryKey(0.0)
        val ONE = DoubleMemoryKey(1.0)
        val PI2 = DoubleMemoryKey(PI / 2.0)

        fun create(name: String): MemoryKey {
            return invoke(name)
        }

        operator fun invoke(name: String): MemoryKey {
            return StringMemoryKey(name)
        }

        operator fun invoke(value: TortoiseParserStackBlock): MemoryKey {
            return StackBlockMemoryKey(value)
        }

        inline fun MemoryKey.ifEmpty(action: () -> MemoryKey): MemoryKey =
            if (this.isNotEmpty())
                this
            else
                action()

        inline operator fun MemoryKey.div(value: Double): MemoryKey =
            DividerMemoryKey(
                this,
                value
            )

        inline operator fun MemoryKey.times(value: Double): MemoryKey =
            MultiplicationMemoryKey(
                this,
                value
            )

        inline operator fun MemoryKey.plus(value: MemoryKey): MemoryKey =
            SumMemoryKey(
                listOf(
                    this,
                    value
                )
            )

        inline operator fun MemoryKey.unaryMinus(): MemoryKey =
            NegativeMemoryKey(this)

        inline fun MemoryKey?.orEmpty(): MemoryKey {
            return this ?: EMPTY
        }
    }

}

interface ICalculatorMemoryKey {
    fun calculate(value: (MemoryKey) -> Double): Double
}


