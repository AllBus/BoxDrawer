package com.kos.boxdrawer.detal.splash

import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_1
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_ANGLE
import com.kos.figure.FigureBezier
import com.kos.figure.FigureEllipse
import turtoise.TortoiseBuilder
import turtoise.TortoiseCommand
import turtoise.TortoiseFigureExtractor
import turtoise.help.HelpData
import turtoise.help.HelpDataParam
import turtoise.parser.TPArg
import vectors.Vec2
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class SplashSpiral : ISplashDetail {
    override val names: List<String>
        get() = listOf("spiral")

    override fun help(): HelpData = HelpData(
        "spiral r rs count step",
        "Рисовать спираль",
        listOf(
            HelpDataParam(
                "r", "Начальный радиус"
            ),
            HelpDataParam(
                "rs", "Растояние между петлями"
            ),
            HelpDataParam(
                "count", "Количество оборотов",FIELD_1
            ),
            HelpDataParam(
                "step", "Длина шага в градусах",FIELD_ANGLE
            ),
        ), creator = TPArg.create("spiral",
            TPArg("r",FIELD_1),
            TPArg("rs",FIELD_1),
            TPArg("count", FIELD_1),
            TPArg("step", FIELD_ANGLE)
        )
    )

    val naprs = listOf(
        Vec2(1.0, 1.0),
        Vec2(1.0, -1.0),
        Vec2(-1.0, -1.0),
        Vec2(-1.0, 1.0),
    )

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        val memory = figureExtractor.memory
        val start = com[1, memory]
        val space = com[2, 1.0,  memory]
        val en = com[3, 1.0, memory]
        val step = com[4, 45.0, memory]

        val points = spiralAtPoint(
            center = builder.xy,
            startRadius = start,
            spacePerLoop = space,
            startTheta = builder.angle,
            endTheta = en*2*Math.PI+builder.angle,
            thetaStep = Math.toRadians(abs(step))
        )
    //    println(points)
        builder.add(
            FigureBezier(

                points
            )
        )

//        if (c > 0) {
//            val arcs = (c * 4).toInt()
//            val radiusUp = r / c
//
//            for (i in 0 until arcs) {
//                val rs = i * radiusUp / 4.0
//                val re = (i + 1) * radiusUp / 4.0
//                val np = naprs[i % 4]
//                FigureEllipse(builder.xy, rs, re, i)
//            }
//        }
    }

    fun intersection(
        m1: Double, b1: Double,
        m2: Double, b2: Double
    ): Vec2? {
        if (m1 == m2) {
            // lines are parallel
            return null
        }
        val x1 = (b2 - b1) / (m1 - m2)
        return Vec2(
            x1,
            m1 * x1 + b1
        )
    }

    fun spiralAtPoint(center: Vec2,
                      startRadius: Double,
                      spacePerLoop: Double,
                      startTheta: Double,
                      endTheta: Double,
                      thetaStep: Double
    ): List<Vec2> {
        val a = startRadius // start distance from center
        val b = spacePerLoop/(2* PI) // space between each loop

        val path = mutableListOf<Vec2>()

        val downTheta = startTheta
        var oldTheta = startTheta
        var newTheta = startTheta

        var newR = a + b * (newTheta-downTheta)

        var newSlope = (b * sin(newTheta) + newR * cos(newTheta)) / (b * cos(newTheta) - newR * sin(newTheta))

        var newPoint = Vec2(center.x + newR * cos(newTheta), center.y + newR * sin(newTheta))

        path.add(newPoint)
        //path.moveTo(newPoint.first, newPoint.second)

        while (oldTheta < endTheta - thetaStep) {
            oldTheta = newTheta
            newTheta += thetaStep

            val oldR = newR
            newR = a + b * (newTheta-downTheta)

            // Slope calculation with the formula:
            // (b * sinΘ + (a + bΘ) * cosΘ) / (b * cosΘ - (a + bΘ) * sinΘ)

            val oldPoint = newPoint
            newPoint = Vec2(center.x + newR * cos(newTheta), center.y + newR * sin(newTheta))

            val aPlusBTheta = a + b * (newTheta-downTheta)
            val oldSlope =  newSlope
            newSlope = (b * sin(newTheta) + aPlusBTheta * cos(newTheta)) / (b * cos(newTheta) - aPlusBTheta * sin(newTheta))

            val oldIntercept = -(oldSlope * oldR * cos(oldTheta) - oldR * sin(oldTheta))
            val newIntercept = -(newSlope * newR * cos(newTheta) - newR * sin(newTheta))

            val controlPoint = center+ (intersection(oldSlope, oldIntercept, newSlope, newIntercept)?: return path)
            val CP1 = oldPoint + (controlPoint-oldPoint)*2.0/3.0
            val CP2 = newPoint + (controlPoint-newPoint)*2.0/3.0

            path+=listOf( CP1,CP2, newPoint)
            //path.quadTo(controlPoint.first, controlPoint.second, newPoint.first, newPoint.second)
        }

        return path
    }
    /*




     */

    fun duga(a:Vec2, b:Vec2): List<Vec2>{
        val ax = a.x
        val ay = a.y
        val bx = b.x
        val by = b.y
        val q1 = ax * ax + ay * ay
        val q2 = q1 + ax * bx + ay * by
        val k2 = (4.0/3.0) * (sqrt(2.0 * q1 * q2) - q2) / (ax * by - ay * bx)

        return listOf(Vec2(ax - k2 * ay,  ay + k2 * ax), Vec2(bx + k2 * by, by - k2 * bx), b )
    }

    fun duga(a:Vec2, b:Vec2, c:Vec2): List<Vec2>{
        val ax = a.x-c.x
        val ay = a.y-c.y
        val bx = b.x - c.x
        val by = b.y - c.y
        val q1 = ax * ax + ay * ay
        val q2 = q1 + ax * bx + ay * by
        val k2 = (4.0/3.0) * (sqrt(2.0 * q1 * q2) - q2) / (ax * by - ay * bx)

        return listOf(Vec2(ax - k2 * ay,  ay + k2 * ax)+c, Vec2(bx + k2 * by, by - k2 * bx)+c, b )
    }
}