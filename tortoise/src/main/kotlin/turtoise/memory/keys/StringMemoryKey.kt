package turtoise.memory.keys

data class StringMemoryKey(
    override val name: String
) : MemoryKey {
    override fun isNotEmpty(): Boolean {
        return name.isNotEmpty()
    }

    override fun toDoubleOrNull(): Double? {
        return name.toDoubleOrNull()
    }

    override fun prefix(): Char = if (name.isNotEmpty()) name[0] else ' '

    override fun drop(): MemoryKey {
        return StringMemoryKey(name.drop(1))
    }

    override fun isCalculator(): Boolean = false
}