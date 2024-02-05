package turtoise.memory

interface TortoiseMemory {
    fun value(variable: String, defaultValue: Double): Double
    fun assign(variable: String, value: Double)
    fun clear(variable: String)

    fun reset()
}