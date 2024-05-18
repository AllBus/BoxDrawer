package turtoise.memory.keys

import kotlin.math.PI
import kotlin.math.acos


abstract class FunctionMemoryKey(
) : MemoryKey, ICalculatorMemoryKey {
    override fun isNotEmpty(): Boolean = true
    override fun toDoubleOrNull(): Double? = null

    override fun isCalculator(): Boolean = true
    override fun prefix(): Char = '='
    abstract override fun calculate(value: (MemoryKey) -> Double): Double

    override fun drop(): MemoryKey = this
}

class SqrMemoryKey(
    val keys: List<MemoryKey>
) : FunctionMemoryKey() {

    override fun calculate(value: (MemoryKey) -> Double): Double {
        return keys.map { value(it) }.sumOf { it * it }
    }

    override val name: String
        get() = keys.joinToString(" ", "(sqr ", ")") { it.name }


}

class TriangleAngleMemoryKey(
    val a: MemoryKey,
    val b: MemoryKey,
    val c: MemoryKey,
) : FunctionMemoryKey() {

    override fun calculate(value: (MemoryKey) -> Double): Double {
        val aa = value(a)
        val bb = value(b)
        val cc = value(c)

        val v = acos(
            (bb * bb + cc * cc - aa * aa) / (2 * bb * cc)
        )
        return if (v.isFinite())
            v
        else 0.0
    }

    override val name: String
        get() = "(tri ${a.name} ${b.name} ${c.name})"
}

class DividerMemoryKey(
    val a: MemoryKey,
    val divide: Double,
) : FunctionMemoryKey() {
    override fun calculate(value: (MemoryKey) -> Double): Double {
        val v = value(a) / divide
        return if (v.isFinite())
            v
        else 0.0
    }

    override val name: String
        get() = "(/ ${a.name} $divide)"
}

class MultiplicationMemoryKey(
    val a: MemoryKey,
    val mult: Double,
) : FunctionMemoryKey() {
    override fun calculate(value: (MemoryKey) -> Double): Double {
        return value(a) * mult
    }

    override val name: String
        get() = "(* ${a.name} $mult)"
}

class SumMemoryKey(
    val keys: List<MemoryKey>,
) : FunctionMemoryKey() {
    override fun calculate(value: (MemoryKey) -> Double): Double {
        return keys.sumOf { value(it) }
    }

    override val name: String
    get() = keys.joinToString(" ", "(+ ", ")") { it.name }
}

class NegativeMemoryKey(
    val key: MemoryKey,
) : FunctionMemoryKey() {
    override fun calculate(value: (MemoryKey) -> Double): Double {
        return -value(key)
    }

    override val name: String
        get() = "(- ${key.name})"
}

/** Задание в градусах значения*/
class DegreesMemoryKey(
    val key: MemoryKey,
) : FunctionMemoryKey() {
    override fun calculate(value: (MemoryKey) -> Double): Double {
        return value(key) * PI / 180.0
    }

    override val name: String
        get() = "(deg ${key.name})"
}

class RadiansToDegreesMemoryKey(
    val key: MemoryKey,
) : FunctionMemoryKey() {
    override fun calculate(value: (MemoryKey) -> Double): Double {
        return value(key) * 180.0 / PI
    }

    override val name: String
        get() = "(rad ${key.name})"
}


class MulMemoryKey(
    val a: MemoryKey,
    val b: MemoryKey,
) : FunctionMemoryKey() {
    override fun calculate(value: (MemoryKey) -> Double): Double {
        return value(a) * value(b)
    }

    override val name: String
        get() = "(* ${a.name} ${b.name})"
}

class DivMemoryKey(
    val a: MemoryKey,
    val b: MemoryKey,
) : FunctionMemoryKey() {
    override fun calculate(value: (MemoryKey) -> Double): Double {
        return value(a) / value(b)
    }

    override val name: String
        get() = "(/ ${a.name} ${b.name})"
}

class SummaMemoryKey(
    val a: MemoryKey,
    val b: Double
) : FunctionMemoryKey() {
    override fun calculate(value: (MemoryKey) -> Double): Double {
        return value(a) + b
    }

    override val name: String
        get() = "(+ ${a.name} ${b})"
}

class MinusMemoryKey(
    val a: MemoryKey,
    val b: MemoryKey,
) : FunctionMemoryKey() {
    override fun calculate(value: (MemoryKey) -> Double): Double {
        return value(a) - value(b)
    }

    override val name: String
        get() = "(- ${a.name} ${b.name})"
}