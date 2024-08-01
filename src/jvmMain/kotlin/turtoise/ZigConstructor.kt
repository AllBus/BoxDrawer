package turtoise

import com.kos.figure.FigureEmpty
import com.kos.figure.FigureLine
import com.kos.figure.IFigure
import com.kos.figure.collections.FigureList
import com.kos.figure.composition.FigureArray
import com.kos.figure.composition.FigureRotate
import vectors.Vec2
import kotlin.math.truncate

object ZigConstructor {

    fun holes(
        origin: Vec2,
        width: Double,
        zig: ZigzagInfo,
        angle: Double,
        param: DrawingParam,
        zigzagFigure: IFigure,
        lineInfo: LineInfo,
    ): IFigure{
        val bot = if (param.back) -1 else 1
        val z = 0.0
        val angleV = angle

        /* Зигзаг выключен нарисуем прямую */
        if (!zig.enable ||
            (width<= lineInfo.startOffset+lineInfo.endOffset)
        ) {
            return FigureEmpty
        }

        val resultList = mutableListOf<IFigure>()
        val spoint = if (lineInfo.startOffset != 0.0) {
            val st = Vec2(lineInfo.startOffset * bot, z).rotate(angleV)+ origin
            st
        } else
            origin

        val zi = holes(
            origin = spoint,
            width = width - lineInfo.startOffset - lineInfo.endOffset,
            zig = zig,
            angle = angle,
            param = param,
            zigzagFigure = zigzagFigure
        )

        resultList += zi

        return FigureList(resultList.toList())
    }

    fun holes(
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
        if (!zig.enable) {
            return FigureEmpty
        }
        val zigzagWidthV = zig.width
        var deltaV = zig.delta

        if (deltaV > width) {
            deltaV = width
        }
        if (zigzagWidthV+zig.drop > deltaV) {
            /* Места недостаточно даже для одного зигзага */
            return FigureEmpty
        }

        val distance = deltaV - zigzagWidthV
        val countA = truncate(width / deltaV).toInt()

        val count = if (zig.fromCorner && (width - deltaV * countA > zigzagWidthV)){
            countA+1
        } else {
            countA
        }

        var offset: Double = (width - deltaV * count + distance) / 2 * bot
        deltaV *= bot.toDouble()
        if (count > 10000) {
            /*  слишком много зигзагов. */
            return FigureEmpty
        }

        offset += 0.0
        val points = mutableListOf<IFigure>()
        points+=FigureArray(
            figure = zigzagFigure,
            startPoint = Vec2(offset, z).rotate(angleV) + origin,
            distance = Vec2(deltaV, 0.0),
            columns = count,
            rows = 1,
            angle = angleV * 180 / Math.PI,

            )

        return FigureList(points.toList())
    }




    fun zigZag(
        origin: Vec2,
        width: Double,
        zig: ZigzagInfo,
        angle: Double,
        param: DrawingParam,
        zigzagFigure: IFigure,
        lineInfo: LineInfo,
    ): IFigure {
        val bot = if (param.back) -1 else 1
        val z = 0.0
        val angleV = angle

        /* Зигзаг выключен нарисуем прямую */
        if (!zig.enable ||
            (width<= lineInfo.startOffset+lineInfo.endOffset)
            ) {
            return FigureLine(origin, Vec2(width * bot, z).rotate(angleV) + origin)
        }


        val resultList = mutableListOf<IFigure>()
        val spoint = if (lineInfo.startOffset != 0.0) {
            val st = Vec2(lineInfo.startOffset * bot, z).rotate(angleV)+ origin
            resultList += FigureLine(origin, st )
            st
        } else
            origin

        val zi = zigZag(
            origin = spoint,
            width = width - lineInfo.startOffset - lineInfo.endOffset,
            zig = zig,
            angle = angle,
            param = param,
            zigzagFigure = zigzagFigure
        )

        resultList += zi

        if (lineInfo.endOffset != 0.0) {
            resultList += FigureLine(
                origin + Vec2((width - lineInfo.endOffset) * bot, z).rotate(
                    angleV
                ), origin + Vec2(width * bot, z).rotate(angleV)
            )
        }
        return FigureList(resultList.toList())
    }

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
        if (!zig.enable) {

            return FigureLine(origin, Vec2(width * bot, z).rotate(angleV) + origin)
        }
        val zigzagWidthV = zig.width
        var deltaV = zig.delta

        if (deltaV > width) {
            deltaV = width
        }
        if (zigzagWidthV+zig.drop > deltaV) {
           // zigzagWidthV = deltaV
            /* Места недостаточно даже для одного зигзага, нарисуем прямую */
           // if (zigzagWidthV < 0.0) {

                return FigureLine(origin, Vec2(width * bot, z).rotate(angleV) + origin)
           // }
        }


        val distance = deltaV - zigzagWidthV
        val countA = truncate(width / deltaV).toInt()

        val count = if (zig.fromCorner && (width - deltaV * countA > zigzagWidthV)){
                countA+1
        } else {
            countA
        }

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
        if (zigzagFigure != FigureEmpty) {
            points += FigureArray(
                figure = zigzagFigure,
                startPoint = Vec2(offset, z).rotate(angleV) + origin,
                distance = Vec2(deltaV, 0.0),
                columns = count,
                rows = 1,
                angle = angleV * 180 / Math.PI,
                )
        }

        var pred = origin
        for (i in 0 until count) {
            points += FigureLine(pred, Vec2(offset, z).rotate(angleV) + origin)
            pred = Vec2(offset + zw, z).rotate(angleV) + origin
            offset += deltaV
        }
        points += FigureLine(pred, Vec2(width * bot, z).rotate(angleV) + origin)
        return FigureList(points.toList())
    }
}