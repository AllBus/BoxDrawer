package com.kos.boxdrawe.presentation

import com.kos.figure.Figure
import com.kos.figure.FigureList
import com.kos.figure.FigurePolyline
import com.kos.figure.IFigure
import com.kos.figure.composition.FigureColor
import com.kos.figure.composition.FigureRotate
import com.kos.figure.composition.FigureTranslate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import turtoise.memory.SimpleTortoiseMemory
import turtoise.memory.keys.DoubleMemoryKey
import turtoise.memory.keys.MemoryKey
import turtoise.rect.Kubik
import turtoise.rect.Kubik.Companion.STORONA_C
import turtoise.rect.Kubik.Companion.STORONA_CL
import turtoise.rect.Kubik.Companion.STORONA_CR
import turtoise.rect.Kubik.Companion.STORONA_L
import turtoise.rect.Kubik.Companion.STORONA_R
import turtoise.rect.KubikBias
import turtoise.rect.KubikGroup
import turtoise.rect.Reka
import turtoise.rect.RekaCad
import turtoise.rect.RekaCad.newReka
import turtoise.rect.RekaCad.rekaPoints
import vectors.Vec2
import kotlin.math.PI

class RekaToolsData(val tools: ITools) {

    private val memory = SimpleTortoiseMemory()

    private val redrawEvent = MutableStateFlow(0)
    private val points = MutableStateFlow<List<String>>(emptyList())
    private val paddingNext = MutableStateFlow<String>("0.0")

    private val currentReka = Reka(DoubleMemoryKey(10.0))

    private val topReka = MutableStateFlow(Reka(DoubleMemoryKey(10.0)))
    private val rekaFigure = MutableStateFlow<IFigure>(Figure.Empty)
    var current: MutableStateFlow<RectBlockPosition> = MutableStateFlow(
        RectBlockPosition(
            reka = topReka.value,
            parent = null,
            position = RekaStoronaPosition(
                edge = 1,
                storona = Kubik.biasLeft,
                block = 0,
            ),
        )
    )

    private val rekaDrawResult = combine(redrawEvent, topReka) { e, reka ->
        val result = RekaCad.RekaDrawResult()
        RekaCad.createFigure(
            reka, Vec2.Zero, 0.0, memory, result
        )
        result
    }

    val figures = combine(rekaDrawResult, current, rekaFigure) { result, cur, f ->

        val rfp = RekaCad.findPosition(result, cur)
        FigureList(
            listOf(
                FigurePolyline(result.points, close = true),
                FigureTranslate(
                    FigureRotate(
                        FigureColor(0xffff00, f),
                        (rfp?.angle ?: 0.0) * 180 / PI,
                        Vec2.Zero
                    ),
                    rfp?.coord ?: Vec2(0.0, 0.0),
                ),
                FigureColor(0xffff00, RekaCad.centerFigures(result)),
                FigureColor(0xff00ff, RekaCad.selectPositionFigure(result, cur)),
            )
        )
    }

    fun createRekaFigure() {
        currentReka.recalculate(memory = memory)
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
                            storona = cv.position.storona,
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
            STORONA_C,
            STORONA_R -> {
                current.value = cv.copy(
                    position = cv.position.copy(block = 0, storona = Kubik.toBias(napravlenie))
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
        val p = reka.storoni.getOrNull(position.edge - 1) ?: return null
        val k = p.kubiki.find { it.napravlenie == position.storona } ?: return null

        return k
    }

    private fun findBlock(reka: Reka, position: RekaStoronaPosition): Reka? {
        val k = findKubiki(reka, position) ?: return null
        return k.group.getOrNull(position.block)?.reka
    }

    private fun insertKubik(reka: Reka, position: RekaStoronaPosition, newKubik: Kubik): Boolean {
        val p = reka.storoni.getOrNull(position.edge - 1) ?: return false
        p.add(
            position.storona,
            position.block,
            newKubik
        )
        return true
    }

    fun createBox() {
        val pt = points.value
        if (pt.isNotEmpty()) {

            val b = newReka(pt, memory)
            val cv = current.value
            if (insertKubik(
                    reka = cv.reka, position = cv.position, newKubik = Kubik(
                        padding = MemoryKey(paddingNext.value),
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

    fun updateBox() {
        val cv = current.value
        val pt = points.value
        if (pt.isNotEmpty()) {
            RekaCad.updateReka(cv.reka, pt, memory)
            redraw()
        }
    }

    fun setPoints(text: String) {
        val tv = text.split(",", " ").map { it.trim() }.filter { it.isNotEmpty() }
        points.value = tv

        if (tv.isNotEmpty()) {
            currentReka.podoshva = MemoryKey(tv.first())
            currentReka.storoni.clear()
            val st = rekaPoints(tv)
            currentReka.storoni.addAll(st)
            createRekaFigure()
        }
    }

    fun setPadding(text: String) {
        paddingNext.value = text
    }

    suspend fun onPress(point: Vec2, button: Int, scale: Float) {
        val result = rekaDrawResult.first()
        val cv = current.value
        val kubik = result.positions.find {
            Vec2.distance(it.coord, point) < 5.0
        }

        if (kubik == null) {
            val pt = cv.reka.points
            for (i in 1 until pt.size) {
                if (Vec2.distance(pt[i - 1], pt[i], point) < (5.0 / scale)) {
                    current.value = cv.copy(
                        position = cv.position.copy(edge = i)
                    )
                }
            }
        } else {
            current.value = RectBlockPosition(
                kubik.reka,
                parent = cv,
                position = RekaStoronaPosition(
                    edge = 1,
                    storona = cv.position.storona,
                    block = 0,
                )
            )

        }
    }

    suspend fun save(fileName: String) {
        redraw()
        val result = rekaDrawResult.first()
        tools.saveFigures(fileName, FigurePolyline(result.points, close = true))
        tools.updateChooserDir(fileName)
    }

    suspend fun print(): String {
        return "reka@ " + RekaCad.print(topReka.value).line
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
    val storona: KubikBias,
    val block: Int,
)