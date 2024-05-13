package turtoise.rect

import com.kos.boxdrawe.presentation.RectBlockPosition
import com.kos.figure.FigureCircle
import com.kos.figure.FigureLine
import com.kos.figure.FigureList
import com.kos.figure.IFigure
import com.kos.figure.composition.FigureColor
import turtoise.memory.TortoiseMemory
import turtoise.memory.keys.DegreesMemoryKey
import turtoise.memory.keys.MemoryKey
import turtoise.memory.keys.TriangleAngleMemoryKey
import vectors.Vec2
import kotlin.math.PI

object RekaCad {

    fun updateReka(reka: Reka, tv: List<String>, memory: TortoiseMemory): Reka {
        if (tv.isNotEmpty()) {
            reka.podoshva = MemoryKey(tv.first())
            reka.storoni.clear()
            reka.storoni.addAll(rekaPoints(tv))
        }

        reka.recalculate(memory)
        return reka
    }

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
        memory: TortoiseMemory,
        points: MutableList<Vec2>
    ): IFigure {

        val tek = top === cur.reka
        var a = angle

        val pv = memory.value(top.podoshva, 0.0)

        /* Вспомогательные фигуры */
        val av = -angle * 180 / PI
        val cn = listOf(
            FigureColor(
                if (tek) 0xff00ff else 0xffff00,
                FigureCircle(center, 2.5, segmentStart = av - 180f, segmentEnd = av),
            ),
        )


        var current = center + Vec2(pv / 2, 0.0).rotate(a)
        //   var points = mutableListOf(current)
        points.add(current)
        val ps = top.storoni.flatMapIndexed { i, storona ->
            a += (PI - memory.value(storona.angle, 0.0))
            val ps = memory.value(storona.length, 0.0)

            val pk = storona.kubiki.flatMap { kubik ->
                val fullLength = kubik.group.sumOf { block ->
                    val pod = memory.value(block.reka.podoshva, 0.0)
                    val pad = memory.value(block.padding, 0.0)
                    pod + pad
                }

                val np = (ps * kubik.napravlenie.offset +
                        when (kubik.napravlenie.napravlenie) {
                            EBiasNapravlenie.LEFT -> 0.0
                            EBiasNapravlenie.RIGHT -> 0.0
                            EBiasNapravlenie.CENTER -> -fullLength / 2
                        })


                val nap = when (kubik.napravlenie.napravlenie) {
                    EBiasNapravlenie.LEFT -> -1.0
                    EBiasNapravlenie.RIGHT -> 1.0
                    EBiasNapravlenie.CENTER -> 1.0
                }

                var kubikStart = np

                val drawAngle = PI + a
                kubik.group.mapIndexed { index, block ->
                    val pod = memory.value(block.reka.podoshva, 0.0)
                    val pad = memory.value(block.padding, 0.0)
                    val p = KubikDrawPosition(
                        position = kubikStart + nap * (pod / 2 + pad),
                        positionAngle = a,
                        angle = drawAngle,
                        kubik = block,
                        index = index,
                        bias = kubik.napravlenie,
                    )
                    kubikStart += nap * (pod + pad)
                    p
                } // end block
                // end kubik
            }.sortedBy {
                it.position
            }.flatMap { kdp ->
                val kubikStart = current + kdp.vec
                listOfNotNull(
                    if (kdp.index == cur.position.block && kdp.bias == cur.position.storona) {
                        FigureColor(
                            0xff00ff,
                            FigureLine(
                                kubikStart + Vec2(-5.0, 0.0).rotate(kdp.angle),
                                kubikStart + Vec2(5.0, 0.0).rotate(kdp.angle)
                            )
                        )
                    } else null,

                    createFigure(
                        top = kdp.kubik.reka,
                        center = kubikStart,
                        angle = kdp.angle,
                        cur = cur,
                        memory = memory,
                        points = points
                    )
                )
            }

            /* Вспомогательные фигуры */
            val pt = if (tek && cur.position.edge == i + 1) {
                listOf(
                    FigureColor(
                        0xff00ff,
                        FigureLine(
                            current + Vec2(0.0, 1.0).rotate(a),
                            current + Vec2(ps, 1.0).rotate(a)
                        )
                    )
                )
            } else emptyList()

            current += Vec2(ps, 0.0).rotate(a)

            points += current
            pk + pt
        } //end storona

        return FigureList(ps + cn)
    }

    class KubikDrawPosition(
        val position: Double,
        val positionAngle: Double,
        val angle: Double,
        val kubik: Kubik,
        val index: Int,
        val bias: KubikBias,
    ) {
        val vec: Vec2 get() = Vec2(position, 0.0).rotate(positionAngle)
    }
}
