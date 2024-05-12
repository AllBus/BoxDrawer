package turtoise.rect

import turtoise.memory.TortoiseMemory
import turtoise.memory.keys.MemoryKey
import turtoise.rect.Kubik.Companion.STORONA_CL
import turtoise.rect.Kubik.Companion.STORONA_CR
import turtoise.rect.Kubik.Companion.STORONA_L
import turtoise.rect.Kubik.Companion.STORONA_R
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
    val kubikiL = KubikGroup(STORONA_L)
    val kubikiR = KubikGroup(STORONA_R)
    val kubikiCL = KubikGroup(STORONA_CL)
    val kubikiCR = KubikGroup(STORONA_CR)
    val kubiki = listOf(kubikiL, kubikiCL, kubikiCR, kubikiR)
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
    }
}

class KubikGroup(
    val napravlenie: Int,
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