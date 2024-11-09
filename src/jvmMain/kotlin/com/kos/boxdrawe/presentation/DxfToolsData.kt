package com.kos.boxdrawe.presentation

import androidx.compose.runtime.mutableStateOf
import com.kos.boxdrawe.presentation.model.BindingType
import com.kos.boxdrawe.widget.NumericTextFieldState
import com.kos.boxdrawer.figure.FigureExtractor
import com.kos.figure.FigureBezier
import com.kos.figure.FigureCircle
import com.kos.figure.FigureEllipse
import com.kos.figure.FigureEmpty
import com.kos.figure.FigureInfo
import com.kos.figure.FigureLine
import com.kos.figure.FigurePolyline
import com.kos.figure.IFigure
import com.kos.figure.collections.FigureList
import com.kos.figure.collections.FigurePoints
import com.kos.figure.composition.FigureColor
import com.kos.figure.composition.FigureComposition
import com.kos.figure.composition.FigureTranslate
import com.kos.figure.editor.FigureMutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import org.kabeja.dxf.DXFDocument
import org.kabeja.parser.DXFParser
import org.kabeja.parser.ParserBuilder
import turtoise.FigureCreator
import turtoise.FigureCreator.changeScale
import turtoise.paint.PaintUtils
import turtoise.paint.PointInfo
import vectors.Matrix
import vectors.Vec2
import java.io.File
import java.io.FileInputStream
import kotlin.math.abs

class DxfToolsData(override val tools: ITools) : SaveFigure {

    private val loadedFigure = MutableStateFlow<IFigure>(FigureEmpty)

    private val currentFigure = MutableStateFlow<IFigure>(FigureEmpty)

    private val _instrument = MutableStateFlow(Instruments.INSTRUMENT_NONE)
    val instrument: StateFlow<Int> = _instrument
    val privjazka = mutableStateOf(false)

    fun changeInstrument(value: Int) {
        appendFigure()
        _instrument.value = value
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val instrumentState = _instrument.mapLatest { v ->
        InstrumentState(v)
    }

    private var startPoint: Vec2 = Vec2.Zero
    private var endPoint: Vec2 = Vec2.Zero

    private var intersectPoint: MutableStateFlow<List<Vec2>> = MutableStateFlow(emptyList())
    private var magnetPoint: MutableStateFlow<Vec2?> = MutableStateFlow(null)
    private var currentPoint: MutableStateFlow<List<Vec2>> = MutableStateFlow(emptyList())
    private var currentScale: MutableStateFlow<Float> = MutableStateFlow(1.0f)

    private val editFigure: MutableStateFlow<IFigure> = MutableStateFlow(FigureEmpty)

    private var currentPointInfo: PointInfo? = null

    private val vspomogatelnieFigure =
        combine(intersectPoint, magnetPoint, currentPoint, currentScale) { i, m, c, scale ->
            FigureList(
                listOfNotNull(
                    m?.let { magentPoint(it, scale) },
                    currentPoints(c, scale),
                    intersectFigures(i, scale)
                )
            )
        }

    val figures =
        combine(currentFigure, editFigure, vspomogatelnieFigure) { figure, editFigure, pomo ->
            FigureList(
                listOf(
                    figure,
                    pomo,
                    editFigure,
                )
            )
        }

    fun recalcFigure() {
        val pointList = currentPoint.value
        val delta = (endPoint - startPoint)
        val r = when (_instrument.value) {
            Instruments.INSTRUMENT_POINTER -> selectedFigure.getOrNull(0)?.figure?.let { f ->
                FigureTranslate(
                    delta,
                    f
                )
            } ?: FigureEmpty

            Instruments.INSTRUMENT_LINE -> FigureLine(startPoint, endPoint)
            Instruments.INSTRUMENT_RECTANGLE -> FigurePolyline(
                listOf(
                    startPoint,
                    Vec2(endPoint.x, startPoint.y),
                    endPoint,
                    Vec2(startPoint.x, endPoint.y)
                ), true
            )

            Instruments.INSTRUMENT_CIRCLE -> FigureCircle(
                center = startPoint,
                radius = delta.magnitude,
                outSide = true
            )

            Instruments.INSTRUMENT_ELLIPSE -> {
                FigureEllipse(
                    center = (endPoint + startPoint) / 2.0,
                    radius = abs(delta.x) / 2.0,
                    radiusMinor = abs(delta.y) / 2.0,
                    rotation = 0.0,
                    outSide = true
                )
            }

            Instruments.INSTRUMENT_POLYGON -> FigureCreator.regularPolygon(
                startPoint,
                6,
                delta.angle,
                delta.magnitude
            )

            Instruments.INSTRUMENT_MULTI,
            Instruments.INSTRUMENT_POLYLINE ->  FigurePolyline(pointList + endPoint, false)

            Instruments.INSTRUMENT_BEZIER -> {
                val pp = pointList.toList()
                if (pp.isEmpty())
                    FigureEmpty
                else {
                    val pr = if (pp.last() != endPoint) {
                        pp + endPoint
                    } else pp
                    val cou = (pr.size - 1) % 3


                    FigureBezier(pr.dropLast(cou))
                }
            }

            Instruments.INSTRUMENT_BEZIER_TREE_POINT -> {
                val st = pointList.getOrNull(0)
                val pp = pointList.getOrNull(1)
                if (pp == null || st == null) {
                    FigureLine(startPoint, endPoint)
                } else {
                    FigureCreator.cubicBezierForThreePoints(st, endPoint, pp)
                }
            }

            Instruments.INSTRUMENT_NONE -> FigureEmpty
            else -> FigureEmpty

        }

        editFigure.value = r
    }

    fun appendPointOrFigure(count: Int) {
        val pointList = currentPoint.value
        if (pointList.size >= count) {
            appendFigure()
        } else {
            appendPoint()
        }
    }

    fun appendPoint() {
        val pointList = currentPoint.value
        if (pointList.isEmpty() || pointList.last() != endPoint) {
            currentPoint.value = pointList + endPoint
        }
    }


    fun appendFigure() {
        val ev = editFigure.value
        val v = currentFigure.value
        if (ev == FigureEmpty)
            return

        recalcIntersectPoints(intersectPoint.value, v, ev)

        currentFigure.value = when (v) {
            is FigureMutableList -> v.add(ev)
            is FigureList -> FigureMutableList(mutableListOf<IFigure>(ev).apply { this.addAll(v.collection()) })
            else -> FigureMutableList(mutableListOf(ev, v))
        }

        editFigure.value = FigureEmpty
        currentPoint.value = emptyList()
    }

    private fun recalcIntersectPoints(oldPoint: List<Vec2>, v: IFigure, ev: IFigure) {
        if (ev == FigureEmpty || v == FigureEmpty)
            return

        intersectPoint.value = oldPoint + PaintUtils.findAllIntersects(listOf(v), listOf(ev))
    }

    fun recalcIntersectPoints() {
        intersectPoint.value = PaintUtils.findAllIntersects(listOf(currentFigure.value))

    }

    fun loadDxf(fileName: String) {
        clear()

        try {
            editFigure.value = FigureEmpty
            currentPointInfo = null

            val f = File(fileName)
            val parser = ParserBuilder.createDefaultParser()

            parser.parse(FileInputStream(f), DXFParser.DEFAULT_ENCODING)
            tools.updateChooserDir(fileName)

            val doc: DXFDocument = parser.getDocument()

            val extractor = FigureExtractor()
            loadedFigure.value = extractor.extractFigures(doc)
            redrawBox()


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun print(): String {
        val figures = currentFigure.value
        return "f (" + figures.print() + ")"
    }

    private var selectedFigure: List<FigureInfo> = emptyList()

    suspend fun onPress(
        point: Vec2,
        button: Int,
        scale: Float,
        selectedItem: MutableStateFlow<List<FigureInfo>>
    ) {
        selectedFigure = selectedItem.value
        when (_instrument.value) {
            Instruments.INSTRUMENT_NONE -> {
                val figure = currentFigure.value
                val result =
                    PaintUtils.findFiguresAtCursor(Matrix.identity, point, 1.0, listOf(figure))

                selectedItem.value = result
            }

            Instruments.INSTRUMENT_POINTER -> {
                val info: PointInfo? = PaintUtils.takePoint(selectedItem.value, point, 1.0)
                currentPointInfo = info
                if (info != null) {
                    removeFigure(info.figure)
                }
            }

            else -> {

                //   println("point -> $point $button")
                if (Instruments.button(button) == Instruments.POINTER_LEFT) {
                    startPoint = findPoint(point, createBinding(button), scale)
                    endPoint = startPoint
                    val pointList = currentPoint.value
                    if (pointList.isEmpty()) {
                        currentPoint.value = listOf(startPoint)
                    }
                    recalcFigure()
                }
            }
        }
    }

    fun createBinding(button: Int): BindingType {
        return BindingType(
            intersection = (button and Instruments.MODIFIER_CTRL) == 0,
            nearest = (button and Instruments.MODIFIER_SHIFT) == 0,
            grid = false,
            points = (button and Instruments.MODIFIER_SHIFT) == 0,
        )
    }

    private fun removeFigure(figure: IFigure) {
        currentFigure.value = PaintUtils.removeFigure(currentFigure.value, figure)
    }

    val magnetDistance = 10.0

    private fun findPoint(point: Vec2, binding: BindingType, scale: Float): Vec2 {
        if (!privjazka.value) {
            return point
        }
        val figure = currentFigure.value

        if (binding.intersection) {
            val ip = intersectPoint.value

            val minip = if (ip.isEmpty())
                null
            else
                ip.minBy { Vec2.distance(point, it) }
                    .takeIf { Vec2.distance(point, it) < magnetDistance / scale }

            if (minip != null) {
                magnetPoint.value = minip
                return minip
            }
        }

        if (binding.nearest) {
            val newPoint = PaintUtils.findPointAtCursor(
                Matrix.identity,
                point,
                magnetDistance / scale,
                listOf(figure)
            )
            if (newPoint != null) {
                magnetPoint.value = newPoint
                return newPoint
            }
        }

        magnetPoint.value = null
        return point
    }

    private fun magentPoint(
        newPoint: Vec2,
        scale: Float
    ) = FigureCreator.colorDxf(2, FigureCircle(newPoint, magnetDistance / scale, true))

    private fun intersectFigures(intersectPoint: List<Vec2>, scale: Float) =
        FigureCreator.colorDxf(3, FigurePoints(intersectPoint, magnetDistance / scale))

    private fun currentPoints(pointList: List<Vec2>, scale: Float) =
        FigureCreator.colorDxf(5, FigurePoints(pointList.toList(), magnetDistance / scale))

    suspend fun onMove(
        point: Vec2,
        button: Int,

        scale: Float,
        selectedItem: MutableStateFlow<List<FigureInfo>>
    ) {
        if (currentScale.value != scale) {
            currentScale.value = scale
        }
        //   println("onMove -> $point $button")
        if (button == Instruments.POINTER_LEFT) {
            when (_instrument.value) {
                Instruments.INSTRUMENT_MULTI -> {
                    appendPoint()
                }

                Instruments.INSTRUMENT_POINTER -> {

                }
            }
            endPoint = findPoint(point, createBinding(button), scale)
            recalcFigure()
        } else {
            val fp = findPoint(point, createBinding(button), scale)
            when (_instrument.value) {
                Instruments.INSTRUMENT_POLYLINE,
                Instruments.INSTRUMENT_BEZIER -> {
                    endPoint = fp
                    recalcFigure()
                }

                else -> {}
            }
        }
    }

    suspend fun onRelease(
        point: Vec2,
        button: Int,
        scale: Float,
        selectedItem: MutableStateFlow<List<FigureInfo>>
    ) {
        //   println("onRelease -> $point $button")
        when (Instruments.button(button)) {
            Instruments.POINTER_LEFT ->
                when (_instrument.value) {
                    Instruments.INSTRUMENT_BEZIER_TREE_POINT -> {
                        appendPointOrFigure(2)
                    }

                    Instruments.INSTRUMENT_POLYLINE,
                    Instruments.INSTRUMENT_BEZIER ->
                        appendPoint()

                    else ->
                        appendFigure()
                }

            Instruments.POINTER_RIGHT -> {
                if (_instrument.value == Instruments.INSTRUMENT_MULTI) {
                    moveAllToLeft()
                }
                val pointList = currentPoint.value
                if (pointList.isNotEmpty()) {
                    currentPoint.value = pointList.dropLast(1)
                    recalcFigure()
                }
            }

            Instruments.POINTER_MIDDLE -> {
                appendFigure()

            }
        }
    }

    override suspend fun createFigure(): IFigure {
        return currentFigure.value
    }

    fun redrawBox() {
        val scale = scaleEdit.decimal
        val color1 = scaleColor.decimal.toInt()
        if (scale != 0.0) {
            val f = loadedFigure.value

            val scale2 = scaleEdit2.decimal
            val color2 = scaleColor2.decimal.toInt()

            val scale3 = scaleEdit3.decimal
            val color3 = scaleColor3.decimal.toInt()

            val c1 = scaleForColor(com.jsevy.jdxf.DXFColor.getRgbColor(color1), f, scale)
            val c2 = scaleForColor(com.jsevy.jdxf.DXFColor.getRgbColor(color2), c1, scale2)
            val c3 = scaleForColor(com.jsevy.jdxf.DXFColor.getRgbColor(color3), c2, scale3)
            currentFigure.value = c3
        }
    }

    fun moveAllToLeft() {

    }

    private fun scaleForColor(rgbColor: Int, figure: IFigure, scale: Double): IFigure {
        return when (figure) {
            is FigureList -> FigureList(
                figure.collection().map { f ->
                    scaleForColor(rgbColor, f, scale)
                }
            )

            is FigureColor -> {
                //  println("Scale for $rgbColor ${figure.color} ${figure.color == rgbColor}")
                if (figure.color == rgbColor) {

                    FigureColor(
                        figure.color,
                        figure.dxfColor,
                        changeScale(figure.figure, scale)
                    )
                } else {
                    FigureColor(
                        figure.color,
                        figure.dxfColor,
                        scaleForColor(rgbColor, figure.figure, scale)
                    )
                }
            }

            is FigureComposition ->
                figure.create(scaleForColor(rgbColor, figure.figure, scale))

            else -> figure
        }
    }

    fun clear() {
        editFigure.value = FigureEmpty
        intersectPoint.value = emptyList()
        currentPoint.value = emptyList()
        currentPointInfo = null
        loadedFigure.value = FigureEmpty
        currentFigure.value = FigureEmpty
    }


    val scaleEdit = NumericTextFieldState(1.0, digits = 3, minValue = 0.000001) { redrawBox() }
    val scaleColor = NumericTextFieldState(0.0, digits = 0, maxValue = 1000.0) { redrawBox() }

    val scaleEdit2 = NumericTextFieldState(1.0, digits = 3, minValue = 0.000001) { redrawBox() }
    val scaleColor2 = NumericTextFieldState(0.0, digits = 0, maxValue = 1000.0) { redrawBox() }

    val scaleEdit3 = NumericTextFieldState(1.0, digits = 3, minValue = 0.000001) { redrawBox() }
    val scaleColor3 = NumericTextFieldState(0.0, digits = 0, maxValue = 1000.0) { redrawBox() }

    //   val selectedItem = MutableStateFlow<List<IFigure>>(emptyList())
}