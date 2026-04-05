package com.kos.boxdrawe.presentation

import com.kos.boxdrawe.presentation.model.SegmentBlock
import com.kos.boxdrawe.presentation.model.SegmentBlockGroup
import com.kos.figure.FigureEmpty
import com.kos.figure.IFigure
import com.kos.figure.collections.FigureList
import com.kos.figure.composition.Figure3dTransform
import com.kos.figure.segments.model.PathElement
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import vectors.Matrix
import vectors.Vec2
import kotlin.math.PI
import com.kos.boxdrawe.presentation.segments.SegmentUtils
import com.kos.boxdrawe.presentation.segments.SegmentUtils.getBlockCenter
import com.kos.boxdrawe.presentation.segments.SegmentUtils.getBlockDistance
import com.kos.boxdrawe.presentation.segments.SegmentUtils.mapBlock
import com.kos.figure.composition.FigureColor
import com.kos.figure.segments.model.EmptyPath

class SegmentsToolsData(val tools: ITools) {

    private val _instrument = MutableStateFlow(Instruments.INSTRUMENT_NONE)
    val instrument: StateFlow<Int> = _instrument

    val blocks = MutableStateFlow<SegmentBlockGroup>(SegmentBlockGroup(emptyList()))
    private val previewElement = MutableStateFlow<List<PathElement>?>(null)
    private val hoveredBlock = MutableStateFlow<SegmentBlock?>(null)
    private val selectedBlocks = MutableStateFlow<List<SegmentBlock>>(emptyList())



    private val points = mutableListOf<Vec2>()
    private var isDrawing = false

    private var movingBlock: SegmentBlock? = null
    private var dragStartPoint: Vec2 = Vec2.Zero
    private var originalMatrix: Matrix = Matrix.identity
    private var pivotPoint: Vec2 = Vec2.Zero

    val figures = combine(blocks, previewElement, hoveredBlock, selectedBlocks) { group, preview, hovered, selected ->
        val blockFigures = group.blocks.map { b ->
            val f = mapBlock(b)
            if (b in selected) {
                // Выделяем выбранные блоки синим цветом
                FigureColor(0xFF0000FF.toInt(), 1, f)
            } else f
        }
        val previewFigure = FigureList( preview?.map { SegmentUtils.toFigure(it) }.orEmpty())

        // Подсвечиваем фигуру под курсором красным
        val highlightFigure = hovered?.let {
            if (it !in selected)
                FigureColor(0xFFFF0000.toInt(), 2, mapBlock(it))
            else null
        }

        val list = FigureList(blockFigures + listOfNotNull(previewFigure, highlightFigure))

        if (group.matrix.isIdentity()) {
            list
        } else {
            Figure3dTransform(group.matrix, list)
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


        if (_instrument.value == Instruments.INSTRUMENT_UNGROUP) {
            if (hovered != null) {
                ungroup(hovered)
            }
        }

        if (_instrument.value == Instruments.INSTRUMENT_GROUP) {
            if (hovered != null) {
                if (hovered in selectedBlocks.value) {
                    // Если кликнули по уже выбранному — группируем
                    groupSelected(selectedBlocks.value)
                    selectedBlocks.value = emptyList()
                } else {
                    // Добавляем в список выбора
                    selectedBlocks.value = selectedBlocks.value + hovered
                }
            } else {
                // Клик по пустому месту — сброс
                selectedBlocks.value = emptyList()
            }
            return
        }

        if (_instrument.value == Instruments.INSTRUMENT_MOVE ||
                _instrument.value == Instruments.INSTRUMENT_ROTATE ||
                _instrument.value == Instruments.INSTRUMENT_SCALE ) {

            movingBlock = hovered
            dragStartPoint = point
            if (hovered != null) {
                originalMatrix = hovered.matrix
                // Центр фигуры для вращения/масштабирования
                pivotPoint = getBlockCenter(hovered)
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
            Instruments.INSTRUMENT_POLYGON ->3
            Instruments.INSTRUMENT_RECTANGLE ->3
            else -> 0
        }

        if (points.size >= requiredPoints && requiredPoints > 0) {
            val element = createPathElement(points, null)
            if (element != null && element.isNotEmpty()) {
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

            var updatedMovingBlock: SegmentBlock? = null
            val newBlocks = blocks.value.blocks.map { block ->
                if (block.isSame(moving)) {
                    val updated = block.copy(matrix = newMatrix)
                    updatedMovingBlock = updated
                    updated
                } else block
            }

            blocks.value = SegmentBlockGroup(newBlocks)
            movingBlock = updatedMovingBlock
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
            val dist = getBlockDistance(block, point)
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

    private fun createPathElement(pts: List<Vec2>, current: Vec2?): List<PathElement>? {
        val allPoints = if (current != null) pts + current else pts

        return when (_instrument.value) {
            Instruments.INSTRUMENT_LINE -> {
                listOfNotNull(SegmentUtils.createPathLine(allPoints))
            }
            Instruments.INSTRUMENT_CIRCLE -> {
                listOfNotNull(SegmentUtils.createPathCircle(allPoints))
            }
            Instruments.INSTRUMENT_ELLIPSE -> {
                listOfNotNull(SegmentUtils.createPathEllipse(allPoints))
            }
            Instruments.INSTRUMENT_BEZIER -> {
                listOfNotNull(SegmentUtils.createPathBezier(allPoints))
            }
            Instruments.INSTRUMENT_RECTANGLE -> {
                SegmentUtils.createPathRectangle(allPoints)
            }
            Instruments.INSTRUMENT_POLYGON -> {
                SegmentUtils.createPathPolygon(allPoints)
            }
            else -> null
        }
    }



    fun onRelease(point: Vec2, button: Int, scale: Float) {
        movingBlock = null
    }

    fun ungroup(block: SegmentBlock){
        if (block.isGroup){
            val children = block.children
            val nw = children.map { child ->
                child.copy(matrix = child.matrix.copyWithTransform(block.matrix))
            }
            blocks.value = SegmentBlockGroup(
                blocks = blocks.value.blocks.filter { !it.isSame(block) }+nw
            )
        }
    }

    fun groupSelected(selected: List<SegmentBlock>) {
        if (selected.size < 2) return

        // Создаем группу. В качестве element можно передать EmptyPath,
        // так как вся геометрия теперь в children.
        val newGroup = SegmentBlock(
            element = EmptyPath,
            children = selected
        )

        blocks.value = SegmentBlockGroup(
            blocks = blocks.value.blocks.filter { it !in selected } + newGroup
        )
    }
}
