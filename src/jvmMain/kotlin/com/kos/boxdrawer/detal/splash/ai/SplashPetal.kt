package com.kos.boxdrawer.detal.splash.ai

import com.kos.boxdrawer.detal.splash.ISplashDetail
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_1
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_2
import com.kos.figure.FigureBezier
import com.kos.figure.collections.toFigure
import turtoise.help.HelpData
import turtoise.help.HelpDataParam
import turtoise.parser.TPArg
import vectors.Vec2
import kotlin.math.PI
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor

data class BezierCurve(val start: Vec2, val end: Vec2, val control1: Vec2, val control2: Vec2)

fun createBezierCurves(points: List<Vec2>, smoothnessFactor: Double = 0.25): List<BezierCurve> {
    val curves = mutableListOf<BezierCurve>()
    for (i in points.indices step 3) {
        val p0 = points[i]
        val p1 = points[i + 1]
        val p2 = points[i + 2]
        val p3 = points.getOrElse(i + 3) { points.last() } // Handle last group

        val c1 = p0 + (p1 - p0) * smoothnessFactor
        val c2 = p3 + (p2 - p3) * smoothnessFactor

        curves.add(BezierCurve(p0*100.0, p3*100.0, c1*100.0, c2*100.0))
    }
    return curves
}

fun createBezierCurvesThroughPoints(points: List<Vec2>, smoothnessFactor: Double = 0.5): List<BezierCurve> {
    val curves = mutableListOf<BezierCurve>()
    if (points.size < 2) return curves // Need at least 2 points for a curve

    for (i in 0 until points.size - 1) {
        val p0 = points[i]
        val p3 = points[i + 1]

        val p1 = if (i > 0) {
            p0 + (points[i] - points[i - 1]) * smoothnessFactor} else {
            p0
        }

        val p2 = if (i < points.size - 2) {
            p3 + (points[i + 1] - points[i + 2]) * smoothnessFactor
        } else {
            p3
        }

        curves.add(BezierCurve(p0*100.0, p3*100.0, p1*100.0, p2*100.0))
    }

    return curves
}

class SplashPetal : ISplashDetail {
    override val names: List<String>
        get() = listOf("petal")

    override fun help(): HelpData {
        return HelpData(
            "petal ax ay ox oy cxcy r",
            "Построить лепесток цветка",
            params = listOf(
                HelpDataParam("a", "Координата начала лепестка", FIELD_2),
                HelpDataParam("o", "Координата центра лепестка", FIELD_2),
                HelpDataParam("c", "Координата конца лепестка", FIELD_2),
                HelpDataParam("r", "радиус лепестка", FIELD_1),
            ),
            creator = TPArg.create("petal",
                TPArg("a", FIELD_2),
                TPArg("o", FIELD_2),
                TPArg("c", FIELD_2),
                TPArg("r", FIELD_1),
            )
        )
    }

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        val memory = figureExtractor.memory
        val a = Vec2(
            com[1, memory],
            com[2, memory],
        ).rotate(builder.angle) + builder.xy
        val o = Vec2(
            com[3, memory],
            com[4, memory],
        ).rotate(builder.angle) + builder.xy
        val b = Vec2(
            com[5, memory],
            com[6, memory],
        ).rotate(builder.angle) + builder.xy
        val r = com[7, memory]

        val points = listOf(
            Vec2(0.0, 0.0),
            Vec2(0.026139408, 0.024128735),
            Vec2(0.21514745, 0.08646113),
            Vec2(0.30160856, 0.106568396),
            Vec2(0.3961126, 0.106568396),
            Vec2(0.46045578, 0.05428958),
           // Vec2(0.43632707, 0.0),
           /// Vec2(0.43632707, 0.0)
        )

        val bezierCurves = createBezierCurvesThroughPoints(points)

        val f  = bezierCurves.map {  c -> FigureBezier(listOf(c.start, c.control1, c.control2, c.end)) }.toFigure()


        builder.addProduct(f)

    }
}