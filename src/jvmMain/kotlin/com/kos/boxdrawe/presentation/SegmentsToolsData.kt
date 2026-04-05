package com.kos.boxdrawe.presentation

import com.kos.boxdrawe.presentation.model.SegmentBlock
import com.kos.boxdrawe.presentation.model.SegmentBlockGroup
import com.kos.figure.FigureEmpty
import com.kos.figure.IFigure
import com.kos.figure.collections.FigureList
import com.kos.figure.complex.transform.toFigure
import com.kos.figure.composition.Figure3dTransform
import com.kos.figure.segments.model.Arc
import com.kos.figure.segments.model.Curve
import com.kos.figure.segments.model.Ellipse
import com.kos.figure.segments.model.PathElement
import com.kos.figure.segments.model.Segment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import vectors.Matrix
import vectors.Vec2
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import com.kos.boxdrawe.presentation.segments.SegmentUtils

class SegmentsToolsData(val tools: ITools) {

    private val _instrument = MutableStateFlow(Instruments.INSTRUMENT_NONE)
    val instrument: StateFlow<Int> = _instrument

    val blocks = MutableStateFlow<SegmentBlockGroup>(SegmentBlockGroup(emptyList()))
    private val previewElement = MutableStateFlow<PathElement?>(null)
    private val hoveredBlock = MutableStateFlow<SegmentBlock?>(null)

    private val points = mutableListOf<Vec2>()
    private var isDrawing = false

    private var movingBlock: SegmentBlock? = null
    private var dragStartPoint: Vec2 = Vec2.Zero
    private var originalMatrix: Matrix = Matrix.identity
    private var pivotPoint: Vec2 = Vec2.Zero

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

        val hovered = hoveredBlock.value

        if (_instrument.value == Instruments.INSTRUMENT_MOVE ||
                    _instrument.value == Instruments.INSTRUMENT_ROTATE ||
                    _instrument.value == Instruments.INSTRUMENT_SCALE ) {

            movingBlock = hovered
            dragStartPoint = point
            if (hovered != null) {
                originalMatrix = hovered.matrix
                // Центр фигуры для вращения/масштабирования
                pivotPoint = hovered.element.center.let {
                    if (hovered.matrix.isIdentity()) it else hovered.matrix.map(it)
                }
            }
            return
        }

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
        val moving = movingBlock
        if (moving != null) {
            val delta = point - dragStartPoint
            val newMatrix = when (_instrument.value) {
                Instruments.INSTRUMENT_MOVE -> {
                    moveElement(delta)
                }
                Instruments.INSTRUMENT_ROTATE -> {
                    rotateElement(point)
                }
                Instruments.INSTRUMENT_SCALE -> {
                    scaleElement(point)
                }
                else -> originalMatrix
            }

            blocks.value = SegmentBlockGroup(blocks.value.blocks.map {
                if (it.element === moving.element) it.copy(matrix = newMatrix) else it
            })
            return
        }

        if (isDrawing && points.isNotEmpty()) {
            previewElement.value = createPathElement(points, point)
            hoveredBlock.value = null
        } else {
            previewElement.value = null

            hoverElement(scale, point)
        }
    }

    private fun hoverElement(scale: Float, point: Vec2) {
        // Поиск ближайшей фигуры
        val threshold = 5.0 / scale
        var minDistance = Double.MAX_VALUE
        var nearest: SegmentBlock? = null

        blocks.value.blocks.forEach { block ->
            val localPoint =
                if (block.matrix.isIdentity()) point else block.matrix.getInvert().map(point)
            val dist = block.element.distance(localPoint)
            if (dist < minDistance && dist < threshold) {
                minDistance = dist
                nearest = block
            }
        }
        hoveredBlock.value = nearest
    }

    private fun moveElement(delta: Vec2): Matrix =
        originalMatrix.copyWithTransform(Matrix.translate(delta.x, delta.y))

    private fun scaleElement(point: Vec2): Matrix {
        val distStart = Vec2.distance(dragStartPoint, pivotPoint)
        val distCurrent = Vec2.distance(point, pivotPoint)
        val s = if (distStart > 0) distCurrent / distStart else 1.0

        // Масштабирование относительно pivotPoint
        val m = Matrix.translate(pivotPoint.x, pivotPoint.y)
        m.scale(s.toFloat(), s.toFloat())
        m.translate(-pivotPoint.x.toFloat(), -pivotPoint.y.toFloat())

        return originalMatrix.copyWithTransform(m)
    }

    private fun rotateElement(point: Vec2): Matrix {
        val angleStart = (dragStartPoint - pivotPoint).angle
        val angleCurrent = (point - pivotPoint).angle
        val diffAngle = angleCurrent - angleStart

        // Вращение вокруг pivotPoint
        val m = Matrix.translate(pivotPoint.x, pivotPoint.y)
        m.rotateZ((diffAngle * 180 / PI).toFloat())
        m.translate(-pivotPoint.x.toFloat(), -pivotPoint.y.toFloat())

        return originalMatrix.copyWithTransform(m)
    }

    private fun createPathElement(pts: List<Vec2>, current: Vec2?): PathElement? {
        val allPoints = if (current != null) pts + current else pts

        return when (_instrument.value) {
            Instruments.INSTRUMENT_LINE -> {
                SegmentUtils.createPathLine(allPoints)
            }
            Instruments.INSTRUMENT_CIRCLE -> {
                SegmentUtils.createPathCircle(allPoints)
            }
            Instruments.INSTRUMENT_ELLIPSE -> {
                SegmentUtils.createPathEllipse(allPoints)
            }
            Instruments.INSTRUMENT_BEZIER -> {
                SegmentUtils.createPathBezier(allPoints)
            }
            else -> null
        }
    }



    fun onRelease(point: Vec2, button: Int, scale: Float) {
        movingBlock = null
    }
}
