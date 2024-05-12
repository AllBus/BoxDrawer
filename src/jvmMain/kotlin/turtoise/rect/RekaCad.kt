package turtoise.rect

import com.kos.boxdrawe.presentation.RectBlockPosition
import com.kos.figure.FigureList
import com.kos.figure.FigurePolyline
import com.kos.figure.IFigure
import com.kos.figure.composition.FigureColor
import turtoise.memory.TortoiseMemory
import turtoise.memory.keys.DegreesMemoryKey
import turtoise.memory.keys.MemoryKey
import turtoise.memory.keys.TriangleAngleMemoryKey
import turtoise.rect.Kubik.Companion.STORONA_CL
import turtoise.rect.Kubik.Companion.STORONA_CR
import turtoise.rect.Kubik.Companion.STORONA_L
import turtoise.rect.Kubik.Companion.STORONA_R
import vectors.Vec2
import kotlin.math.PI

object RekaCad {

    fun newReka(tv: List<String>, memory: TortoiseMemory): Reka {
        val reka = if (tv.isNotEmpty()) {
            val currentReka = Reka(
                MemoryKey(tv.first())
            )
            currentReka.storoni.addAll(rekaPoints(tv))
            currentReka
        } else
            Reka(
                MemoryKey.EMPTY
            )

        reka.recalculate(memory)
        return reka
    }

    fun rekaPoints(tv: List<String>): List<RekaStorona> {
        val st = when (tv.size) {
            2 -> {
                listOf(
                    RekaStorona(
                        length = MemoryKey(tv[1]),
                        angle = MemoryKey.PI2
                    ),
                    RekaStorona(
                        length = MemoryKey(tv[0]),
                        angle = MemoryKey.PI2
                    ),
                    RekaStorona(
                        length = MemoryKey(tv[1]),
                        angle = MemoryKey.PI2
                    ),
                )
            }

            3 -> {

                val ma = MemoryKey(tv[0])
                val mb = MemoryKey(tv[1])
                val mc = MemoryKey(tv[2])

                listOf(
                    RekaStorona(
                        length = mb,
                        angle = TriangleAngleMemoryKey(mc, ma, mb)
                    ),
                    RekaStorona(
                        length = mc,
                        angle = TriangleAngleMemoryKey(ma, mb, mc)
                    ),
                )
            }

            1 -> {
                listOf(
                    RekaStorona(
                        length = MemoryKey(tv[0]),
                        angle = MemoryKey.PI2
                    ),
                    RekaStorona(
                        length = MemoryKey(tv[0]),
                        angle = MemoryKey.PI2
                    ),
                    RekaStorona(
                        length = MemoryKey(tv[0]),
                        angle = MemoryKey.PI2
                    ),
                )
            }

            else -> {
                tv.drop(1).windowed(2, 2).map { v ->
                    RekaStorona(
                        length = MemoryKey(v[0]),
                        angle = DegreesMemoryKey(MemoryKey(v[1])),
                    )
                }
            }
        }
        return st
    }

    fun createFigure(
        top: Reka,
        center: Vec2,
        angle: Double,
        cur: RectBlockPosition,
        memory: TortoiseMemory
    ): IFigure {

        val p = FigurePolyline(top.points.map { it.rotate(angle) + center })
        val p2 = if (top === cur.reka) {
            FigureColor(0xff00ff, p)
        } else {
            p
        }

        val pv = memory.value(top.podoshva, 0.0)

        var a = angle
        var current = center + Vec2(pv / 2, 0.0)
        top.storoni.forEach { storona ->
            a += (PI - memory.value(storona.angle, 0.0))
            val ps = memory.value(storona.length, 0.0)

            storona.kubiki.forEach { kubik ->

                val np = when (kubik.napravlenie) {
                    STORONA_L -> Vec2(0.0, 0.0)
                    STORONA_CL -> Vec2(ps / 2, 0.0)
                    STORONA_CR -> Vec2(ps / 2, 0.0)
                    STORONA_R -> Vec2(ps, 0.0)
                    else -> Vec2(0.0, 0.0)
                }.rotate(a)

                val nap = when (kubik.napravlenie) {
                    STORONA_L, STORONA_CL -> Vec2(1.0, 0.0)
                    STORONA_CR, STORONA_R -> Vec2(-1.0, 0.0)
                    else -> Vec2(1.0, 0.0)
                }.rotate(a)

                var kubikStart = current + np

                kubik.group.forEach { block ->
                    kubikStart+= nap*memory.value(block.padding, 0.0)
                    createFigure(block.reka, kubikStart, a, cur, memory)
                }
            }

            current += Vec2(ps, 0.0).rotate(a)
        }

        return FigureList(listOf(p2))
    }
}