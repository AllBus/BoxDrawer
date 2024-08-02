package com.kos.figure.complex

import com.kos.drawer.IFigureGraphics
import com.kos.figure.CropSide
import com.kos.figure.Figure
import com.kos.figure.FigureEmpty
import com.kos.figure.IFigure
import com.kos.figure.IFigurePath
import com.kos.figure.PointWithNormal
import com.kos.figure.collections.FigureList
import com.kos.figure.collections.FigurePath
import com.kos.figure.composition.FigureRotate
import vectors.BoundingRectangle
import vectors.Matrix
import vectors.Vec2
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.min

class FigureRoundRect(
    val left: Double,
    val top: Double,
    val right: Double,
    val bottom: Double,
    val radius: Double,
): Figure(), IFigurePath {

    val width = right - left
    val height = top - bottom

    val edgeCount = 4

    val edges : List<Edge> = listOf(
        Edge(
            Vec2(left+radius, top),
            Vec2(right-radius, top),
        ),

        Edge(
            Vec2(right, top+radius),
            Vec2(right, bottom-radius),
        ),
        Edge(
            Vec2(right-radius, bottom),
            Vec2(left+radius, bottom),
        ),
        Edge(
            Vec2(left, bottom-radius),
            Vec2(left, top+radius),
        ),
    )

    val corners: List<Corner> = listOf(
        Corner(
            Vec2(left+radius, top+radius),
            radius,
            Math.toRadians(180.0),
            PI/2
        ),
        Corner(
            Vec2(right-radius, top+radius),
            radius,
            Math.toRadians(270.0),
            PI/2
        ),
        Corner(
            Vec2(right-radius, bottom-radius),
            radius,
            Math.toRadians(00.0),
            PI/2
        ),
        Corner(
            Vec2(left+radius, bottom-radius),
            radius,
            Math.toRadians(90.0),
            PI/2
        ),
    )

    val segments = edges.zip(corners).flatMap { listOf(it.first, it.second) }

    override val count: Int
        get() = 1

    override fun list(): List<Figure> = emptyList()

    private val fullLength by lazy { segments.sumOf { it.perimeter() } }

    override fun positionInPath(delta: Double): PointWithNormal {
        //val le = delta*pathLength()
        var ostatok = delta*pathLength()
        if (ostatok<0)
            return PointWithNormal.EMPTY
        for( s in segments){
            val q = s.perimeter()
            if (q>0) {
                if (ostatok <= q) {
                    return s.positionInPath(ostatok / q)
                } else {
                    ostatok -= q
                }
            }
        }
        return PointWithNormal.EMPTY
    }

    override fun positionInPath(edge: Int, delta: Double): PointWithNormal {
        if (edge>=0 && edge< segments.size){
            val s= segments[edge]
            return s.positionInPath(delta)
        }
        return PointWithNormal.EMPTY
    }

    override fun pathLength(): Double {
        return fullLength
    }

    override fun pathLength(edge: Int): Double {
        if (edge>=0 && edge< segments.size){
            return segments[edge].perimeter()
        }
        return 0.0
    }

    override fun edgeCount(): Int {
        return segments.size
    }

    override fun path(edge: Int): IFigurePath {
        if (edge>=0 && edge< segments.size){
            return segments[edge].toPath()
        }
        return FigureEmpty
    }

    override fun startPoint(): Vec2 {
        return edges[0].a
    }

    override fun endPoint(): Vec2 {
        return edges[0].a
    }

    override fun take(startMM: Double, endMM: Double): Figure {
        val le = pathLength()
        if (le<=0)
            return FigureEmpty

        val st = startMM.coerceIn(0.0,le)
        val end =  endMM.coerceIn(0.0,le)

        val result = mutableListOf<Figure>()
        var state= true
        var ostatok = st
        for(s in segments){
            val q = s.perimeter()
            if (q>0) {
                if (state) {
                    if (ostatok <= q) {
                        result.add(s.take(ostatok, q))
                        ostatok+=end-st
                        state = false
                    }
                } else{
                    if (ostatok >= q) {
                        result.add(s.toFigure())
                    } else{
                        result.add(s.take(0.0, ostatok))
                        break
                    }
                }
                ostatok -= q
            }
        }

        return FigurePath(result.toList())
    }

    override fun duplicationAtNormal(h: Double): Figure {
        return FigureRoundRect(
            left = left-h,
            top = top-h,
            right = right+h,
            bottom = bottom+h,
            radius = radius+h,
        )
    }

    override fun rect(): BoundingRectangle {
        return BoundingRectangle(Vec2(left, bottom),Vec2(right, top))
    }

    override fun toFigure(): Figure {
        return this
    }

    override fun transform(matrix: Matrix): FigurePath {
        return FigurePath(
            segments.map { it.toFigure() }.map { it.transform(matrix) }
        )
    }

    override fun crop(k: Double, cropSide: CropSide): Figure {
        return FigurePath(  segments.map { it.toFigure() }.map { it.crop(k, cropSide) })
    }


    override fun draw(g: IFigureGraphics) {

        for (i in 0 until edgeCount){
            val e = edge(i)
            val c = corner(i)
            g.drawLine(e.a, e.b )
            g.drawArc(c.center, c.radius, c.radius, c.startAngle, c.sweepAngle)
        }
    }

    fun edge(i:Int):Edge = edges[i]
    fun corner(i:Int):Corner = corners[i]

    override fun print(): String {
        return "r $width $height $radius"
    }

    override fun collection(): List<IFigure> = emptyList()

    override fun name(): String {
        return "RoundRect"
    }

    override val transform: Matrix
        get() = Matrix.identity
    override val hasTransform: Boolean
        get() = false

    override fun translate(translateX: Double, translateY: Double): Figure {
        return FigureRoundRect(
            left = left + translateX,
            top = top +translateY,
            right = right+translateX,
            bottom = bottom+translateY,
            radius = radius,

        )
    }
}
