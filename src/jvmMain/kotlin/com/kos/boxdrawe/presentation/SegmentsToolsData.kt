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
import kotlin.math.abs

class SegmentsToolsData(val tools: ITools) {

    private val _instrument = MutableStateFlow(Instruments.INSTRUMENT_NONE)
    val instrument: StateFlow<Int> = _instrument

    val blocks = MutableStateFlow<SegmentBlockGroup>(SegmentBlockGroup(emptyList()))
    private val previewElement = MutableStateFlow<PathElement?>(null)

    private var startPoint: Vec2? = null
    private var isDrawing = false

    val figures = combine(blocks, previewElement) { group, preview ->
        val blockFigures = group.blocks.map { b -> mapBlock(b) }
        val previewFigure = preview?.let { toFigure(it) }
        
        val list = FigureList(blockFigures + listOfNotNull(previewFigure))
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
        startPoint = null
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

        val start = startPoint
        if (start == null) {
            startPoint = point
            isDrawing = true
        } else {
            val element = createPathElement(start, point)
            if (element != null) {
                blocks.value = SegmentBlockGroup(blocks.value.blocks + SegmentBlock(element))
            }
            resetDrawing()
        }
    }

    fun onMove(point: Vec2, button: Int, scale: Float) {
        val start = startPoint
        if (isDrawing && start != null) {
            previewElement.value = createPathElement(start, point)
        }
    }

    private fun createPathElement(start: Vec2, end: Vec2): PathElement? {
        return when (_instrument.value) {
            Instruments.INSTRUMENT_LINE -> Segment(start, end)
            Instruments.INSTRUMENT_CIRCLE -> {
                val radius = Vec2.distance(start, end)
                if (radius > 0) {
                    Arc(center = start, radius = radius, outSide = true, startAngle = 0.0, sweepAngle = Math.PI * 2)
                } else null
            }
            Instruments.INSTRUMENT_RECTANGLE -> {
                // Прямоугольник как набор сегментов или специфичный PathElement
                // Для упрощения пока только линия или окружность
                null
            }
            else -> null
        }
    }

    fun onRelease(point: Vec2, button: Int, scale: Float) {
        // Логика завершения может быть в onPress для двухэтапного рисования
    }
}
