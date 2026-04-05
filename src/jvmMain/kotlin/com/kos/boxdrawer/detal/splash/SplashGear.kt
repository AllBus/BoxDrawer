package com.kos.boxdrawer.detal.splash

import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_1
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_2
import com.kos.boxdrawer.template.editor.TemplateField.Companion.FIELD_NONE
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
        com[6, memory]
        val center = Vec2(cx, cy)
        builder.addProduct(drawGearGost(
            center = center,
            module = ro,
            x = ri,
            z = num,
        ))


    }

    /**
     * Рисование шестерёнки с зубьями в форме солнца
     * @param center - центр шестерёнки
     * @param outerRadius - радиус внешней окружности
     * @param innerRadius - радиус внутренней окружности
     * @param numTeeth - количество зубьев
     * @param toothHeight - высота зуба
     * @param pressureAngle - угол давления (по умолчанию 20°)
     */
    fun drawGearSun(
        center: Vec2,
        outerRadius: Double,
        innerRadius: Double,
        numTeeth: Int,
        toothHeight: Double,
        pressureAngle: Double = 20.0 * PI / 180.0 // Угол давления в радианах (по умолчанию 20 градусов)
    ) :IFigure {
        val result = mutableListOf<IFigure>()
        // Нарисовать внешний круг
        result += FigureCreator.figureCircle(center, outerRadius)
        // Нарисовать внутренний круг
        result += FigureCreator.figureCircle(center, innerRadius)

        // Нарисовать зубья
        val baseRadius = outerRadius - toothHeight
        val angleStep = 2 * PI / numTeeth

        val points = mutableListOf<Vec2>()
        for (i in 0 until numTeeth) {
            val angle = i * angleStep
            val nextAngle = (i + 1) * angleStep

            // Точки основания зуба на базовой окружности
            val p1 = center + Vec2(baseRadius * cos(angle), baseRadius * sin(angle))
            val p2 = center + Vec2(baseRadius * cos(nextAngle), baseRadius * sin(nextAngle))
            // Точки вершины зуба на внешней окружности
            val p3 = center + Vec2(
                outerRadius * cos(nextAngle - angleStep / 2),
                outerRadius * sin(nextAngle - angleStep / 2)
            )
            val p4 = center + Vec2(
                outerRadius * cos(angle + angleStep / 2),
                outerRadius * sin(angle + angleStep / 2)
            )

            points.addAll(
                listOf(p1, p3, p4, p2)
            )
        }

        result += FigurePolyline(
            points = points.toList()
        )

        return result.toFigure()
    }

    /**
     * Рисование шестерёнки по ГОСТ 13755-81 (эвольвентный профиль)
     * @param center - центр шестерёнки
     * @param module - модуль зуба
     * @param z - число зубьев
     * @param x - коэффициент смещения (обычно 0)
     * @param pressureAngle - угол давления (обычно 20°)
     */
    fun drawGearGost(
        center: Vec2,
        module: Double,
        z: Int,
        x: Double = 0.0,
        pressureAngle: Double = 20.0 * PI / 180.0
    ): IFigure {
        /*
        Для простого ГОСТ-эвольвентного зуба:
        Делительная окружность: r = m * z / 2
        Окружность вершины зуба: r_a = r + m
        Окружность впадины: r_f = r - 1.25 * m
        Эвольвентный профиль строится по формуле эвольвенты.
         */
        val result = mutableListOf<IFigure>()
        val r = module * z / 2.0 // радиус делительной окружности
        val r_a = r + module // радиус окружности вершины зуба
        val r_f = r - 1.25 * module // радиус окружности впадины
        val baseRadius = r * cos(pressureAngle) // радиус основной окружности
        PI * module / 2.0 + 2.0 * x * module * tan(pressureAngle)
        val angleStep = 2 * PI / z

        // Нарисовать делительную, вершину и впадину окружности
        result += FigureCreator.figureCircle(center, r)
        result += FigureCreator.figureCircle(center, r_a)
        result += FigureCreator.figureCircle(center, r_f)

        // Строим профиль одного зуба
        fun involutePoint(baseR: Double, t: Double): Vec2 {
            // t — параметр эвольвенты (угол)
            val x = baseR * (cos(t) + t * sin(t))
            val y = baseR * (sin(t) - t * cos(t))
            return Vec2(x, y)
        }

        val toothProfile = mutableListOf<Vec2>()
        // Левая сторона зуба (эвольвента от основной до вершины)
//        for (i in 0..steps) {
//            val t = i.toDouble() / steps * (acos(baseRadius / r_a) - acos(baseRadius / r))
//            val p = involutePoint(baseRadius, t)
//            val len = p.length()
//            val scale = r_a / len
//            toothProfile += p * scale
//        }
//        // Правая сторона зуба (симметрично)
//        for (i in steps downTo 0) {
//            val t = i.toDouble() / steps * (acos(baseRadius / r_a) - acos(baseRadius / r))
//            val p = involutePoint(baseRadius, -t)
//            val len = p.length()
//            val scale = r_a / len
//            toothProfile += p * scale
//        }
        toothProfile.addAll(
        buildInvolute(baseRadius, baseRadius, r_a,)
        )
        // Соединяем впадины
        toothProfile += Vec2(r_f, 0.0)

        // Копируем профиль по окружности
        for (i in 0 until z) {
            val angle = i * angleStep
            val rotated = toothProfile.map { pt -> center + pt.rotate(angle) }
            result += FigurePolyline(rotated, true)
        }
        return result.toFigure()
    }

    /**
     * Построение эвольвенты для шестерёнки
     * @param baseRadius радиус основной окружности
     * @param rStart радиус начала эвольвенты (обычно baseRadius)
     * @param rEnd радиус конца эвольвенты (например, r_a — радиус вершины зуба)
     * @param steps количество точек
     * @return список точек эвольвенты (Vec2)
     */
    fun buildInvolute(baseRadius: Double, rStart: Double, rEnd: Double, steps: Int = 20): List<Vec2> {
        val result = mutableListOf<Vec2>()
        val tStart = 0.0
        val tEnd = Math.sqrt((rEnd * rEnd - baseRadius * baseRadius) / (baseRadius * baseRadius))
        for (i in 0..steps) {
            val t = tStart + (tEnd - tStart) * i / steps
            val x = baseRadius * (Math.cos(t) + t * Math.sin(t))
            val y = baseRadius * (Math.sin(t) - t * Math.cos(t))
            val r = Math.sqrt(x * x + y * y)
            val scale = rEnd / r
            result += Vec2(x * scale, y * scale)
        }
        return result
    }

}