package com.kos.boxdrawe.presentation

import androidx.compose.runtime.collectAsState
import com.kos.boxdrawe.presentation.model.BlockModifier
import com.kos.boxdrawe.presentation.model.SegmentBlock
import com.kos.boxdrawe.presentation.model.SegmentBlockGroup
import com.kos.figure.Figure
import com.kos.figure.FigureCircle
import com.kos.figure.FigureEmpty
import com.kos.figure.IFigure
import com.kos.figure.collections.FigureList
import com.kos.figure.complex.transform.toFigure
import com.kos.figure.composition.Figure3dTransform
import com.kos.figure.composition.FigureTranslate
import com.kos.figure.matrix.FigureMatrix
import com.kos.figure.segments.model.Arc
import com.kos.figure.segments.model.Curve
import com.kos.figure.segments.model.Ellipse
import com.kos.figure.segments.model.PathElement
import com.kos.figure.segments.model.Segment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import vectors.Matrix
import vectors.Vec2
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2

class SegmentsToolsData(val tools: ITools) {

    private val _instrument = MutableStateFlow(Instruments.INSTRUMENT_NONE)
    val instrument: StateFlow<Int> = _instrument

    val blocks = MutableStateFlow<SegmentBlockGroup>(SegmentBlockGroup(emptyList()))
    private val previewElement = MutableStateFlow<PathElement?>(null)
    private val hoveredBlock = MutableStateFlow<SegmentBlock?>(null)

    private val points = mutableListOf<Vec2>()
    private var isDrawing = false

    val figures = combine(blocks, previewElement, hoveredBlock) { group, preview, hovered ->
        val blockFigures = group.blocks.map { b -> mapBlock(b) }
        val previewFigure = preview?.let { toFigure(it) }

        // Подсвечиваем ближайшую фигуру, если она найдена
        val highlightFigure = hovered?.let {
            com.kos.figure.composition.FigureColor(0xFFFF0000.toInt(), 1, mapBlock(it))
        }

        val list = FigureList(blockFigures + listOfNotNull(previewFigure, highlightFigure))
        if (group.matrix.isIdentity()) {
            list
        } else {
            Figure3dTransform(group.matrix, list)
        }
    }

    private fun toFigure(segment: PathElement): IFigure {
        return when (segment) {
            is Segment -> segment.toFigure()
            is Arc -> segment.toFigure()
            is Curve -> segment.toFigure()
            is Ellipse -> segment.toFigure()
            else -> FigureEmpty
        }
    }

    private fun mapBlock(block: SegmentBlock): IFigure {
        val f = toFigure(block.element)
        return if (block.matrix.isIdentity()) {
            f
        } else {
            Figure3dTransform(block.matrix, f)
        }
    }

    fun changeInstrument(value: Int) {
        _instrument.value = value
        resetDrawing()
    }

    private fun resetDrawing() {
        points.clear()
        isDrawing = false
        previewElement.value = null
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val instrumentState = _instrument.mapLatest { v ->
        InstrumentState(v)
    }

    fun clear() {
        blocks.value = SegmentBlockGroup(emptyList())
        resetDrawing()
    }

    fun onPress(point: Vec2, button: Int, scale: Float) {
        if (Instruments.button(button) != Instruments.POINTER_LEFT) return

        points.add(point)
        isDrawing = true

        val requiredPoints = when (_instrument.value) {
            Instruments.INSTRUMENT_LINE -> 2
            Instruments.INSTRUMENT_CIRCLE -> 2
            Instruments.INSTRUMENT_ELLIPSE -> 3
            Instruments.INSTRUMENT_BEZIER -> 4
            else -> 0
        }

        if (points.size >= requiredPoints && requiredPoints > 0) {
            val element = createPathElement(points, null)
            if (element != null) {
                blocks.value = SegmentBlockGroup(blocks.value.blocks + SegmentBlock(element))
            }
            resetDrawing()
        }
    }

    fun onMove(point: Vec2, button: Int, scale: Float) {
        if (isDrawing && points.isNotEmpty()) {
            previewElement.value = createPathElement(points, point)
            hoveredBlock.value = null
        } else {
            previewElement.value = null

            // Поиск ближайшей фигуры
            val threshold = 5.0 / scale
            var minDistance = Double.MAX_VALUE
            var nearest: SegmentBlock? = null

            blocks.value.blocks.forEach { block ->
                val localPoint = if (block.matrix.isIdentity()) point else block.matrix.getInvert().map(point)
                val dist = block.element.distance(localPoint)
                if (dist < minDistance && dist < threshold) {
                    minDistance = dist
                    nearest = block
                }
            }
            hoveredBlock.value = nearest
        }
    }

    private fun createPathElement(pts: List<Vec2>, current: Vec2?): PathElement? {
        val allPoints = if (current != null) pts + current else pts

        return when (_instrument.value) {
            Instruments.INSTRUMENT_LINE -> {
                if (allPoints.size >= 2) Segment(allPoints[0], allPoints[1]) else null
            }
            Instruments.INSTRUMENT_CIRCLE -> {
                if (allPoints.size >= 2) {
                    val radius = Vec2.distance(allPoints[0], allPoints[1])
                    if (radius > 0) {
                        Arc(
                            center = allPoints[0],
                            radius = radius,
                            outSide = true,
                            startAngle = 0.0,
                            sweepAngle = PI * 2
                        )
                    } else null
                } else null
            }
            Instruments.INSTRUMENT_ELLIPSE -> {
                if (allPoints.size >= 2) {
                    val center = allPoints[0]
                    val p1 = allPoints[1]
                    val diff = p1 - center
                    val radiusX = diff.magnitude
                    val rotation = atan2(diff.y, diff.x)

                    val radiusY = if (allPoints.size >= 3) {
                        val p2 = allPoints[2]
                        // Проекция p2 на перпендикулярную ось
                        val p2Local = (p2 - center).rotate(-rotation)
                        abs(p2Local.y)
                    } else {
                        radiusX * 0.5 // Превью второго радиуса
                    }

                    if (radiusX > 0 && radiusY > 0) {
                        Ellipse(
                            center = center,
                            radiusX = radiusX,
                            radiusY = radiusY,
                            rotation = rotation,
                            startAngle = 0.0,
                            endAngle = PI * 2,
                            outSide = true
                        )
                    } else null
                } else null
            }
            Instruments.INSTRUMENT_BEZIER -> {
                when (allPoints.size) {
                    2 -> Segment(allPoints[0], allPoints[1])
                    3 -> Curve(allPoints[0], allPoints[1], allPoints[2], allPoints[2])
                    4 -> Curve(allPoints[0], allPoints[1], allPoints[2], allPoints[3])
                    else -> null
                }
            }
            else -> null
        }
    }

    fun onRelease(point: Vec2, button: Int, scale: Float) {
    }
}
