package turtoise.memory

import turtoise.TurtoiseParserStackArgument
import turtoise.TurtoiseParserStackBlock
import turtoise.TurtoiseParserStackItem
import turtoise.memory.keys.ACosMemoryKey
import turtoise.memory.keys.ASinMemoryKey
import turtoise.memory.keys.ATanMemoryKey
import turtoise.memory.keys.CoTanMemoryKey
import turtoise.memory.keys.CosMemoryKey
import turtoise.memory.keys.DegreesMemoryKey
import turtoise.memory.keys.DivMemoryKey
import turtoise.memory.keys.MemoryKey
import turtoise.memory.keys.MinusMemoryKey
import turtoise.memory.keys.MulMemoryKey
import turtoise.memory.keys.NegativeMemoryKey
import turtoise.memory.keys.RadiansToDegreesMemoryKey
import turtoise.memory.keys.SinMemoryKey
import turtoise.memory.keys.SqrMemoryKey
import turtoise.memory.keys.SumMemoryKey
import turtoise.memory.keys.TanMemoryKey
import turtoise.memory.keys.TriangleAngleMemoryKey

object MemoryKeyBuilder {

    fun constructFunction(item: TurtoiseParserStackBlock): MemoryKey {
        return if (item.inner.size>=2) {
            when (item.name.name) {
                "cos" -> CosMemoryKey(createMemoryKey(item.inner[1]))
                "sin" -> SinMemoryKey(createMemoryKey(item.inner[1]))
                "tg", "tan" -> TanMemoryKey(createMemoryKey(item.inner[1]))
                "ctg", "ctan" -> CoTanMemoryKey(createMemoryKey(item.inner[1]))
                "acos" -> ACosMemoryKey(createMemoryKey(item.inner[1]))
                "asin" -> ASinMemoryKey(createMemoryKey(item.inner[1]))
                "atan" -> ATanMemoryKey(createMemoryKey(item.inner[1]))
                "deg" -> DegreesMemoryKey(createMemoryKey(item.inner[1]))
                "rad" -> RadiansToDegreesMemoryKey(createMemoryKey(item.inner[1]))
                "sqr" -> SqrMemoryKey(item.inner.drop(1).map(::createMemoryKey))
                "tri" -> {
                    if (item.inner.size >= 4) {
                        val a = createMemoryKey(item.inner[1])
                        val b = createMemoryKey(item.inner[2])
                        val c = createMemoryKey(item.inner[3])
                        TriangleAngleMemoryKey(a, b, c)
                    } else {
                        val a = createMemoryKey(item.inner[1])
                        TriangleAngleMemoryKey(a, a, a)
                    }
                }
                "-" -> if (item.inner.size <= 2)
                    NegativeMemoryKey(createMemoryKey(item.inner[1])) else
                    MinusMemoryKey(createMemoryKey(item.inner[1]), createMemoryKey(item.inner[2]))

                "+" -> SumMemoryKey(item.inner.drop(1).map(::createMemoryKey))
                "/" -> if (item.inner.size >=3) DivMemoryKey(createMemoryKey(item.inner[1]), createMemoryKey(item.inner[2])) else
                    DivMemoryKey(MemoryKey.ONE, createMemoryKey(item.inner[1]))
                "*" -> if (item.inner.size >=3) MulMemoryKey(createMemoryKey(item.inner[1]), createMemoryKey(item.inner[2]))
                    else createMemoryKey(item.inner[1])
                else -> item.name
            }
        } else
            item.name
    }

    fun createMemoryKey(item: TurtoiseParserStackItem?): MemoryKey {
        return when (item) {
            is TurtoiseParserStackBlock -> constructFunction(item)
            is TurtoiseParserStackArgument -> item.argument
            else -> MemoryKey.ZERO
        }
    }
}