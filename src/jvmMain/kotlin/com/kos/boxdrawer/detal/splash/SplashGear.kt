package com.kos.boxdrawer.detal.splash

import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_1
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_2
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_NONE
import com.kos.figure.FigureLine
import com.kos.figure.FigurePolyline
import com.kos.figure.IFigure
import com.kos.figure.collections.toFigure
import turtoise.FigureCreator
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
import kotlin.math.tan

class SplashGear : ISplashDetail{
    override val names: List<String>
        get() = listOf("gear")

    override fun help(): HelpData = HelpData(
        "gear cx cy ri ro num h",
        "Построить шестерёнку",
        params = listOf(
            HelpDataParam("cx", "X центра окружности",FIELD_2),
            HelpDataParam("cy", "Y центра окружности", FIELD_NONE),
            HelpDataParam("ri", "Радиус внешней окружности", FIELD_1),
            HelpDataParam("ro", "Радиус внутренней окружности", FIELD_1),
            HelpDataParam("num", "Количество зубьев", FIELD_1),
            HelpDataParam("h", "Высота зуба", FIELD_1),

        ),
        creator = TPArg.create(
            "gear",
            TPArg("cxy", FIELD_2),
            TPArg("ri", FIELD_1),
            TPArg("ro", FIELD_1),
            TPArg("num", FIELD_1),
            TPArg("h", FIELD_1),
        )
    )

    override fun draw(
        builder: TortoiseBuilder,
        com: TortoiseCommand,
        figureExtractor: TortoiseFigureExtractor
    ) {
        val memory = figureExtractor.memory
        val cx = com[1, memory]
        val cy = com[2, memory]
        val ri = com[3, memory]
        val ro = com[4, memory]
        val num = com[5, memory].toInt()
        val h = com[6, memory]
        val center = Vec2(cx, cy)
        builder.addProduct(drawGear(
            center = center,
            outerRadius = ro,
            innerRadius = ri,
            numTeeth = num,
            toothHeight = h
        ))


    }

    fun drawGear(
                 center: Vec2,
                 outerRadius: Double,
                 innerRadius: Double,
                 numTeeth: Int,
                 toothHeight: Double,
                 pressureAngle: Double = 20.0 * PI / 180.0 // Угол давления в радианах (по умолчанию 20 градусов)
    ) :IFigure {
        val result = mutableListOf<IFigure>()
        // Нарисовать внешний круг
        result+=FigureCreator.figureCircle(center, outerRadius)

        // Нарисовать внутренний круг
        result+=FigureCreator.figureCircle(center, innerRadius)

        // Нарисовать зубья
        val angleStep = 2 * PI / numTeeth
        for (i in 0 until numTeeth) {
            val angle = i * angleStep

            // Координаты внешней точки зуба
            val outerPoint = center + Vec2(outerRadius * cos(angle), outerRadius * sin(angle))

            //Координаты точек основания зуба с учетом скоса
            val bevel = toothHeight * tan(pressureAngle)
            val baseAngle1 = angle - angleStep / 4
            val baseAngle2 = angle + angleStep / 4
            val basePoint1 = center + Vec2((outerRadius - toothHeight) * cos(baseAngle1) - bevel * sin(baseAngle1), (outerRadius - toothHeight) * sin(baseAngle1) + bevel * cos(baseAngle1))
            val basePoint2 = center + Vec2((outerRadius - toothHeight) * cos(baseAngle2) + bevel * sin(baseAngle2), (outerRadius - toothHeight) * sin(baseAngle2) - bevel * cos(baseAngle2))

            result.add(FigureLine(outerPoint, basePoint1))
            result.add(FigureLine(basePoint1, basePoint2))
            result.add(FigureLine(basePoint2, outerPoint))
        }
        return result.toFigure()
    }
}