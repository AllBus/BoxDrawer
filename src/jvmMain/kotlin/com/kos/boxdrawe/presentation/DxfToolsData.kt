package com.kos.boxdrawe.presentation

import com.kos.boxdrawe.widget.NumericTextFieldState
import com.kos.boxdrawer.figure.FigureExtractor
import com.kos.figure.FigureCircle
import com.kos.figure.FigureEmpty
import com.kos.figure.FigureInfo
import com.kos.figure.FigureLine
import com.kos.figure.FigurePolyline
import com.kos.figure.IFigure
import com.kos.figure.collections.FigureList
import com.kos.figure.composition.FigureColor
import com.kos.figure.composition.FigureComposition
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import org.kabeja.dxf.DXFDocument
import org.kabeja.parser.DXFParser
import org.kabeja.parser.ParserBuilder
import turtoise.FigureCreator
import turtoise.FigureCreator.changeScale
import turtoise.paint.PaintUtils
import vectors.Matrix
import vectors.Vec2
import java.io.File
import java.io.FileInputStream

class DxfToolsData(override val tools: ITools) : SaveFigure {

    private val loadedFigure = MutableStateFlow<IFigure>(FigureEmpty)


    private val currentFigure = MutableStateFlow<IFigure>(FigureEmpty)
    val dxfPreview = MutableStateFlow(false)

    val instrument = MutableStateFlow(Instruments.INSTRUMENT_NONE)

    @OptIn(ExperimentalCoroutinesApi::class)
    val instrumentState = instrument.mapLatest { v ->
        InstrumentState(v)
    }

    private var startPoint: Vec2 = Vec2.Zero
    private var endPoint: Vec2 = Vec2.Zero

    private val editFigure: MutableStateFlow<IFigure> = MutableStateFlow(FigureEmpty)
    private val vspomogatelnieFigure: MutableStateFlow<IFigure> = MutableStateFlow(FigureEmpty)

    val figures = combine(currentFigure, editFigure, vspomogatelnieFigure) { figure, editFigure, pomo ->
        FigureList(
            listOf(
                figure,
                pomo,
                editFigure
            )
        )
    }

    fun recalcFigure() {
        val r = when (instrument.value) {
            Instruments.INSTRUMENT_LINE -> FigureLine(startPoint, endPoint)
            Instruments.INSTRUMENT_RECTANGLE -> FigurePolyline(
                listOf(
                    startPoint,
                    Vec2(endPoint.x, startPoint.y),
                    endPoint,
                    Vec2(startPoint.x, endPoint.y)
                ), true
            )

            else -> FigureEmpty

        }

        editFigure.value = r
    }

    fun appendFigure() {
        val v = currentFigure.value
        currentFigure.value = if (v is FigureList) {
            FigureList(v.collection() + editFigure.value)
        } else {
            FigureList(listOf(v, editFigure.value))
        }
    }

    fun loadDxf(fileName: String) {
        try {
            val f = File(fileName)
            val parser = ParserBuilder.createDefaultParser()

            parser.parse(FileInputStream(f), DXFParser.DEFAULT_ENCODING)
            tools.updateChooserDir(fileName)

            val doc: DXFDocument = parser.getDocument()

            val extractor = FigureExtractor()
            loadedFigure.value = extractor.extractFigures(doc)
            redrawBox()


            dxfPreview.value = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun print(): String {
        val figures = currentFigure.value
        return "f (" + figures.print() + ")"
    }

    suspend fun onPress(
        point: Vec2,
        button: Int,
        scale: Float,
        selectedItem: MutableStateFlow<List<FigureInfo>>
    ) {
        if (instrument.value == Instruments.INSTRUMENT_NONE) {
            val figure = currentFigure.value
            val result = PaintUtils.findFiguresAtCursor(Matrix.identity, point, 1.0, listOf(figure))

            selectedItem.value = result
        } else {

            //   println("point -> $point $button")
            if (button == Instruments.POINTER_LEFT) {
                startPoint = findPoint(point, scale)
                endPoint = findPoint(point, scale)
                recalcFigure()
            }
        }
    }

    val magnetDistance = 10.0
    private fun findPoint(point: Vec2, scale: Float): Vec2 {
        val figure = currentFigure.value
        val newPoint = PaintUtils.findPointAtCursor(Matrix.identity, point, magnetDistance/scale, listOf(figure))
        if (newPoint != null){
            vspomogatelnieFigure.value = FigureCreator.colorDxf(2, FigureCircle(newPoint, magnetDistance/scale,true))
            return newPoint
        } else {
            vspomogatelnieFigure.value = FigureEmpty
        }

        return point
    }

    suspend fun onMove(
        point: Vec2,
        button: Int,
        scale: Float,
        selectedItem: MutableStateFlow<List<FigureInfo>>
    ) {
        //   println("onMove -> $point $button")
        if (button == Instruments.POINTER_LEFT) {
            endPoint = findPoint(point, scale)
            recalcFigure()
        }else{
            findPoint(point, scale)
        }
    }

    suspend fun onRelease(
        point: Vec2,
        button: Int,
        scale: Float,
        selectedItem: MutableStateFlow<List<FigureInfo>>
    ) {
        //   println("onRelease -> $point $button")
        if (button == Instruments.POINTER_LEFT) {
            appendFigure()
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


    val scaleEdit = NumericTextFieldState(1.0, digits = 3, minValue = 0.000001) { redrawBox() }
    val scaleColor = NumericTextFieldState(0.0, digits = 0, maxValue = 1000.0) { redrawBox() }

    val scaleEdit2 = NumericTextFieldState(1.0, digits = 3, minValue = 0.000001) { redrawBox() }
    val scaleColor2 = NumericTextFieldState(0.0, digits = 0, maxValue = 1000.0) { redrawBox() }

    val scaleEdit3 = NumericTextFieldState(1.0, digits = 3, minValue = 0.000001) { redrawBox() }
    val scaleColor3 = NumericTextFieldState(0.0, digits = 0, maxValue = 1000.0) { redrawBox() }

    //   val selectedItem = MutableStateFlow<List<IFigure>>(emptyList())
}