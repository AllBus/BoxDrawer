package com.kos.boxdrawe.presentation

import com.kos.boxdrawe.widget.NumericTextFieldState
import com.kos.boxdrawer.figure.FigureExtractor
import com.kos.figure.FigureEllipse
import com.kos.figure.FigureEmpty
import com.kos.figure.FigureInfo
import com.kos.figure.collections.FigureList
import com.kos.figure.FigurePolygon
import com.kos.figure.IFigure
import com.kos.figure.composition.FigureArray
import com.kos.figure.composition.FigureColor
import com.kos.figure.composition.FigureComposition
import kotlinx.coroutines.flow.MutableStateFlow
import org.kabeja.dxf.DXFDocument
import org.kabeja.parser.DXFParser
import org.kabeja.parser.ParserBuilder
import turtoise.FigureCreator.changeScale
import turtoise.paint.PaintUtils
import vectors.Matrix
import vectors.Vec2
import java.io.File
import java.io.FileInputStream

class DxfToolsData(override val tools: ITools) : SaveFigure {

    private val loadedFigure = MutableStateFlow<IFigure>(FigureEmpty)
    val currentFigure = MutableStateFlow<IFigure>(FigureEmpty)
    val dxfPreview = MutableStateFlow(false)

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
        val figure = currentFigure.value
        val result = PaintUtils.findFiguresAtCursor(Matrix.identity, point, 1.0, listOf(figure))
        selectedItem.value = result
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



    val scaleEdit = NumericTextFieldState(1.0, minValue = 0.000001) { redrawBox() }
    val scaleColor = NumericTextFieldState(0.0, digits = 0, maxValue = 1000.0) { redrawBox() }

    val scaleEdit2 = NumericTextFieldState(1.0, minValue = 0.000001) { redrawBox() }
    val scaleColor2 = NumericTextFieldState(0.0, digits = 0, maxValue = 1000.0) { redrawBox() }

    val scaleEdit3 = NumericTextFieldState(1.0, minValue = 0.000001) { redrawBox() }
    val scaleColor3 = NumericTextFieldState(0.0, digits = 0, maxValue = 1000.0) { redrawBox() }

    //   val selectedItem = MutableStateFlow<List<IFigure>>(emptyList())
}