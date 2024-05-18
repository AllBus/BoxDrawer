package turtoise.memory.keys

import turtoise.parser.TortoiseParserStackBlock
import turtoise.memory.MemoryKeyBuilder.constructFunction

class StackBlockMemoryKey(val block: TortoiseParserStackBlock): MemoryKey, ICalculatorMemoryKey {
    override val name: String
        get() = block.line

    override fun isNotEmpty(): Boolean = true

    override fun toDoubleOrNull(): Double? = null

    override fun prefix(): Char = '('

    override fun drop(): MemoryKey = this

    override fun isCalculator(): Boolean = true

    override fun calculate(value: (MemoryKey) -> Double): Double {
        return value(constructFunction(block))
    }

}