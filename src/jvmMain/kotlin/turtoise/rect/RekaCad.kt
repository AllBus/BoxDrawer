package turtoise.rect

import com.kos.boxdrawe.presentation.RectBlockPosition
import com.kos.figure.FigureCircle
import com.kos.figure.FigureLine
import com.kos.figure.FigureList
import com.kos.figure.IFigure
import com.kos.figure.composition.FigureColor
import turtoise.TurtoiseParserStackBlock
import turtoise.TurtoiseParserStackItem
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

    fun newReka(items: TurtoiseParserStackBlock): Reka? {
        return items.getBlockAtName("reka")?.let { rekaBlock ->
            rekaBlock.getBlockAtName("e")?.arguments()?.let { args ->
                //Todo: получить все блоки как MemoryKey
                val reka = Reka(
                    podoshva = args.getOrElse(0) { MemoryKey.ZERO }
                )

                args.drop(1).windowed(2, 2) { v ->
                    RekaStorona(
                        length = v[0],
                        angle = v[1],
                    )
                }

                rekaBlock.getBlockAtName("s")?.let { st ->
                    val index = memoryToInt(st.get(1), 1)
                    if (index >= 0 && index <= reka.storoni.size) {
                        st.getBlockAtName("k")?.blocks?.forEach { kub ->
                            kub.getBlockAtName("b")?.let { b ->
                                val kg = KubikGroup(
                                    napravlenie = biasFrom(b)
                                )
                                kg.group.addAll(
                                    kub.getBlockAtName("v")?.blocks?.mapNotNull { vb ->
                                        newReka(vb)?.let { r ->
                                            Kubik(
                                                padding = vb.get("p") ?: MemoryKey.ZERO,
                                                reka = r
                                            )
                                        }
                                    }.orEmpty()
                                )
                                reka.storoni[index - 1].add(kg)
                            }
                        }// blocks
                    }
                }// storona
                reka
            }
        }
    }

    fun memoryToInt(key: MemoryKey?, defaultValue: Int): Int {
        return key?.toDoubleOrNull()?.toInt() ?: defaultValue
    }

    fun biasFrom(block: TurtoiseParserStackBlock): KubikBias {
        val n = memoryToInt(block.get(0), 1)
        val b = memoryToInt(block.get(2), 1)
        val t = memoryToInt(block.get(1), 0)

        return KubikBias(
            biasT = t,
            biasB = if (b >= 1) b else 1,
            napravlenie = if (n < 0) EBiasNapravlenie.LEFT else if (n > 0) EBiasNapravlenie.RIGHT else EBiasNapravlenie.CENTER
        )
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
        memory: TortoiseMemory,
        result: RekaDrawResult
    ) {
        var a = angle

        val pv = memory.value(top.podoshva, 0.0)

        var current = center + Vec2(pv / 2, 0.0).rotate(a)
        result.points.add(current)

        val topPoints = mutableListOf(current)
        top.storoni.forEachIndexed { i, storona ->
            a += (PI - memory.value(storona.angle, 0.0))
            val ps = memory.value(storona.length, 0.0)

            storona.kubiki.flatMap { kubik ->
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
                        center = current,
                        position = kubikStart + nap * (pod / 2 + pad),
                        positionAngle = a,
                        angle = drawAngle,
                        reka = block.reka,
                        index = index,
                        bias = kubik.napravlenie,
                        parent = top,
                    )
                    kubikStart += nap * (pod + pad)
                    p
                } // end block
                // end kubik
            }.sortedBy {
                it.position
            }.forEach { kdp ->
                result.positions += kdp

                createFigure(
                    top = kdp.reka,
                    center = kdp.coord,
                    angle = kdp.angle,
                    memory = memory,
                    result = result,
                )
            }

            current += Vec2(ps, 0.0).rotate(a)

            result.points += current
            topPoints += current
        } //end storona

        top.recalculate(topPoints)
    }

    fun centerFigures(rekaDraw: RekaDrawResult): IFigure {
        return FigureList(
            rekaDraw.positions.map { kubik ->
                val av = -kubik.angle * 180 / PI
                FigureCircle(kubik.coord, 2.5, segmentStart = av - 180f, segmentEnd = av)
            }
        )
    }

    fun selectPositionFigure(
        rekaDraw: RekaDrawResult,
        cur: RectBlockPosition,
    ): IFigure {
        val resList = mutableListOf<IFigure>()
        rekaDraw.positions.forEach { kubik ->
            if (kubik.reka === cur.reka) {
                val av = -kubik.angle * 180 / PI
                resList.add(
                    FigureCircle(kubik.coord, 2.6, segmentStart = av - 180f, segmentEnd = av)
                )

                findEdgePoints(kubik.reka, cur.position.edge)?.let { rep ->
                    resList.add(
                        FigureLine(
                            rep.start,
                            rep.end,
                        )
                    )
                }
            }// end if reka

            val kubikStart = kubik.coord

            if (kubik.parent == cur.reka && kubik.index == cur.position.block && kubik.bias == cur.position.storona) {
                FigureColor(
                    0xff00ff,
                    FigureLine(
                        kubikStart + Vec2(-5.0, 0.0).rotate(kubik.angle),
                        kubikStart + Vec2(5.0, 0.0).rotate(kubik.angle)
                    )
                )
            }
        }
        return FigureList(
            resList.toList()
        )
    }

    private fun findEdgePoints(reka: Reka, edge: Int): RekaEdgePoints? {
        val pt = reka.points
        if (edge >= 1 && edge < pt.size)
            return RekaEdgePoints(
                pt[edge - 1],
                pt[edge]
            )
        else
            return null
    }

    fun findPosition(
        rekaDraw: RekaDrawResult,
        cur: RectBlockPosition,
    ): KubikDrawPosition? {
        return rekaDraw.positions.find { kubik ->
            kubik.parent == cur.reka && kubik.index == cur.position.block && kubik.bias == cur.position.storona
        } ?: rekaDraw.positions.find { kubik -> kubik.reka === cur.reka }
    }

    fun print(top: Reka): TurtoiseParserStackItem {

        val tp = TurtoiseParserStackBlock('(', "reka")

        val te = TurtoiseParserStackBlock('(', "e")
        tp.add(te)

        te.add(top.podoshva)

        top.storoni.forEach { storona ->
            te.add(storona.length)
            te.add(storona.angle)
        }

        val ts = TurtoiseParserStackBlock('(', "s")
        tp.add(ts)

        top.storoni.forEachIndexed { index, storona ->
            if (storona.kubkikiSize > 0) {
                val tsi = TurtoiseParserStackBlock('(', "$index")

                val tsk = TurtoiseParserStackBlock('(', "k")
                storona.kubiki.forEach { kubik ->
                    if (kubik.size > 0) {
                        val tkubik = TurtoiseParserStackBlock()
                        tkubik.add("b", kubik.napravlenie.print())
                        val tg = TurtoiseParserStackBlock('(', "v")
                        tkubik.add(tg)

                        kubik.group.map {
                            val tf = TurtoiseParserStackBlock()
                            tf.add("p", it.padding)
                            tf.addItems(listOf(print(it.reka)))
                            tf
                        }.forEach {
                            tg.add(it)
                        }
                        tsk.add(tkubik)
                    }
                }
                tsi.add(tsk)
                ts.add(tsi)
            }
        }
        return tp
    }


    class KubikDrawPosition(
        val center: Vec2,
        val position: Double,
        val positionAngle: Double,
        val angle: Double,
        val reka: Reka,
        val index: Int,
        val bias: KubikBias,
        val parent: Reka,
    ) {
        val vec: Vec2 get() = Vec2(position, 0.0).rotate(positionAngle)
        val coord: Vec2 get() = center + vec
    }

    class RekaDrawResult() {
        val points = mutableListOf<Vec2>()
        val positions = mutableListOf<KubikDrawPosition>()
    }

    class RekaEdgePoints(
        val start: Vec2,
        val end: Vec2,
    )

}
