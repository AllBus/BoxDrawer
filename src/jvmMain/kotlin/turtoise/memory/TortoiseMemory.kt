package turtoise.memory

import turtoise.memory.keys.MemoryKey

interface TortoiseMemory {
    fun value(variable: MemoryKey, defaultValue: Double): Double
    fun assign(variable: MemoryKey, value: Double)
    fun clear(variable: MemoryKey)

    fun reset()
}