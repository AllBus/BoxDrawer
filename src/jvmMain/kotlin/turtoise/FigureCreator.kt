package turtoise

import com.kos.figure.FigureBezier
import com.kos.figure.FigureCircle
import com.kos.figure.FigureEmpty
import com.kos.figure.FigureLine
import com.kos.figure.FigurePolyline
import com.kos.figure.IFigure
import com.kos.figure.algorithms.FigureBezierList
import com.kos.figure.collections.FigureList
import com.kos.figure.collections.toFigure
import vectors.Vec2
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan
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
            center + Vec2(cos(ang), sin(ang)) * radius
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
        val eps = 0.0 //-0.00001
        return FigureBezierList(
            Vec2(v.x - p1.first * smoothSize, v.y - p1.second * smoothSize),
            Vec2(v.x - (eps + p1.first) * smoothSize, v.y - (eps + p1.second) * smoothSize),
            Vec2(v2.x + (eps + p2.first) * smoothSize, v2.y + (eps + p2.second) * smoothSize),
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
//            return FigureRoundRect(
//                left = left,
//                top = top,
//                right = right,
//                bottom = bottom,
//                radius = smoothSize
//            )
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

    fun arcInTwoPoint(p: Vec2, z: Vec2, radius: Double): IFigure {
        val distance2 = Vec2.distance(p, z) / 2
        return if (radius >= distance2) {
            val hyp = sqrt(radius * radius - distance2 * distance2)
            val pza = (z - p).angle
            val h2 = (p + z) / 2.0 + Vec2(0.0, hyp).rotate(pza)

            val b = (z - h2).angle
            val a = (p - h2).angle
            //      println("$r $p $z $h2 $a $b")
            FigureCircle(-h2, radius, true, a , (b - a) )
        } else
            FigureEmpty
    }

    fun roundedLine(points: List<Vec2>, radius: List<Double>): IFigure {
        if (points.size < 2)
            return FigureEmpty

        var predPoint = points[0]
        var lastPoint =  points.last()
        val result = mutableListOf<IFigure>()
//        result += FigureColor(
//            Color.Yellow.toArgb(),
//            DXFColor.getClosestDXFColor(Color.Yellow.toArgb()),
//            FigurePolyline(points)
//
//        )

        val isClose = points.first() == points.last()
        if (isClose){
            val r = radius.getOrNull(points.size-2) ?: 0.0
            val a = points[points.size-2]
            val b = points[0]
            val c = points[1]
            val ci = calculateRadius(a,b,c,r)
            predPoint = ci.bc
            lastPoint = ci.ab
            result += figureCircle(ci, r)
        }

        for (i in 0 until points.size - 2) {
            val a = points[i + 0]
            val b = points[i + 1]
            val c = points[i + 2]
            val r = radius.getOrNull(i) ?: 0.0
            if (r != 0.0) {

                val ci: CornerInfo = calculateRadius(a, b, c, r)

                if (ci.nonZero){
                    result += FigureLine(predPoint, ci.ab)
                    result += figureCircle(ci, r)
                }else{
                    result += FigureLine(predPoint, ci.ab)
                }
                predPoint = ci.bc

            } else {
                result += FigureLine(predPoint, b)
                predPoint = b
            }
        }
        result.add(FigureLine(predPoint, lastPoint))
        return result.toFigure()
    }

    fun figureCircle(
        info: CornerInfo,
        r: Double,
    ) :FigureCircle {
        val startAngle = -atan2((info.ab.y - info.o.y), (info.ab.x - info.o.x))
        val endAngle = -atan2((info.bc.y - info.o.y), (info.bc.x - info.o.x))

        var sweep = endAngle - startAngle

        var re = 1
        if (sweep<0.0){
            re *= -1
            sweep = -sweep
        }

        if (sweep > Math.PI){
            sweep = (sweep - Math.PI * 2)
        //    re *= -1

        }

        val sa = if (re<0) endAngle else startAngle
        val sr = sweep


       // println("> ${Math.toDegrees(startAngle)} ${Math.toDegrees(endAngle)} ${Math.toDegrees(sweep)}")

        return FigureCircle(
            center = info.o,
            radius = r,
            outSide = true,
            segmentStartAngle = sa,
            segmentSweepAngle =  sr
        )
    }

    private fun calculateRadius(
        a: Vec2,
        b: Vec2,
        c: Vec2,
        r: Double
    ): CornerInfo {
        val oa = Vec2.angle(a, b, c)
        val segment = r / abs(tan(oa / 2))
        val pp1 = Vec2.distance(a, b)
        val pp2 = Vec2.distance(b, c)
        val po = sqrt(r * r + segment * segment)

        val ab = b - (b - a) * segment / pp1
        val bc = b - (b - c) * segment / pp2

        val co = ab + bc - b
        val pc = Vec2.distance(b, co)

        val ci: CornerInfo = if (pc != 0.0) {
            val d = b - co
            val o = b - d * po / pc

            CornerInfo(ab, bc, o, true)
        } else {
            CornerInfo(b, b, b, false)
        }
        return ci
    }
}

class CornerInfo(val ab:Vec2,val bc:Vec2, val o:Vec2,val nonZero:Boolean)