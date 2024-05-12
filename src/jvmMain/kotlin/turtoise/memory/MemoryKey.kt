package turtoise.memory

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
        fun create(name: String): MemoryKey {
            return StringMemoryKey(name)
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
    fun calculate(value: List<Double>): Double
    val keys: List<MemoryKey>
}


data class StringMemoryKey(
    override val name: String
) : MemoryKey {
    override fun isNotEmpty(): Boolean {
        return name.isNotEmpty()
    }

    override fun toDoubleOrNull(): Double? {
        return name.toDoubleOrNull()
    }

    override fun prefix(): Char = name[0]

    override fun drop(): MemoryKey {
        return StringMemoryKey(name.drop(1))
    }

    override fun isCalculator(): Boolean = false
}

data class MemoryKeyWithDefault(
    val key: MemoryKey,
    val defaultValue: Double
) : MemoryKey, ICalculatorMemoryKey {
    override val name: String
        get() = key.name

    override fun isNotEmpty(): Boolean {
        return key.isNotEmpty()
    }

    override fun toDoubleOrNull(): Double {
        return key.toDoubleOrNull() ?: defaultValue
    }

    override fun prefix(): Char = key.prefix()

    override fun drop(): MemoryKey {
        return MemoryKeyWithDefault(key.drop(), defaultValue)
    }

    override fun isCalculator(): Boolean = key.isCalculator()

    override fun calculate(value: List<Double>): Double {
        return if (key is ICalculatorMemoryKey) {
            key.calculate(value)
        } else
            defaultValue
    }

    override val keys: List<MemoryKey>
        get() = if (key is ICalculatorMemoryKey)
            key.keys
        else emptyList()
}


data class SpecialMemoryKey(
    override val name: String
) : MemoryKey {

    override fun isNotEmpty(): Boolean = false

    override fun toDoubleOrNull(): Double? = null

    override fun prefix(): Char = ' '

    override fun drop(): MemoryKey = this

    override fun isCalculator(): Boolean = false
}

data class DoubleMemoryKey(
    val value: Double
) : MemoryKey {
    override val name: String
        get() = value.toString()

    override fun isNotEmpty(): Boolean = true

    override fun toDoubleOrNull(): Double = value

    override fun prefix(): Char = ' '

    override fun drop(): MemoryKey = this

    override fun isCalculator(): Boolean = false

    companion object {
        val PI2 = DoubleMemoryKey(PI / 2.0)
    }
}