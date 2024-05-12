package turtoise.memory.keys

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
}