package com.kos.boxdrawe.presentation

import com.kos.figure.Figure
import com.kos.figure.FigureList
import com.kos.figure.FigurePolyline
import com.kos.figure.IFigure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import turtoise.Tortoise
import turtoise.memory.SimpleTortoiseMemory
import turtoise.memory.keys.DoubleMemoryKey
import turtoise.memory.keys.MemoryKey
import turtoise.rect.EStorona
import turtoise.rect.Kubik
import turtoise.rect.Kubik.Companion.STORONA_CL
import turtoise.rect.Kubik.Companion.STORONA_CR
import turtoise.rect.Kubik.Companion.STORONA_L
import turtoise.rect.Kubik.Companion.STORONA_R
import turtoise.rect.KubikGroup
import turtoise.rect.RectBlock
import turtoise.rect.RectBlockEdges
import turtoise.rect.RectBlockParent
import turtoise.rect.Reka
import turtoise.rect.RekaCad
import turtoise.rect.RekaCad.newReka
import turtoise.rect.RekaCad.rekaPoints
import vectors.Vec2

class RectToolsData(val tools: ITools) {

    private val memory = SimpleTortoiseMemory()

    private val redrawEvent = MutableStateFlow(0)
    private val points = MutableStateFlow<List<String>>(emptyList())

    private val currentReka = Reka(DoubleMemoryKey(10.0))

    private val topReka = MutableStateFlow(Reka(DoubleMemoryKey(10.0)))
    private val rekaFigure = MutableStateFlow<IFigure>(Figure.Empty)
    private var current: MutableStateFlow<RectBlockPosition> = MutableStateFlow(
        RectBlockPosition(
            reka = currentReka,
            parent = null,
            position = RekaStoronaPosition(
                edge = 1,
                storona = STORONA_L,
                block = 0,
            ),
        )
    )

    val figures = combine(redrawEvent, topReka, current, rekaFigure) { e,  reka , cur , f ->
        FigureList(listOf(RekaCad.createFigure(reka, Vec2.Zero, 0.0, cur, memory),f))
    }

    fun createRekaFigure() {
        currentReka.recalculate(memory = memory)
        // println(currentReka.points)
        rekaFigure.value = FigurePolyline(currentReka.points)

    }

    fun redraw() {
        redrawEvent.value += 1
    }

    fun selectPosition(napravlenie: Int) {
        val cv = current.value
        when (napravlenie) {
            DOWN_BLOCK -> {
                if (cv.parent != null) {
                    current.value = cv.parent
                }
            }

            UP_BLOCK -> {
                val b = findBlock(cv.reka, cv.position)
                if (b != null) {
                    current.value = RectBlockPosition(
                        b,
                        parent = cv,
                        position = RekaStoronaPosition(
                            edge = 1,
                            storona = STORONA_L,
                            block = 0,
                        )
                    )
                }
            }

            NEXT_EDGE -> {
                val e = cv.position.edge + 1
                val e2 = if (e > cv.reka.storoni.size) 1 else e

                current.value = cv.copy(
                    position = cv.position.copy(edge = e2)
                )
            }

            BACK_EDGE -> {
                val e = cv.position.edge - 1
                val e2 = if (e < 1) cv.reka.storoni.size else e

                current.value = cv.copy(
                    position = cv.position.copy(edge = e2)
                )
            }

            STORONA_L,
            STORONA_CL,
            STORONA_CR,
            STORONA_R -> {
                current.value = cv.copy(
                    position = cv.position.copy(block = 0, storona = STORONA_L)
                )
            }

            NEXT_BLOCK -> {
                val kubiki = findKubiki(cv.reka, cv.position)
                if (kubiki != null) {
                    val e = cv.position.block + 1
                    val e2 = if (e > kubiki.size) kubiki.size else e

                    current.value = cv.copy(
                        position = cv.position.copy(block = e2)
                    )
                }
            }

            BACK_BLOCK -> {
                val e = cv.position.block - 1
                val e2 = if (e < 0) 0 else e

                current.value = cv.copy(
                    position = cv.position.copy(block = e2)
                )
            }
        }
    }

    private fun findKubiki(reka: Reka, position: RekaStoronaPosition): KubikGroup? {
        val p = reka.storoni.getOrNull(position.storona - 1) ?: return null
        val k = p.kubiki.find { it.napravlenie == position.storona } ?: return null

        return k
    }

    private fun findBlock(reka: Reka, position: RekaStoronaPosition): Reka? {
        val k = findKubiki(reka, position) ?: return null
        return k.group.getOrNull(position.block)?.reka
    }

    private fun insertKubik(reka: Reka, position: RekaStoronaPosition, newKubik: Kubik): Boolean {
        val k = findKubiki(reka, position) ?: return false
        k.add(position.block, newKubik)


        return true
    }

    fun createBox() {
        val pt = points.value
        if (pt.isNotEmpty()) {

            val b = newReka(pt, memory)
            val cv = current.value
            if (insertKubik(
                    reka = cv.reka, position = cv.position, newKubik = Kubik(
                        padding = MemoryKey.ZERO,
                        reka = b
                    )
                )
            ) {
                cv.reka.recalculate(memory = memory)
                redraw()
            }
        }
    }

    fun removeBox() {
        val cv = current.value
        if (cv.parent != null) {
            val cp = cv.parent
            cp.reka.remove(cv.reka)
            cv.reka.recalculate(memory = memory)
            current.value = cv.parent
            redraw()
        }
    }

    fun setPoints(text: String) {
        val tv = text.split(",").map { it.trim() }
        points.value = tv

        if (tv.isNotEmpty()) {
            currentReka.podoshva = MemoryKey(tv.first())
            currentReka.storoni.clear()
            val st = rekaPoints(tv)
            currentReka.storoni.addAll(st)
            createRekaFigure()
        }
    }

    companion object {

        const val NEXT_EDGE = 10
        const val BACK_EDGE = 20
        const val NEXT_BLOCK = 11
        const val BACK_BLOCK = 21
        const val UP_BLOCK = 12
        const val DOWN_BLOCK = 22
    }
}

data class RectBlockPosition(
    val reka: Reka,
    val parent: RectBlockPosition?,
    val position: RekaStoronaPosition,
)

data class RekaStoronaPosition(
    val edge: Int,
    val storona: Int,
    val block: Int,
)