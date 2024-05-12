package turtoise.memory.keys

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