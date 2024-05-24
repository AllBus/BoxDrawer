package turtoise

import com.kos.figure.FigureBezier
import com.kos.figure.FigureBezierList
import com.kos.figure.FigureLine
import com.kos.figure.FigureList
import com.kos.figure.FigurePolyline
import com.kos.figure.IFigure
import vectors.Vec2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.truncate

object FigureCreator {
    fun regularPolygon(
        center: Vec2,
        count: Int,
        angle: Double,
        radius: Double,
    ): IFigure {
        val p = (1..count).map { ind ->
            val ang = angle + 2 * Math.PI * ind / count
            center + Vec2(sin(ang), cos(ang)) * radius
        }
        return FigurePolyline(p, true)
    }

    fun zigzag(
        points: MutableList<Vec2>,
        origin: Vec2,
        width: Double,
        zig: ZigzagInfo,
        angle: Double,
        param: DrawingParam,
        boardWeight: Double
    ) {
        val bot = if (param.back) -1 else 1
        val z = 0.0
        val angleV = angle

        if (!zig.enable) {
            points.add(Vec2(width * bot, z).rotate(angleV) + origin)
            return
        }
        var zigzagWidthV = zig.width
        var deltaV = zig.delta

        if (deltaV > width) {
            deltaV = width
        }
        if (zigzagWidthV > deltaV) {
            zigzagWidthV = deltaV - boardWeight * 2
            if (zigzagWidthV < boardWeight) {
                points.add(Vec2(width * bot, z).rotate(angleV) + origin)
                return
            }
        }


        val distance = deltaV - zigzagWidthV
        val count = truncate(width / deltaV).toInt()

        var offset: Double = (width - deltaV * count + distance) / 2 * bot

        val zv = if (zig.height == 0.0) boardWeight else zig.height
        val weight = if (param.reverse) -zv else zv


        deltaV *= bot.toDouble()
        val zw = zigzagWidthV * bot
        if (count > 10000) {
            points.add(Vec2(width * bot, z).rotate(angleV) + origin)
            return
        }

        offset += 0.0

        for (i in 0 until count) {
            points.add(Vec2(offset, z).rotate(angleV) + origin)
            points.add(Vec2(offset, z + weight).rotate(angleV) + origin)
            points.add(Vec2(offset + zw, z + weight).rotate(angleV) + origin)
            points.add(Vec2(offset + zw, z).rotate(angleV) + origin)
            offset += deltaV
        }
        points.add(Vec2(width * bot, z).rotate(angleV) + origin)
    }

    fun holes(
        origin: Vec2,
        width: Double,
        zig: ZigzagInfo,
        angle: Double,
        param: DrawingParam,
        boardWeight: Double
    ): List<IFigure> {
        if (!zig.enable) {
            return emptyList()
        }

        var zigzagWidthV = zig.width
        var deltaV = zig.delta

        if (deltaV > width) {
            deltaV = width
        }
        if (zigzagWidthV > deltaV) {
            zigzagWidthV = deltaV - boardWeight * 2
            if (zigzagWidthV < boardWeight) return emptyList()
        }

        val bot = if (param.back) -1 else 1
        val distance = deltaV - zigzagWidthV
        val count = truncate(width / deltaV).toInt()

        var offset: Double = (width - deltaV * count + distance) / 2 * bot

        val weight = if (param.reverse) -boardWeight else boardWeight

        deltaV *= bot.toDouble()
        val zw = zigzagWidthV * bot
        if (count > 10000) return emptyList()

        offset += 0.0
        val z = 0.0

        return (0 until count).map { i ->
            val v = FigurePolyline(
                listOf(
                    Vec2(offset, z).rotate(angle) + origin,
                    Vec2(offset, z + weight).rotate(angle) + origin,
                    Vec2(offset + zw, z + weight).rotate(angle) + origin,
                    Vec2(offset + zw, z).rotate(angle) + origin,
                    Vec2(offset, z).rotate(angle) + origin,
                )
            )
            offset += deltaV
            v
        }
    }


    val next: List<Pair<Int, Int>> = listOf(
        (-1 to 0),
        (0 to -1),
        (1 to 0),
        (0 to 1),
    )

    private val tan = 0.552284749831
    fun bezierQuartir(v: Vec2, smoothSize: Double, g1: Int, g2: Int): FigureBezierList {
        val p1 = next[g1 % 4]
        val p2 = next[g2 % 4]
        return FigureBezierList(
            Vec2(v.x - p1.first * smoothSize, v.y - p1.second * smoothSize),
            Vec2(
                v.x - p1.first * smoothSize * (1 - tan),
                v.y - p1.second * smoothSize * (1 - tan)
            ),
            Vec2(
                v.x + p2.first * smoothSize * (1 - tan),
                v.y + p2.second * smoothSize * (1 - tan)
            ),
            Vec2(v.x + p2.first * smoothSize, v.y + p2.second * smoothSize)
        )
    }

    fun bezierLine(v: Vec2, v2: Vec2, smoothSize: Double, g1: Int, g2: Int): FigureBezierList {
        val p1 = next[g1 % 4]
        val p2 = next[g2 % 4]
        return FigureBezierList(
            Vec2(v.x - p1.first * smoothSize, v.y - p1.second * smoothSize),
            Vec2(v.x - p1.first * smoothSize, v.y - p1.second * smoothSize),
            Vec2(v2.x + p2.first * smoothSize, v2.y + p2.second * smoothSize),
            Vec2(v2.x + p2.first * smoothSize, v2.y + p2.second * smoothSize)
        )
    }

    fun bezierLine(
        v: Vec2,
        v2: Vec2,
        smoothSizeStart: Double,
        smoothSizeEnd: Double,
        g1: Int,
        g2: Int
    ): FigureBezierList {
        val p1 = next[g1 % 4]
        val p2 = next[g2 % 4]
        return FigureBezierList(
            Vec2(v.x - p1.first * smoothSizeStart, v.y - p1.second * smoothSizeStart),
            Vec2(v.x - p1.first * smoothSizeStart, v.y - p1.second * smoothSizeStart),
            Vec2(v2.x + p2.first * smoothSizeEnd, v2.y + p2.second * smoothSizeEnd),
            Vec2(v2.x + p2.first * smoothSizeEnd, v2.y + p2.second * smoothSizeEnd)
        )
    }

    fun rectangle(
        left: Double,
        top: Double,
        right: Double,
        bottom: Double,
        enableSmooth: Boolean,
        smoothSize: Double,
    ): IFigure {
        if (enableSmooth) {
            val lt = Vec2(left, top);
            val rt = Vec2(right, top);
            val lb = Vec2(left, bottom);
            val rb = Vec2(right, bottom);

            val bz = FigureBezierList.simple(
                listOf(
                    bezierQuartir(lt, smoothSize, 1, 2),
                    bezierLine(lt, rt, smoothSize, 0, 0),
                    bezierQuartir(rt, smoothSize, 2, 3),
                    bezierLine(rt, rb, smoothSize, 1, 1),
                    bezierQuartir(rb, smoothSize, 3, 0),
                    bezierLine(rb, lb, smoothSize, 2, 2),
                    bezierQuartir(lb, smoothSize, 0, 1),
                    bezierLine(lb, lt, smoothSize, 3, 3),
                )
            )

            return bz.toFigure()
        } else {
            val bz = FigurePolyline(
                listOf(
                    Vec2(left, top),
                    Vec2(right, top),
                    Vec2(right, bottom),
                    Vec2(left, bottom),
                ),
                true
            )
            return bz
        }
    }

    fun zigFigure(
        hz: Double,
        bz1x: Double,
        bz2x: Double,
        bz1y: Double,
        bz2y: Double,
        board: Double,
        zigWidth: Double,
    ): IFigure {
        return FigureList(
            listOf(
                FigureLine(
                    Vec2(0.0, 0.0),
                    Vec2(0.0, hz)
                ),
                FigureBezier(
                    listOf(
                        Vec2(0.0, hz),
                        Vec2(-bz1x, hz + bz1y),
                        Vec2(-bz2x, board + bz2y),
                        Vec2(0.0, board),
                    )
                ),
                FigureLine(
                    Vec2(0.0, board),
                    Vec2(zigWidth, board),

                    ),
                FigureBezier(
                    listOf(
                        Vec2(zigWidth, hz),
                        Vec2(zigWidth + bz1x, hz + bz1y),
                        Vec2(zigWidth + bz2x, board + bz2y),
                        Vec2(zigWidth, board),
                    )
                ),
                FigureLine(
                    Vec2(zigWidth, hz),
                    Vec2(zigWidth, 0.0)
                )
            )
        )
    }

    fun rectangle(center: Vec2, width: Double, height: Double): IFigure {
        val c2 = center
        val angle = 0.0
        val width2 = width / 2.0
        val height2 = height / 2.0
        val points = listOf<Vec2>(
            c2 + Vec2(-width2, -height2).rotate(angle),
            c2 + Vec2(-width2, height2).rotate(angle),
            c2 + Vec2(width2, height2).rotate(angle),
            c2 + Vec2(width2, -height2).rotate(angle),
            c2 + Vec2(-width2, -height2).rotate(angle),
        )

        return FigurePolyline(points)
    }
}