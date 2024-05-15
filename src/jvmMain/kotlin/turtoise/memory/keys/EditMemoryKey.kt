package turtoise.memory.keys

class EditMemoryKey(
    val mainKey: MemoryKey,
    val value:Double,
    val calkKey: MemoryKey
): MemoryKey, ICalculatorMemoryKey {
    override val name: String
        get() = calkKey.name

    override fun isNotEmpty(): Boolean = calkKey.isNotEmpty()

    override fun toDoubleOrNull(): Double? = calkKey.toDoubleOrNull()

    override fun prefix(): Char = calkKey.prefix()

    override fun drop(): MemoryKey = calkKey.drop()

    override fun isCalculator(): Boolean = calkKey.isCalculator()

    override fun calculate(value: (MemoryKey) -> Double): Double {
        return if (calkKey is ICalculatorMemoryKey){
            calkKey.calculate(value)
        } else
            0.0
    }
}