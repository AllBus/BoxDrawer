package com.kos.figure.complex

import com.kos.drawer.IFigureGraphics
import com.kos.figure.Figure
import com.kos.figure.FigureEmpty
import com.kos.figure.FigureLine
import com.kos.figure.IFigure
import com.kos.figure.collections.FigureList
import com.kos.figure.composition.FigureArray
import com.kos.tortoise.ZigzagInfo
import vectors.BoundingRectangle
import vectors.Matrix
import vectors.Vec2
import kotlin.math.floor
import kotlin.math.truncate

class FigureZigZag(
    val origin: Vec2,
    val width: Double,
    val angle: Double,
    val zig: ZigzagInfo,
    val zigFigure:IFigure,
    val reverse: Boolean = false,
    val back: Boolean = false,
    ): IFigure {

    private val isLine:Boolean
    private val end: Vec2

    private val distance: Double
    private val offset: Double
    private val zigCount:Int
    private val zigWidth:Double
    private val zigHeight :Double

    init {
        val bot = if (back) -1 else 1
        end = Vec2(width * bot, 0.0).rotate(angle) + origin
        val zigzagWidthV = zig.width
        var deltaV = zig.delta
        var offsetV = 0.0
        var countV = 0

        /* Зигзаг выключен нарисуем прямую */
        if (!zig.enable) {
            isLine = true
        } else {
            if (deltaV > width) {
                deltaV = width
            }
            if (zigzagWidthV+zig.drop > deltaV) {
                /* Места недостаточно даже для одного зигзага, нарисуем прямую */
                isLine = true
            } else {
                val distance = deltaV - zigzagWidthV
                val countA = truncate(width / deltaV).toInt()

                val count = if (zig.fromCorner && (width - deltaV * countA > zigzagWidthV)){
                    countA+1
                } else {
                    countA
                }

                offsetV = (width - deltaV * count + distance) / 2 * bot

                deltaV *= bot.toDouble()
                if (count > 10000) {
                    /*  слишком много зигзагов. Нарисуем прямую */
                    isLine = true
                } else {

                    isLine = false
                }
                countV = count
            }
        }
        offset = offsetV
        distance = deltaV
        zigCount = countV
        zigWidth = zigzagWidthV * bot

        val zv = zig.height
        zigHeight = if (reverse) -zv else zv
    }

    override val count: Int
        get() = 1

    override fun rect(): BoundingRectangle {
        return BoundingRectangle.apply(listOf(origin, origin+Vec2(width,0.0).rotate(angle)))
    }

    override fun draw(g: IFigureGraphics) {
        if (isLine || g.isSimple()){
            g.drawLine(origin, end)
        }else{
            g.save()
            g.translate(origin.x, origin.y)
            g.rotate(Math.toDegrees(angle), Vec2.Zero)

            var pred = Vec2.Zero
            var off = offset
            for (i in 0 until zigCount) {
                g.drawLine(pred,  Vec2(off, 0.0))
                pred = Vec2(off+ zigWidth, 0.0)

                g.save()
                g.translate(off, 0.0)
                zigFigure.draw(g)
                g.restore()

                off += distance
            }
            val bot = if (back) -1 else 1
            g.drawLine(pred, Vec2(width*bot, 0.0))

            g.restore()
        }
    }

    override fun print(): String {
        return "Z $width ${zig.commandLine()}"
    }

    override fun collection(): List<IFigure> {
        return emptyList()
    }

    override fun name(): String {
        return "Зигзаг"
    }

    override val transform: Matrix
        get() = Matrix.identity
    override val hasTransform: Boolean
        get() = false


}