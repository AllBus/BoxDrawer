package com.kos.boxdrawe.presentation

import com.kos.boxdrawe.widget.NumericTextFieldState
import com.kos.boxdrawer.figure.FigureExtractor
import com.kos.figure.FigureEllipse
import com.kos.figure.FigureEmpty
import com.kos.figure.FigureList
import com.kos.figure.FigurePolygon
import com.kos.figure.IFigure
import com.kos.figure.composition.FigureArray
import com.kos.figure.composition.FigureColor
import com.kos.figure.composition.FigureComposition
import com.kos.figure.matrix.FigureMatrixScale
import kotlinx.coroutines.flow.MutableStateFlow
import org.kabeja.dxf.DXFDocument
import org.kabeja.parser.DXFParser
import org.kabeja.parser.ParserBuilder
import vectors.Vec2
import java.io.File
import java.io.FileInputStream

class DxfToolsData(override val tools: ITools): SaveFigure {

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
        return "f ("+figures.print()+")"
    }

    override suspend fun createFigure(): IFigure {
        return currentFigure.value
    }

    fun redrawBox(){
        val scale = scaleEdit.decimal
        val color1 = scaleColor.decimal.toInt()
        if (scale!= 0.0){
            val f = loadedFigure.value

            val scale2 = scaleEdit2.decimal
            val color2 = scaleColor2.decimal.toInt()
            val c1 = scaleForColor(  com.jsevy.jdxf.DXFColor.getRgbColor(color1), f, scale)
            val c2 = scaleForColor(  com.jsevy.jdxf.DXFColor.getRgbColor(color2), c1, scale2)
            currentFigure.value = c2
         }
    }

    private fun scaleForColor(rgbColor: Int, figure: IFigure, scale: Double): IFigure {
        return when (figure){
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

    private fun changeScale(figure: IFigure, scale: Double): IFigure {
        return when (figure){
            is FigurePolygon -> {
                val rect = figure.rect()
                val c = Vec2(rect.centerX, rect.centerY)
                val points = figure.points.map { (it - c)*scale+c }
               // println("Super $scale")
                figure.create(points)
            }
            is FigureEllipse -> {
                figure.create(
                    center = figure.center,
                    radius = figure.radius*scale,
                    radiusMinor = figure.radiusMinor*scale,
                    rotation = figure.rotation,
                    segmentStart = figure.segmentStart,
                    segmentSweep = figure.segmentSweep
                    )
            }
            is FigureArray ->
                FigureArray(
                    changeScale(figure.figure, scale),
                    startPoint = figure.startPoint,
                    distance = figure.distance,
                    columns = figure.columns,
                    rows = figure.rows,
                    angle = figure.angle,
                    scaleX = figure.scaleX,
                    scaleY = figure.scaleY,
                    figureStart = figure.figureStart?.let {  changeScale(it, scale) },
                    figureEnd = figure.figureEnd?.let {  changeScale(it, scale) },
                )
            is FigureComposition ->
                figure.create(changeScale(figure.figure, scale))
            is FigureList ->
                FigureList(
                    figure.collection().map { f ->
                        changeScale( f, scale)
                    }
                )
            else -> figure
        }

    }

    val scaleEdit = NumericTextFieldState(1.0) { redrawBox() }
    val scaleColor = NumericTextFieldState(0.0, digits = 0) { redrawBox() }

    val scaleEdit2 = NumericTextFieldState(1.0) { redrawBox() }
    val scaleColor2 = NumericTextFieldState(0.0, digits = 0) { redrawBox() }
}