package turtoise.memory.keys

data class SpecialMemoryKey(
    override val name: String
) : MemoryKey {

    override fun isNotEmpty(): Boolean = false

    override fun toDoubleOrNull(): Double? = null

    override fun prefix(): Char = ' '

    override fun drop(): MemoryKey = this

    override fun isCalculator(): Boolean = false
}