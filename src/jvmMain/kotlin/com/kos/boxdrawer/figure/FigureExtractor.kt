package com.kos.boxdrawer.figure

import com.kos.figure.FigureBezier
import com.kos.figure.FigureCircle
import com.kos.figure.FigureEllipse
import com.kos.figure.FigureEmpty
import com.kos.figure.FigureLine
import com.kos.figure.collections.FigureList
import com.kos.figure.FigurePolyline
import com.kos.figure.FigureText
import com.kos.figure.IFigure
import com.kos.figure.composition.FigureArray
import com.kos.figure.composition.FigureColor
import org.kabeja.dxf.DXFArc
import org.kabeja.dxf.DXFCircle
import org.kabeja.dxf.DXFDocument
import org.kabeja.dxf.DXFEllipse
import org.kabeja.dxf.DXFEntity
import org.kabeja.dxf.DXFInsert
import org.kabeja.dxf.DXFLine
import org.kabeja.dxf.DXFPoint
import org.kabeja.dxf.DXFPolyline
import org.kabeja.dxf.DXFSpline
import org.kabeja.dxf.DXFText
import org.kabeja.dxf.helpers.Point
import vectors.Vec2

class FigureExtractor {

    fun extractFigures(doc: DXFDocument): IFigure {
        val result = mutableListOf<IFigure>()

        for (layer in doc.dxfLayerIterator) {
            var currentColor = layer.color
            var colorBlock = mutableListOf<IFigure>()
            println( layer.name+" -- "+layer.color)
            for (t in layer.dxfEntityTypeIterator) {
                for (entry in layer.getDXFEntities(t)) {
                    if (entry.color != currentColor) {
                        if (colorBlock.isNotEmpty()) {
                            result += FigureColor(
                                color = com.jsevy.jdxf.DXFColor.getRgbColor(currentColor),
                                dxfColor = currentColor,
                                figure = FigureList(colorBlock.toList()).simple()
                            )
                            //println("" +currentColor+" as "+  com.jsevy.jdxf.DXFColor.getRgbColor(currentColor))
                            colorBlock = mutableListOf<IFigure>()
                        }
                        currentColor = entry.color
                    }
                    val figure = createFigure(entry, doc)
                    if (figure != FigureEmpty) {
                //        println( entry.id+" "+entry.color)
                        colorBlock += figure
                    }
                } //end for entry
            }
            result += FigureColor(
                color = com.jsevy.jdxf.DXFColor.getRgbColor(currentColor),
                dxfColor = currentColor,
                figure = FigureList(colorBlock.toList()).simple()
            )
       //     println("" +currentColor+" as "+  com.jsevy.jdxf.DXFColor.getRgbColor(currentColor))
        }
        return FigureList(result.toList())
    }

    private fun createFigure(
        entry: DXFEntity,
        doc: DXFDocument
    ): IFigure = when (entry) {
        is DXFLine ->
            FigureLine(entry.startPoint.vec, entry.endPoint.vec)

        is DXFPolyline -> {
            val vertex = entry.vertexIterator
            FigurePolyline(
                vertex.asSequence().map { it.vec }.toList(), entry.isClosed
            )
        }

        is DXFSpline -> {
            if (entry.degree == 3) {
                FigureBezier(
                    entry.splinePointIterator.asSequence().map { it.vec }.toList()
                )
            } else
                FigureEmpty
        }

        is DXFCircle ->
            FigureCircle(entry.centerPoint.vec, entry.radius,           outSide = true,)

        is DXFEllipse -> {

            //println("E ${entry.ratio} ${entry.halfMajorAxisLength} ${entry.rotationAngle}")
            FigureEllipse(
                center = entry.centerPoint.vec,
                radius = entry.halfMajorAxisLength,
                radiusMinor = entry.ratio * entry.halfMajorAxisLength,
                rotation = entry.rotationAngle,
                outSide = true,
                segmentStartAngle = entry.startParameter,
                segmentSweepAngle = if (entry.endParameter == entry.startParameter) Math.PI*2 else
                    (entry.endParameter - entry.startParameter),
            )
        }

        is DXFArc -> {
            //println("A ${entry.radius} ${entry.startAngle} ${entry.endAngle} ${entry.isCounterClockwise}")

            FigureCircle(
                center = entry.centerPoint.vec,
                radius = entry.radius,
                outSide = true,
                segmentStartAngle = Math.toRadians (entry.startAngle),
                segmentSweepAngle = if (entry.endAngle == entry.startAngle) Math.PI*2 else
                        Math.toRadians (entry.endAngle - entry.startAngle)
            )
        }

        is DXFInsert -> {
            doc.getDXFBlock(entry.blockID)?.let { block ->
                val f = FigureList(
                    block.dxfEntitiesIterator.asSequence().map {
                        createFigure(it, doc)
                    }.toList()
                )
                FigureArray(
                    figure = f,
                    startPoint = entry.point.vec,
                    distance = Vec2(entry.columnSpacing, entry.rowSpacing),
                    columns = entry.columns,
                    rows = entry.rows,
                    angle = entry.rotate * 180.0 / Math.PI,
                    scaleX = entry.scaleX,
                    scaleY = entry.scaleY,
                )
            } ?: FigureEmpty
        }
        is DXFText -> {
            val t = entry.text
            FigureText(t)
        }


        else -> FigureEmpty
    }

    private val Point.vec get() = Vec2(this.x, -this.y)
    private val DXFPoint.vec get() = Vec2(this.x, -this.y)
}