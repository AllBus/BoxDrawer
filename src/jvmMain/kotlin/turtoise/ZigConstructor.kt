package turtoise

import com.kos.figure.FigureLine
import com.kos.figure.FigureList
import com.kos.figure.IFigure
import com.kos.figure.composition.FigureArray
import vectors.Vec2
import kotlin.math.truncate

object ZigConstructor {
    fun zigZag(
        origin: Vec2,
        width: Double,
        zig: ZigzagInfo,
        angle: Double,
        param: DrawingParam,
        zigzagFigure: IFigure,
    ): IFigure {
        val bot = if (param.back) -1 else 1
        val z = 0.0
        val angleV = angle

        /* Зигзаг выключен нарисуем прямую */
        if (!zig.enable){

            return FigureLine(origin, Vec2(width * bot, z).rotate(angleV) + origin)
        }
        var zigzagWidthV = zig.width
        var deltaV = zig.delta

        if (deltaV > width) {
            deltaV = width
        }
        if (zigzagWidthV > deltaV) {
            zigzagWidthV = deltaV
            /* Места недостаточно даже для однго зигзага, нарисуем прямую */
            if (zigzagWidthV < 0.0) {

                return FigureLine(origin, Vec2(width * bot, z).rotate(angleV) + origin)
            }
        }



        val distance = deltaV - zigzagWidthV
        val count = truncate(width / deltaV).toInt()

        var offset: Double = (width - deltaV * count + distance) / 2 * bot

        val zv = zig.height
        val weight = if (param.reverse) -zv else zv


        deltaV *= bot.toDouble()
        val zw = zigzagWidthV * bot
        if (count > 10000) {
            /*  слишком много зигзагов. Нарисуем прямую */
            return FigureLine(origin, Vec2(width * bot, z).rotate(angleV) + origin)
        }

        offset += 0.0
        val points = mutableListOf<IFigure>()
        points+=FigureArray(figure = zigzagFigure,
            startPoint = Vec2(offset, z).rotate(angleV) + origin,
            distance = Vec2(deltaV, 0.0),
            columns = count,
            rows = 1,
            angle = angleV*180/ Math.PI,

            )

        var pred = origin
        for (i in 0 until count) {
            points+=FigureLine(pred, Vec2(offset, z).rotate(angleV) + origin)
            pred = Vec2(offset + zw, z).rotate(angleV) + origin
            offset += deltaV
        }
        points+=FigureLine(pred, Vec2(width * bot, z).rotate(angleV) + origin)
        return FigureList( points.toList())
    }
}