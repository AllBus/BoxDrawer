package turtoise.rect

import turtoise.memory.MemoryKey
import turtoise.memory.TortoiseMemory
import vectors.Vec2

/**
 * podoshva дли стороны которой держится на родителе
 */
class Reka(
    var podoshva: MemoryKey,
) {
    val storoni = mutableListOf<RekaStorona>()

    fun recalculate(memory: TortoiseMemory){
        val pv = memory.value(podoshva, 0.0)
        var alpha = 0.0
        var current = Vec2(pv/2, 0.0)
        val points = mutableListOf(current)
        storoni.forEach{storona ->
            alpha += memory.value( storona.angle, 0.0)
            val ps = memory.value(storona.length, 0.0)
            current += Vec2( ps, 0.0).rotate(alpha)
            points.add(current)
        }

        points.add(Vec2(-pv/2, 0.0))
    }

    var points: List<Vec2> = emptyList()
        private set

}

/**
 * @param length mm
 * @param angle radians
 */
class RekaStorona(
    var length:MemoryKey,
    var angle:MemoryKey,
    var visible:Boolean = true,
){
    val kubikiL = mutableListOf<Kubik>()
    val kubikiR = mutableListOf<Kubik>()
    val kubikiCL = mutableListOf<Kubik>()
    val kubikiCR = mutableListOf<Kubik>()
}

class Kubik(
    var padding: MemoryKey,
    var figure: Reka,
)