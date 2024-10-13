package com.kos.boxdrawer.detal.splash.ai

import com.kos.boxdrawer.detal.splash.ISplashDetail
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_1
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_INT
import com.kos.figure.FigureBezier
import com.kos.figure.FigureCircle
import com.kos.figure.FigurePolyline
import com.kos.figure.IFigure
import com.kos.figure.collections.FigureList
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData
import turtoise.help.HelpDataParam
import turtoise.parser.TPArg
import vectors.Vec2
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class SplashChamomile: ISplashDetail {
    override val names: List<String>
        get() = listOf("chamomile", "flower")

    override fun help(): HelpData = HelpData(
        "chamomile radius petals","Draw a chamomile flower",
        listOf(
            HelpDataParam("radius", "Radius of the flower", FIELD_1),
            HelpDataParam("petals", "Number of petals", FIELD_INT)
        ),
        creator = TPArg.create("chamomile", TPArg("radius",FIELD_1), TPArg("petals", FIELD_INT))
    )

    override fun draw(builder: TortoiseBuilder, com: TortoiseCommand, figureExtractor: TortoiseFigureExtractor) {
        val radius = com.take(1, 100.0, figureExtractor.memory)
        val petals = com.take(2, 12.0, figureExtractor.memory).toInt()
        val centerRadius = com.take(3, radius / 4, figureExtractor.memory) //

        val figures = mutableListOf<IFigure>()

        // Create petal figures (using two Bezier curves per petal)
        for (i in 0 until petals) {
            val angle = 2 * PI * i / petals
            figures.addAll(createPetal(angle, radius, centerRadius, petals))
        }



        // Create center figure
        val centerFigure = FigureCircle(Vec2(0.0, 0.0), centerRadius, true)
        figures.add(centerFigure)// Add all figures as a single FigureList


        // Add all figures as a single FigureList
        val chamomileFigure = FigureList(figures)
        builder.addProduct(chamomileFigure)
    }

    private fun createPetal(angle: Double, radius: Double, centerRadius: Double, petals: Int): List<IFigure> {// Calculate starting point on the center circle
        val startAngle = angle + PI / petals / 2
        val startX = centerRadius * cos(startAngle)
        val startY = -centerRadius * sin(startAngle) // Note the minus sign here

        // Calculate ending point onthe center circle
        val endAngle = angle - PI / petals / 2
        val endX = centerRadius * cos(endAngle)
        val endY = -centerRadius * sin(endAngle) // Note the minus sign here

        // Calculate intermediate points for the petal shape (multiply y-coordinates by -1)
        val p1X = startX + (radius - centerRadius) * 0.4 * cos(angle + PI / petals / 4)
        val p1Y = -(startY + (radius - centerRadius) * 0.4 * sin(angle+ PI / petals / 4))
        val p2X = startX + (radius - centerRadius) * 0.9 * cos(angle)
        val p2Y = -(startY + (radius - centerRadius) * 1.3 * sin(angle))
        val p3X = endX + (radius - centerRadius) * 0.9 * cos(angle)
        val p3Y = -(endY + (radius - centerRadius) * 1.3 * sin(angle))
        val p4X = endX + (radius - centerRadius) * 0.4 * cos(angle - PI / petals / 4)
        val p4Y = -(endY + (radius - centerRadius) * 0.4 * sin(angle - PI / petals / 4))

        // Calculate control points for the notch (multiply y-coordinates by -1)
        val notchDepth = 0.1
        val notchWidth = 0.1
        val notchX = p2X + (p3X - p2X) * 0.5
        val notchY = -(p2Y + (p3Y- p2Y) * 0.5 + notchDepth * (radius - centerRadius))
        val notchControl1X = notchX - notchWidth * (radius - centerRadius) * cos(angle)
        val notchControl1Y = -(notchY + notchWidth * (radius - centerRadius) * sin(angle))
        val notchControl2X = notchX + notchWidth * (radius - centerRadius) * cos(angle)
        val notchControl2Y = -(notchY - notchWidth * (radius - centerRadius) * sin(angle))

        // Create Bezier curves for the petal
        return listOf(
            FigureBezier(listOf(Vec2(startX, startY), Vec2(p1X, p1Y), Vec2(p2X, p2Y), Vec2(notchControl1X, notchControl1Y))),
            FigureBezier(listOf(Vec2(notchControl1X, notchControl1Y), Vec2(notchX, notchY), Vec2(notchControl2X, notchControl2Y), Vec2(notchControl2X, notchControl2Y))),
            FigureBezier(listOf(Vec2(notchControl2X, notchControl2Y), Vec2(p3X, p3Y), Vec2(p4X, p4Y), Vec2(endX, endY)))
        )
    }

    private fun createPetal1(angle: Double, radius: Double, centerRadius: Double, petals: Int): List<IFigure> {
        // Calculate starting point on the center circle
        val startAngle = angle+ PI / petals
        val startX = centerRadius * cos(startAngle)
        val startY = centerRadius * sin(startAngle)

        // Calculate ending point on the center circle
        val endAngle = angle - PI / petals
        val endX = centerRadius * cos(endAngle)
        val endY = centerRadius * sin(endAngle)

        // Calculate control points for a more petal-like shape
        val controlPoint1 = Vec2(startX + (radius - centerRadius) * cos(angle) * 0.7, startY+ (radius - centerRadius) * sin(angle) * 0.7)
        val controlPoint2 = Vec2(endX + (radius - centerRadius) * cos(angle) * 0.7, endY + (radius - centerRadius) * sin(angle) * 0.7)

        // Create two Bezier curves and return them as a list
        return listOf(
            FigureBezier(listOf(Vec2(startX, startY), controlPoint1, controlPoint2, Vec2(endX, endY)))
        )
    }
}