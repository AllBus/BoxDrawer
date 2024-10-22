package turtoise.paint

import com.kos.figure.Figure
import com.kos.figure.FigureBezier
import com.kos.figure.FigureCircle
import com.kos.figure.FigureEllipse
import com.kos.figure.FigureInfo
import com.kos.figure.FigureLine
import com.kos.figure.FigurePolyline
import com.kos.figure.IFigure
import com.kos.figure.algorithms.BezierManipulation
import com.kos.figure.algorithms.findClosestPointOnEllipse
import com.kos.figure.algorithms.findPathIntersections
import com.kos.figure.collections.SimpleFigure
import com.kos.figure.complex.model.Ellipse
import com.kos.figure.complex.model.PathIterator
import com.kos.figure.complex.model.SimpleElement
import com.kos.figure.composition.FigureRotate
import com.kos.figure.composition.FigureTranslate
import com.kos.figure.composition.FigureTranslateWithRotate
import nl.adaptivity.xmlutil.core.impl.multiplatform.name
import turtoise.TortoiseState
import vectors.BoundingRectangle
import vectors.Matrix
import vectors.Vec2
import kotlin.math.max
import kotlin.math.min

object PaintUtils {

    fun findAllIntersects(figures: List<IFigure>): List<Vec2> {
        val res = mutableListOf<PathIterator>()
        val transform = Matrix.identity
        findAllIntersects(figures, transform, res)
        println(res.map { "${it::class.name}-${it.size}" }.joinToString(", "))
        val points = findPathIntersections(res)
        println("points ${points.size}")
        return points
    }

    private fun findAllIntersects(
        figures: List<IFigure>,
        transform: Matrix,
        result: MutableList<PathIterator>
    ) {
        figures.forEach { f ->
            if (f.hasTransform) {
                val mt = transform.copyWithTransform(f.transform)

                //: todo нужно вычислять с матрицей трасформации
                if (f is SimpleElement) {
                    result.add(f.segments())
                }

                findAllIntersects(f.collection(), mt, result)
            } else {
                if (f is SimpleElement) {
                    result.add(f.segments())
                }
                findAllIntersects(f.collection(), transform, result)
            }
        }
    }

    fun findPointAtCursor(
        transform: Matrix,
        position: Vec2,
        eps: Double,
        figures: List<IFigure>
    ): Vec2? {
        var current : Vec2? = null
        var currentDistance = Double.MAX_VALUE
        figures.forEach { f ->
            val mt = transform.copyWithTransform(f.transform)
            val ptf = f.transform.getInvert()
            val posp = ptf * position

            //if ( check(posp, eps, f) ) {
            val r = blizPoint(posp, eps, f)
            if (r != null) {
                val nd = Vec2.distance(position, r)
                if (nd< currentDistance){
                    currentDistance = nd
                    current = r
                }
            }
            //   }else{
            val p = findPointAtCursor(mt, posp, eps, f.collection())
            if (p != null) {
                val nd = Vec2.distance(position, p)
                if (nd< currentDistance){
                    currentDistance = nd
                    current = p
                }
            }
        }

        return current
    }

    fun findFiguresAtCursor(
        transform: Matrix,
        position: Vec2,
        eps: Double,
        figures: List<IFigure>
    ): List<FigureInfo> {
        return figures.flatMap { f ->
            if (f.hasTransform) {
                val mt = transform.copyWithTransform(f.transform)
                val ptf = f.transform.getInvert()
                val posp = ptf * position

                listOfNotNull(
                    f.takeIf { check(posp, eps, f) }?.let {
                        FigureInfo(it, null, transform)
                    }
                ) + findFiguresAtCursor(mt, posp, eps, f.collection())

            } else {
                listOfNotNull(
                    f.takeIf { check(position, eps, f) }?.let {
                        FigureInfo(it, null, transform)
                    }
                ) + findFiguresAtCursor(transform, position, eps, f.collection())
            }
        }
    }

    private fun blizPoint(position: Vec2, eps: Double, figure: IFigure): Vec2? {
        // println("blizPoint $position $eps ${figure::class.name}")
        return when (figure) {
            is Figure -> {
                val b: Vec2? = if (inRect(position, eps, figure.rect())) {
                    when (figure) {
                        is FigureCircle -> {
                            val dist = Vec2.distance(
                                figure.center,
                                position
                            )

                            if (dist < eps) figure.center else {
                                if (dist < figure.radius + eps && dist > figure.radius - eps) {
                                    val angle = Vec2.angle(figure.center, position)
                                    figure.center + Vec2(figure.radius, 0.0).rotate(angle)
                                } else null
                            }
                        }

                        is FigureEllipse -> {
                            val dist = Vec2.distance(
                                figure.center,
                                position
                            )

                            if (dist < eps) figure.center else {
                                if (dist < max(
                                        figure.radius,
                                        figure.radiusMinor
                                    ) + eps && dist > min(figure.radius, figure.radiusMinor) - eps
                                )
                                {
                                    findClosestPointOnEllipse(
                                        figure.segments().element,
                                        position
                                    )
                                } else null
                            }
                        }

                        is FigurePolyline -> {
                            //   println(figure.points.joinToString(" "))
                            val minValue = figure.points.minBy { Vec2.distance(it, position) }
                            if (Vec2.distance(minValue, position) < eps) minValue else {
                                val dot =
                                    PolygonUtils.findClosestPointOnPolygon(figure.points, position)
                                if (Vec2.distance(dot, position) < eps) dot else null
                            }
                        }

                        is FigureBezier -> {
                            val minValue = figure.points.minBy { Vec2.distance(it, position) }
                            if (Vec2.distance(minValue, position) < eps) minValue else {
                                val dot = BezierManipulation.findClosestPointOnPolygon(
                                    figure.points,
                                    position
                                )
                                if (Vec2.distance(dot, position) < eps) dot else null
                            }
                        }

                        else -> null
                    }
                } else
                    null
                b
            }

            else -> null
        }
    }

    private fun check(position: Vec2, eps: Double, figure: IFigure): Boolean {
        return when (figure) {
            is Figure -> {
                val b: Boolean = if (inRect(position, eps, figure.rect())) {
                    when (figure) {
                        is FigureCircle ->
                            Vec2.distance(figure.center, position) < eps + figure.radius

                        is FigureLine -> {
                            val p = PolygonUtils.findClosestPointOnPolygon(figure.points, position)
                            if (Vec2.distance(p, position) < 2 * eps) true else false
                        }

                        is FigurePolyline ->
                            if (figure.isClose()) {
                                inside(position, figure.points)
                            } else
                                true

                        else -> true
                    }
                } else
                    false
                b
            }

            else -> false
        }
    }

    fun inRect(position: Vec2, eps: Double, rect: BoundingRectangle): Boolean {
        return position.x + eps >= rect.min.x && position.x - eps <= rect.max.x &&
                position.y + eps >= rect.min.y && position.y - eps <= rect.max.y
    }

    fun inside(point: Vec2, vs: List<Vec2>): Boolean {
        // ray-casting algorithm based on
        // https://wrf.ecse.rpi.edu/Research/Short_Notes/pnpoly.html

        val x = point.x
        val y = point.y

        var inside = false;
        for (i in 1 until vs.size) {
            val j = i - 1
            val xi = vs[i].x
            val yi = vs[i].y;
            val xj = vs[j].x
            val yj = vs[j].y;

            val intersect = ((yi > y) != (yj > y))
                    && (x < (xj - xi) * (y - yi) / (yj - yi) + xi);
            if (intersect) inside = !inside;
        }
        return inside
    }

    fun onlyFigures(figure: IFigure): SimpleFigure {
        val state = TortoiseState()
        return SimpleFigure(onlyFigures(figure, state))
    }

    fun onlyFigures(figure: IFigure, state: TortoiseState): List<Figure> {
        val res = when (figure) {
            is Figure -> return listOf(figure /*.rotate(state.angle) */.translate(state.x, state.y))
            else -> {
                val newState = if (figure.hasTransform) {
                    val nm = TortoiseState().from(state)
                    when (figure) {
                        is FigureTranslate -> {
                            nm.move(figure.offset)
                            nm
                        }

                        is FigureRotate -> {
                            nm.move(figure.pivot)
                            nm.angleInDegrees += figure.angle
                            nm.move(-figure.pivot)
                            nm
                        }

                        is FigureTranslateWithRotate -> {
                            nm.move(figure.offset)
                            nm.angleInDegrees += figure.angleInDegrees
                            nm
                        }

                        else ->
                            return emptyList()
                    }
                } else state

                figure.collection().flatMap { f ->
                    onlyFigures(f, newState)
                }
            }
        }
        return res
    }
}