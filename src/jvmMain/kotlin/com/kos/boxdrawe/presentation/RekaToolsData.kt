package com.kos.boxdrawe.presentation

import androidx.compose.runtime.mutableStateOf
import com.kos.figure.FigureEmpty
import com.kos.figure.collections.FigureList
import com.kos.figure.FigurePolyline
import com.kos.figure.IFigure
import com.kos.figure.composition.FigureColor
import com.kos.figure.composition.FigureRotate
import com.kos.figure.composition.FigureTranslate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import turtoise.memory.SimpleTortoiseMemory
import turtoise.memory.keys.DegreesMemoryKey
import turtoise.memory.keys.DoubleMemoryKey
import turtoise.memory.keys.EditMemoryKey
import turtoise.memory.keys.MemoryKey
import turtoise.memory.keys.SummaMemoryKey
import turtoise.parser.TortoiseParser
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
import turtoise.rect.RekaEndStorona
import turtoise.rect.RekaStorona
import vectors.Vec2
import kotlin.math.PI

class RekaToolsData(override val tools: ITools) : SaveFigure {

    private val memory = SimpleTortoiseMemory()

    private val redrawEvent = MutableStateFlow(0)
    val points = mutableStateOf<String>("")
    val paddingNext = mutableStateOf<String>("0.0")

    val shiftValue = mutableStateOf<String>("0.0")
    val angleValue = mutableStateOf<String>("0.0")

    private var currentReka = Reka(DoubleMemoryKey(10.0), MemoryKey.ZERO)

    private val topReka = MutableStateFlow(Reka(DoubleMemoryKey(10.0), MemoryKey.ZERO).apply {
        this.storoni.addAll(listOf(
            RekaStorona(DoubleMemoryKey(10.0), DegreesMemoryKey(DoubleMemoryKey(90.0))),
            RekaStorona(DoubleMemoryKey(10.0), DegreesMemoryKey(DoubleMemoryKey(90.0))),
            RekaEndStorona(DegreesMemoryKey(DoubleMemoryKey(90.0))),
        ))
    })
    private val rekaFigure = MutableStateFlow<IFigure>(FigureEmpty)

    val current: MutableStateFlow<RectBlockPosition> = MutableStateFlow(
        RectBlockPosition(
            reka = topReka.value,
            position = RekaStoronaPosition(
                edge = 1,
                storona = Kubik.biasLeft,
                block = 0,
            ),
        )
    )

    private val rekaDrawResult = combine(redrawEvent, topReka) { e, reka ->
        val result = RekaCad.createFigure(
            reka, Vec2.Zero, 0.0, memory
        )
        result
    }

    val figures = combine(rekaDrawResult, current, rekaFigure) { result, cur, f ->

        val rfp = RekaCad.findPosition(result, cur)?.let { rp ->
            RekaCad.insertPosition(rp, cur, currentReka, memory)
        }

        FigureList(
            listOf(
                FigurePolyline(result.points, close = true),
                FigureTranslate(
                    FigureRotate(
                        FigureColor(0xffff00, 1, f),
                        (rfp?.angle ?: 0.0) * 180 / PI,
                        Vec2.Zero
                    ),
                    rfp?.coord ?: Vec2(0.0, 0.0),
                ),
                FigureColor(0xffff00,2, RekaCad.centerFigures(result)),
                FigureColor(0xff00ff, 6, RekaCad.selectPositionFigure(result, cur)),
            )
        )
    }

    fun createRekaFigure() {
        RekaCad.createFigure(
            currentReka, Vec2.Zero, 0.0, memory
        )
        rekaFigure.value = FigurePolyline(currentReka.points)

    }

    fun redraw() {
        redrawEvent.value += 1
    }

    suspend fun selectPosition(napravlenie: Int) {
        val cv = current.value
        when (napravlenie) {
            DOWN_BLOCK -> {
                val f = rekaDrawResult.first()
                RekaCad.findPosition(f, cv)?.parent?.let { parent ->
                    current.value = cv.copy(reka = parent)
                }
            }

            UP_BLOCK -> {
                val b = findBlock(cv.reka, cv.position)
                if (b != null) {
                    current.value = RectBlockPosition(
                        b,
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

        resetShiftValue()
    }

    private fun findKubiki(reka: Reka, position: RekaStoronaPosition): KubikGroup? {
        val p = reka.storoni.getOrNull(position.edge - 1) ?: return null
        val k = p.kubiki.find { it.napravlenie == position.storona } ?: return null

        return k
    }

    private fun findBlock(reka: Reka, position: RekaStoronaPosition): Reka? {
        val k = findKubiki(reka, position) ?: return null
        return k.group.getOrNull(position.block)
    }

    private fun insertKubik(reka: Reka, position: RekaStoronaPosition, newKubik: Reka): Boolean {
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

            parseReka(pt, MemoryKey(paddingNext.value))?.let { b ->
                val cv = current.value
                if (insertKubik(
                        reka = cv.reka, position = cv.position, newKubik = b
                    )
                ) {
                    redraw()
                }
            }
        }
    }

    suspend fun clearBox() {
        val cv = current.value

        topReka.value = Reka(DoubleMemoryKey(10.0), MemoryKey.ZERO).apply {
            this.storoni.addAll(listOf(
                RekaStorona(DoubleMemoryKey(10.0), DegreesMemoryKey(DoubleMemoryKey(90.0))),
                RekaStorona(DoubleMemoryKey(10.0), DegreesMemoryKey(DoubleMemoryKey(90.0))),
                RekaEndStorona(DegreesMemoryKey(DoubleMemoryKey(90.0))),
            ))
        }
        current.value = cv.copy(reka =topReka.value)
        resetShiftValue()
        redraw()
    }

    suspend fun removeBox() {
        val cv = current.value

        rekaDrawResult.first().positions.find {
            it.reka == cv.reka
        }?.parent?.let { parent ->
            parent.remove(cv.reka)

            current.value = cv.copy(reka = parent)
            resetShiftValue()
            redraw()
        }
    }

    fun updateBox() {
        val cv = current.value
        val pt = points.value
        if (pt.isNotEmpty()) {
            parseReka(pt, MemoryKey(paddingNext.value))?.let { f ->
                RekaCad.updateReka(cv.reka, f)
            }
            resetShiftValue()
            redraw()
        }
    }

    private fun parseReka(text: String, padding: MemoryKey): Reka? {
        return if (text.drop(1).startsWith("reka")) {
            RekaCad.newReka(TortoiseParser.parseSkobki(text))?.apply {
                this.padding = padding
            }
        } else {
            val tv = text.split(",", " ").map { it.trim() }.filter { it.isNotEmpty() }
            newReka(tv, padding)
        }
    }

    fun setPoints(text: String) {
        points.value = text
        parseReka(points.value, MemoryKey(paddingNext.value))?.let { f ->
            currentReka = f
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
                    resetShiftValue()
                }
            }
        } else {
            current.value = RectBlockPosition(
                kubik.reka,
                position = RekaStoronaPosition(
                    edge = 1,
                    storona = cv.position.storona,
                    block = 0,
                )
            )
            resetShiftValue()
        }
    }

    override suspend fun createFigure(): IFigure {
        redraw()
        val result = rekaDrawResult.first()
        return FigurePolyline(result.points, close = true)
    }

    suspend fun print(): String {
        return "reka@ " + RekaCad.print(topReka.value).line
    }

    fun rotateCurrentReka(degrees: Double) {
        val reka = current.value.reka

        val e = current.value.position.edge
        if (e == 0) {

        } else {
            reka.storoni.getOrNull(e - 1)?.let { storona ->
                val a = storona.angle
                val change = -degrees * PI / 180
                val k = if (a is EditMemoryKey) {
                    EditMemoryKey(
                        a.mainKey,
                        a.value + change,
                        SummaMemoryKey(a.mainKey, a.value + change)
                    )
                } else {
                    EditMemoryKey(a, change, SummaMemoryKey(a, change))
                }
                storona.angle = k
                angleValue.value = k.name
                redraw()
            }
        }
    }

    fun moveCurrentReka(value: Double) {
        val change = value / 15.0
        val reka = current.value.reka
        val e = current.value.position.edge
        if (e == 1) {
            val a = reka.padding
            val k = if (a is EditMemoryKey) {
                EditMemoryKey(
                    a.mainKey,
                    a.value + change,
                    SummaMemoryKey(a.mainKey, a.value + change)
                )
            } else {
                EditMemoryKey(a, change, SummaMemoryKey(a, change))
            }
            shiftValue.value = k.name
            reka.padding = k
            redraw()
        } else {
            reka.storoni.getOrNull(e - 2)?.let { storona ->
                val a = storona.length

                val k = if (a is EditMemoryKey) {
                    EditMemoryKey(
                        a.mainKey,
                        a.value + change,
                        SummaMemoryKey(a.mainKey, a.value + change)
                    )
                } else {
                    EditMemoryKey(a, change, SummaMemoryKey(a, change))
                }
                storona.length = k
                shiftValue.value = k.name
                redraw()
            }
        }
    }

    private fun resetShiftValue(){
        val reka = current.value.reka
        val e = current.value.position.edge
        shiftValue.value = ""
        angleValue.value = ""
        if (e == 1) {
            val a = reka.padding
            shiftValue.value = a.name
        } else {
            reka.storoni.getOrNull(e - 2)?.let { storona ->
                shiftValue.value = storona.length.name

            }
        }
        reka.storoni.getOrNull(e - 1)?.let { storona ->
            angleValue.value = storona.angle.name
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
    val position: RekaStoronaPosition,
)

data class RekaStoronaPosition(
    val edge: Int,
    val storona: KubikBias,
    val block: Int,
)