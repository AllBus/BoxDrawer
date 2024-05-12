package turtoise.memory

import kotlin.math.acos



abstract class FunctionMemoryKey(
    override val keys: List<MemoryKey>
) : MemoryKey, ICalculatorMemoryKey {
    override fun isNotEmpty(): Boolean = true
    override fun toDoubleOrNull(): Double? = null

    override fun isCalculator(): Boolean = true
    override fun prefix(): Char = '='
    abstract override fun calculate(value: List<Double>): Double

    override fun drop(): MemoryKey = this
}

class ACosMemoryKey(
    key: MemoryKey
) : FunctionMemoryKey(listOf(key)) {

    override fun calculate(value: List<Double>): Double {
        return acos(value.first())
    }

    override val name: String
        get() = "==arccos"
}

class SqrMemoryKey(
    keys: List<MemoryKey>
) : FunctionMemoryKey(keys) {

    override fun calculate(value: List<Double>): Double {
        return value.map { it * it }.sum()
    }

    override val name: String
        get() = "==sqr"
}

class TriangleAngleMemoryKey(
    a: MemoryKey,
    b: MemoryKey,
    c: MemoryKey,
) : FunctionMemoryKey(listOf(a, b, c)) {

    override fun calculate(value: List<Double>): Double {
        return acos(
            (value[1] * value[1] + value[2] * value[2] - value[0] * value[0]) / (2 * value[1] * value[2])
        )
    }

    override val name: String
        get() = "==triangle"
}

class DividerMemoryKey(
    a: MemoryKey,
    val divide: Double,
) : FunctionMemoryKey(listOf(a)) {
    override fun calculate(value: List<Double>): Double {
        return value[0] / divide
    }

    override val name: String
        get() = "==/($divide)"
}

class MultiplicationMemoryKey(
    a: MemoryKey,
    val mult: Double,
) : FunctionMemoryKey(listOf(a)) {
    override fun calculate(value: List<Double>): Double {
        return value[0] * mult
    }

    override val name: String
        get() = "==*($mult)"
}

class SumMemoryKey(
    keys: List<MemoryKey>,
) : FunctionMemoryKey(keys) {
    override fun calculate(value: List<Double>): Double {
        return value.sum()
    }

    override val name: String
        get() = "==+()"
}

class NegativeMemoryKey(
    key: MemoryKey,
) : FunctionMemoryKey(listOf(key)) {
    override fun calculate(value: List<Double>): Double {
        return -value.first()
    }

    override val name: String
        get() = "==-"
}