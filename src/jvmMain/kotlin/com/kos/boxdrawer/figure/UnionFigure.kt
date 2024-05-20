package com.kos.boxdrawer.figure

import com.kos.figure.Approximation
import com.kos.figure.FigureEmpty
import com.kos.figure.FigureList
import com.kos.figure.FigurePolyline
import com.kos.figure.IFigure
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.MultiPolygon
import org.locationtech.jts.geom.Polygon
import vectors.Vec2

object UnionFigure {
    val factory = GeometryFactory()

    fun diff(a: List<Approximation>, b: List<Approximation>): IFigure {
        return try {
            val pp = multiPolygon(a).union()
            val pb = multiPolygon(b).union()

            val union = pp.difference(pb)

            return figures(union)

        } catch (e: Exception) {
            FigureEmpty
        }
    }

    fun symDiff(a: List<Approximation>, b: List<Approximation>): IFigure {
        return try {
            val pp = multiPolygon(a).union()
            val pb = multiPolygon(b).union()

            val union = pp.symDifference(pb)

            figures(union)

        } catch (e: Exception) {
            FigureEmpty
        }
    }

    fun intersect(a: List<Approximation>, b: List<Approximation>): IFigure {
        return try {
            val pp = multiPolygon(a).union()
            val pb = multiPolygon(b).union()

            val union = pp.intersection(pb)

            figures(union)

        } catch (e: Exception) {
            FigureEmpty
        }
    }

    fun union(a: List<Approximation>): IFigure {
        return try {
            val pp = multiPolygon(a)

            val union = pp.union()

            figures(union)

        } catch (e: Exception) {
            FigureEmpty
        }
    }

    private fun multiPolygon(a: List<Approximation>): MultiPolygon {
        val pp = factory.createMultiPolygon(
            a.flatMap {

                it.approximate(30).filter {
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
}