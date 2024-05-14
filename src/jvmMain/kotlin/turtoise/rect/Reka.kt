package turtoise.rect

import turtoise.TurtoiseParserStackBlock
import turtoise.memory.TortoiseMemory
import turtoise.memory.keys.MemoryKey
import vectors.Vec2
import kotlin.math.PI

/**
 * podoshva дли стороны которой держится на родителе
 */
class Reka(
    var podoshva: MemoryKey,
) {
    val storoni = mutableListOf<RekaStorona>()

    fun recalculate(memory: TortoiseMemory) {
        val pv = memory.value(podoshva, 0.0)
        var alpha = 0.0
        var current = Vec2(pv / 2, 0.0)
        val points = mutableListOf(current)
        storoni.forEach { storona ->
            alpha += (PI - memory.value(storona.angle, 0.0))
            val ps = memory.value(storona.length, 0.0)
            current += Vec2(ps, 0.0).rotate(alpha)
            points.add(current)
        }

        points.add(Vec2(-pv / 2, 0.0))
        this.points = points
    }

    fun recalculate(newPoints: List<Vec2>) {
        points = newPoints
    }

    fun remove(reka: Reka) {
        storoni.forEach { s ->
            s.kubiki.forEach { k ->
                k.group.removeIf { v -> v.reka === reka }
            }
        }
    }

    var points: List<Vec2> = emptyList()
        private set

}

/**
 * @param length mm
 * @param angle radians
 */
class RekaStorona(
    var length: MemoryKey,
    var angle: MemoryKey,
    var visible: Boolean = true,
) {
    val kubiki = mutableListOf<KubikGroup>()

    val kubkikiSize: Int get() = kubiki.sumOf { it.size }

    fun add(kg: KubikGroup) {
        val group = kubiki.find { it.napravlenie == kg.napravlenie }
        if (group == null) {
            kubiki.add(kg)
        } else {
            group.group.addAll(kg.group)
        }
    }

    fun add(napravlenie: KubikBias, index:Int,  value: Kubik) {
        val group = kubiki.find { it.napravlenie == napravlenie }
        if (group == null) {
            val kg = KubikGroup(napravlenie)
            kg.add(index, value)
            kubiki.add(kg)
        } else {
            group.add(index, value)
        }
    }
}

class Kubik(
    var padding: MemoryKey,
    var reka: Reka,
) {
    companion object {
        const val STORONA_L = 1
        const val STORONA_CL = 2
        const val STORONA_CR = 3
        const val STORONA_R = 4
        const val STORONA_C = 5

        val biasLeft = KubikBias(0, 1, EBiasNapravlenie.RIGHT)
        val biasRight = KubikBias(1, 1, EBiasNapravlenie.LEFT)
        val biasCenter = KubikBias(1, 2, EBiasNapravlenie.CENTER)
        val biasCenterLeft = KubikBias(1, 2, EBiasNapravlenie.LEFT)
        val biasCenterRight = KubikBias(1, 2, EBiasNapravlenie.RIGHT)

        fun toBias(storona: Int): KubikBias {
            return when (storona) {
                STORONA_L -> biasLeft
                STORONA_CL -> biasCenterLeft
                STORONA_CR -> biasCenterRight
                STORONA_R -> biasRight
                STORONA_C -> biasCenter
                else -> biasCenter
            }
        }
    }
}

class KubikGroup(
    val napravlenie: KubikBias,
) {
    fun add(index: Int, newKubik: Kubik) {
        if (index >= 0 && index < group.size)
            group.add(index, newKubik)
        else
            group.add(newKubik)
    }

    val group: MutableList<Kubik> = mutableListOf<Kubik>()

    val size: Int get() = group.size
}

data class KubikBias(
    val biasT: Int,
    val biasB: Int,
    val napravlenie: EBiasNapravlenie,
) {
    val offset: Double get() = biasT * 1.0 / biasB

    override fun toString(): String {
        return "$biasT/$biasB~$napravlenie"
    }

    fun print(): TurtoiseParserStackBlock {
        val tp = TurtoiseParserStackBlock()
        tp.add("${napravlenie.value}")
        tp.add("${biasT}")
        tp.add("${biasB}")

        return tp
    }
}

enum class EBiasNapravlenie(val value: Int) {
    LEFT(-1), RIGHT(1), CENTER(0)
}