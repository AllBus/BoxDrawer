package turtoise.memory.keys

import java.lang.Math.tan
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.sin

class ACosMemoryKey(
    val key: MemoryKey
) : FunctionMemoryKey() {

    override fun calculate(value: (MemoryKey) -> Double): Double {
        return acos(value(key))
    }

    override val name: String
        get() = "==arccos"
}

class ASinMemoryKey(
    val key: MemoryKey
) : FunctionMemoryKey() {

    override fun calculate(value: (MemoryKey) -> Double): Double {
        return asin(value(key))
    }

    override val name: String
        get() = "==arcsin"
}

class ATanMemoryKey(
    val key: MemoryKey
) : FunctionMemoryKey() {

    override fun calculate(value: (MemoryKey) -> Double): Double {
        return atan(value(key))
    }

    override val name: String
        get() = "==arctg"
}


class CosMemoryKey(
    val key: MemoryKey
) : FunctionMemoryKey() {

    override fun calculate(value: (MemoryKey) -> Double): Double {
        return cos(value(key))
    }

    override val name: String
        get() = "==cos"
}

class SinMemoryKey(
    val key: MemoryKey
) : FunctionMemoryKey() {

    override fun calculate(value: (MemoryKey) -> Double): Double {
        return sin(value(key))
    }

    override val name: String
        get() = "==sin"
}

class TanMemoryKey(
    val key: MemoryKey
) : FunctionMemoryKey() {

    override fun calculate(value: (MemoryKey) -> Double): Double {
        val v = tan(value(key))
        return if (v.isFinite())
            v
        else 0.0
    }

    override val name: String
        get() = "==tg"
}

class CoTanMemoryKey(
    val key: MemoryKey
) : FunctionMemoryKey() {

    override fun calculate(value: (MemoryKey) -> Double): Double {
        val v = 1.0/tan(value(key))
        return if (v.isFinite())
            v
        else 0.0
    }

    override val name: String
        get() = "==ctg"
}