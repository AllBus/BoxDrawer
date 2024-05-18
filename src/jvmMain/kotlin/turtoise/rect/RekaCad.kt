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
import turtoise.parser.TortoiseParserStackBlock
import turtoise.parser.TortoiseParserStackItem
import vectors.Vec2
import kotlin.math.PI
import kotlin.math.abs

object RekaCad {

    const val BLOCK_REKA = "reka"
    const val BLOCK_REKA_EDGE = "e"
    const val BLOCK_REKA_EDGE_INFO = "s"
    const val BLOCK_KUBIK_LIST = "k"
    const val BLOCK_KUBIK_BIAS = "b"
    const val BLOCK_KUBIK = "v"
    const val BLOCK_PADDING = "p"

    fun updateReka(reka: Reka, tv: List<String>): Reka {
        if (tv.isNotEmpty()) {
            reka.podoshva = MemoryKey(tv.first())
            val newStoroni = rekaPoints(tv)
            newStoroni.zip(reka.storoni) { newStorona, old ->
                newStorona.kubiki.addAll(old.kubiki)
            }
            reka.storoni.clear()
            reka.storoni.addAll(newStoroni)
        }

        return reka
    }

    fun updateReka(reka: Reka, tv: Reka): Reka {
        reka.podoshva = tv.podoshva
        val newStoroni = tv.storoni
        newStoroni.zip(reka.storoni) { newStorona, old ->
            newStorona.kubiki.clear()
            newStorona.kubiki.addAll(old.kubiki)
        }
        reka.storoni.clear()
        reka.storoni.addAll(newStoroni)
        return reka
    }

    fun newReka(items: TortoiseParserStackBlock): Reka? {

        return items.getBlockAtName(BLOCK_REKA)?.let { rekaBlock ->
            rekaBlock.getBlockAtName(BLOCK_REKA_EDGE)?.inner?.map { it.argument }?.let { args ->
                val reka = Reka(
                    podoshva = args.getOrElse(1) { MemoryKey.ZERO },
                    padding = rekaBlock.getBlockAtName(BLOCK_PADDING)?.get(1) ?: MemoryKey.ZERO,
                )

                reka.storoni.addAll(args.drop(2).dropLast(1).windowed(2, 2) { v ->
                    RekaStorona(
                        angle = v[0],
                        length = v[1],
                    )
                })
                reka.storoni.add(RekaEndStorona(args.last()))

//                println("newReka ${reka.storoni.size} $$ ${reka.podoshva.name}")
//                println("tut ${rekaBlock.blocks.map { it.name }.joinToString()}")

                rekaBlock.getBlockAtName(BLOCK_REKA_EDGE_INFO)?.let { storoni ->
//                    println("storoni ${storoni.line}")
                    storoni.blocks.forEach { st ->
                        val index = memoryToInt(st.get(0), 1)
                        if (index > 0 && index <= reka.storoni.size) {
                            st.getBlockAtName(BLOCK_KUBIK_LIST)?.blocks?.forEach { kub ->
                                kub.getBlockAtName(BLOCK_KUBIK_BIAS)?.let { b ->
                                    val kg = KubikGroup(
                                        napravlenie = biasFrom(b)
                                    )
                                    kg.group.addAll(
                                        kub.getBlockAtName(BLOCK_KUBIK)?.blocks?.mapNotNull { vb ->
                                            newReka(vb)
                                        }.orEmpty()
                                    )
                                    reka.storoni[index - 1].add(kg)
                                }
                            }// blocks
                        }
                    }
                }// storona
                reka
            }
        }
    }

    private fun memoryToInt(key: MemoryKey?, defaultValue: Int): Int {
        return key?.toDoubleOrNull()?.toInt() ?: defaultValue
    }

    fun biasFrom(block: TortoiseParserStackBlock): KubikBias {
        val n = memoryToInt(block.get(1), 1)
        val b = memoryToInt(block.get(3), 1)
        val t = memoryToInt(block.get(2), 0)

        return KubikBias(
            biasT = t,
            biasB = if (b >= 1) b else 1,
            napravlenie = if (n < 0) EBiasNapravlenie.LEFT else if (n > 0) EBiasNapravlenie.RIGHT else EBiasNapravlenie.CENTER
        )
    }


    fun newReka(tv: List<String>, padding: MemoryKey): Reka {
        val reka = if (tv.isNotEmpty()) {
            val currentReka = Reka(
                MemoryKey(tv.first()),
                padding,
            )
            currentReka.storoni.addAll(rekaPoints(tv))
            currentReka
        } else
            Reka(
                MemoryKey.EMPTY,
                padding,
            )

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
                    RekaEndStorona(
                        //  length = MemoryKey(tv[1]),
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
                    RekaEndStorona(
                        //  length = mc,
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
                    RekaEndStorona(
                        // length = MemoryKey(tv[0]),
                        angle = MemoryKey.PI2
                    ),
                )
            }

            else -> {
                tv.drop(1).dropLast(1).windowed(2, 2).map { v ->
                    RekaStorona(
                        length = MemoryKey(v[1]),
                        angle = DegreesMemoryKey(MemoryKey(v[0])),
                    )
                } + RekaEndStorona(tv.lastOrNull().orEmpty().let { MemoryKey(it) })
            }
        }
        return st
    }

    fun createFigure(
        top: Reka,
        center: Vec2,
        angle: Double,
        memory: TortoiseMemory
    ): RekaDrawResult {
        val result = RekaDrawResult()

        createFigure(top, center, angle, memory, result)
        result.positions.add(
            KubikDrawPosition(center, 0.0, angle, angle, top, 0, Kubik.biasLeft, top)
        )
        return result
    }

    fun insertPosition(
        kubik: KubikDrawPosition,
        cur: RectBlockPosition,
        meKubik: Reka,
        memory: TortoiseMemory
    ): KubikDrawPosition {
        val top = kubik.reka
        var a = kubik.angle
        val pv = memory.value(top.podoshva, 0.0)
        val startPoint = kubik.coord + Vec2(pv / 2, 0.0).rotate(a)
        var current = startPoint
        val endPoint = kubik.coord + Vec2(-pv / 2, 0.0).rotate(a)

        val meBias = cur.position.storona

        top.storoni.forEachIndexed { i, storona ->
            val ps = if (storona.isEnd()) {
                a += (PI - memory.value(storona.angle, 0.0))
                val s = calculateStoronaLength(current, startPoint, endPoint, a)
                if (s == null) {
                    a = PI + (current - endPoint).angle
                    Vec2.distance(current, endPoint)
                } else
                    s
            } else {
                a += (PI - memory.value(storona.angle, 0.0))
                memory.value(storona.length, 0.0)
            }


            if (i + 1 == cur.position.edge) {
                val kuk = storona.kubiki.find { it.napravlenie == meBias } ?: KubikGroup(meBias)

                val kubik = KubikGroup(meBias)
                kubik.group.addAll(kuk.group)
                kubik.add(cur.position.block, meKubik)

                val fullLength = kubik.group.sumOf { block ->
                    val pod = memory.value(block.podoshva, 0.0)
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

                kubik.group.map { block ->
                    val pod = memory.value(block.podoshva, 0.0)
                    val pad = memory.value(block.padding, 0.0)

                    if (block === meKubik) {
                        return KubikDrawPosition(
                            center = current,
                            position = kubikStart + nap * (pod / 2 + pad),
                            positionAngle = a,
                            angle = PI + a,
                            reka = block,
                            index = cur.position.block,
                            bias = kubik.napravlenie,
                            parent = top,
                        )
                    }
                    kubikStart += nap * (pod + pad)
                }
            }
            current += Vec2(ps, 0.0).rotate(a)
        }
        return kubik
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

        val startPoint = center + Vec2(pv / 2, 0.0).rotate(a)
        var current = startPoint
        val endPoint = center + Vec2(-pv / 2, 0.0).rotate(a)
        result.points.add(current)

        val topPoints = mutableListOf(current)
        top.storoni.forEachIndexed { i, storona ->
            val ps = if (storona.isEnd()) {
                a += (PI - memory.value(storona.angle, 0.0))
                val s = calculateStoronaLength(current, startPoint, endPoint, a)
                if (s == null) {
                    a = PI + (current - endPoint).angle
                    Vec2.distance(current, endPoint)
                } else
                    s
            } else {
                a += (PI - memory.value(storona.angle, 0.0))
                memory.value(storona.length, 0.0)
            }

            storona.kubiki.flatMap { kubik ->
                val fullLength = kubik.group.sumOf { block ->
                    val pod = memory.value(block.podoshva, 0.0)
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
                    val pod = memory.value(block.podoshva, 0.0)
                    val pad = memory.value(block.padding, 0.0)
                    val p = KubikDrawPosition(
                        center = current,
                        position = kubikStart + nap * (pod / 2 + pad),
                        positionAngle = a,
                        angle = drawAngle,
                        reka = block,
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

    private fun calculateStoronaLength(
        current: Vec2,
        startPoint: Vec2,
        endPoint: Vec2,
        a: Double
    ): Double? {

        val curB = current + Vec2(10.0, 0.0).rotate(a)

        val znamenatel =
            (current.x - curB.x) * (startPoint.y - endPoint.y) - (current.y - curB.y) * (startPoint.x - endPoint.x)
        if (abs(znamenatel) < 0.001)
            return null

        val a = (current.x * curB.y - current.y * curB.x)
        val b = (startPoint.x * endPoint.y - startPoint.y * endPoint.x)

        val x = a * (startPoint.x - endPoint.x) - b * (current.x - curB.x)
        val y = a * (startPoint.y - endPoint.y) - b * (current.y - curB.y)

        val v = Vec2(x / znamenatel, y / znamenatel)
        val d = Vec2.distance(current, v)
        if (d > 100000.0)
            return null
        return d
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
        return rekaDraw.positions.find { kubik -> kubik.reka === cur.reka }
    }

    fun print(top: Reka): TortoiseParserStackItem {

        val tp = TortoiseParserStackBlock('{', BLOCK_REKA)

        val te = TortoiseParserStackBlock('[', BLOCK_REKA_EDGE)
        tp.add(te)

        te.add(top.podoshva)

        top.storoni.forEach { storona ->
            if (!storona.isEnd()) {
                te.add(storona.angle)
                te.add(storona.length)
            } else {
                te.add(storona.angle)
            }
        }

        tp.add(BLOCK_PADDING, top.padding)

        val ts = TortoiseParserStackBlock('(', BLOCK_REKA_EDGE_INFO)
        tp.add(ts)

        top.storoni.forEachIndexed { index, storona ->
            if (storona.kubkikiSize > 0) {
                val tsi = TortoiseParserStackBlock('(', "${index + 1}")

                val tsk = TortoiseParserStackBlock('(', BLOCK_KUBIK_LIST).apply {
                    addItems(
                        storona.kubiki.filter { it.size > 0 }.map { kubik ->
                            TortoiseParserStackBlock().apply {
                                add(kubik.napravlenie.print())
                                add(printKubikGroup(kubik))
                            }
                        }
                    )
                }

                tsi.add(tsk)
                ts.add(tsi)
            }
        }
        return tp
    }

    private fun printKubikGroup(kubik: KubikGroup): TortoiseParserStackBlock {
        return TortoiseParserStackBlock('(', BLOCK_KUBIK).apply {
            addItems(
                kubik.group.map {
                    TortoiseParserStackBlock().apply {
                        addItems(listOf(print(it)))
                    }
                }
            )
        }
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

    class KubikPosition(
        val center: Vec2,
        val angle: Double,
    )

}
