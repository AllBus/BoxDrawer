package com.kos.figure.algorithms

import com.kos.figure.Approximation
import com.kos.figure.Figure
import com.kos.figure.FigureBezier
import com.kos.figure.FigureCircle
import com.kos.figure.FigureEmpty
import com.kos.figure.collections.FigureList
import com.kos.figure.FigurePolyline
import com.kos.figure.IFigure
import com.kos.figure.collections.toFigure
import org.locationtech.jts.awt.PointShapeFactory.Circle
import org.locationtech.jts.awt.PolygonShape
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.MultiPolygon
import org.locationtech.jts.geom.Polygon
import vectors.Vec2
import java.awt.Shape
import java.awt.geom.Arc2D
import java.awt.geom.Area
import java.awt.geom.CubicCurve2D
import java.awt.geom.Path2D

object UnionFigure {
    val factory = GeometryFactory()

    fun diff(a: List<Approximation>, b: List<Approximation>, approximationSize:Int): IFigure {
        return try {
            val pp = multiPolygon(a, approximationSize).union()
            val pb = multiPolygon(b, approximationSize).union()

            val union = pp.difference(pb)

            return figures(union)

        } catch (e: Exception) {
            FigureEmpty
        }
    }

    fun symDiff(a: List<Approximation>, b: List<Approximation>, approximationSize:Int): IFigure {
        return try {
            val pp = multiPolygon(a, approximationSize).union()
            val pb = multiPolygon(b, approximationSize).union()

            val union = pp.symDifference(pb)

            figures(union)

        } catch (e: Exception) {
            FigureEmpty
        }
    }

    fun intersect(a: List<Approximation>, b: List<Approximation>, approximationSize:Int): IFigure {
        return try {
            val pp = multiPolygon(a,approximationSize).union()
            val pb = multiPolygon(b,approximationSize).union()
//
//           val i= intersectArbitraryPolygons(
//                a.first().approximate(200).first(),
//                b.first().approximate(200).first()
//            )
//            return i.map { f -> FigurePolyline(f, true) }.toFigure()
            val union = pp.intersection(pb)

            figures(union)

        } catch (e: Exception) {
            FigureEmpty
        }
    }

    fun union(a: List<Approximation>, approximationSize:Int): IFigure {
        return try {
            val pp = multiPolygon(a, approximationSize)

            val union = pp.union()

            figures(union)

        } catch (e: Exception) {
            FigureEmpty
        }
    }

    private fun multiPolygon(a: List<Approximation>, approximationSize:Int): MultiPolygon {
        val pp = factory.createMultiPolygon(
            a.flatMap {

                it.approximate(approximationSize).filter {
                    it.size >= 3
                }.map {
                    if (it.last() != it.first())
                        (it + it.first())
                    else
                        it
                }
            }.map { f ->
                factory.createPolygon(
                    f.map { Coordinate(it.x, it.y) }.toTypedArray()
                )
            }.toTypedArray()
        )
        return pp
    }

    fun figures(g: Geometry): IFigure {
        return FigureList(
            geometries(g).map { u ->
                FigurePolyline(
                    u.coordinates.map { Vec2(it.x, it.y) }
                )
            }
        )
    }

    fun geometries(g: Geometry): List<Geometry> {
        val n = g.numGeometries

        if (g is Polygon) {
            return (listOf(g.exteriorRing) +
                    (0 until g.numInteriorRing).map { u ->
                        g.getInteriorRingN(u)
                    }
                    )
        }
        if (n == 0)
            return emptyList()
        if (n == 1)
            return listOf(g)

        return (0 until n).flatMap { u ->
            geometries(g.getGeometryN(u))
        }
    }

    fun figureToArea(figure: IFigure):Area {
        val a:Shape = when (figure){
            is FigureCircle -> Arc2D.Double(figure.center.x-figure.radius, figure.center.y-figure.radius,figure.center.x+figure.radius, figure.center.y+figure.radius,Math.toDegrees( figure.segmentStartAngle), Math.toDegrees(figure.segmentSweepAngle), Arc2D.OPEN)
            is FigureBezier -> {
                val p = Path2D.Double()
                figure.points.windowed(4, 3).forEach { curve ->
                    p.append(CubicCurve2D.Double(
                        curve[0].x, curve[0].y,
                        curve[1].x, curve[1].y,
                        curve[2].x, curve[2].y,
                        curve[3].x, curve[3].y,
                    ), true)
                }
                p
            }
            is FigurePolyline ->{
                PolygonShape(figure.points.map { Coordinate(it.x, it.y)  }.toTypedArray(), emptyList<Shape>() )
            }
            else -> Area()
        }
        return Area(a)
    }
}