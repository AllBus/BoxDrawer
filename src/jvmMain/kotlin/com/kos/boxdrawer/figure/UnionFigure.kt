package com.kos.boxdrawer.figure

import com.kos.figure.FigurePolyline
import com.kos.figure.IFigure
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Polygon
import vectors.Vec2

class UnionFigure {

    fun union(a: List<FigurePolyline>): IFigure {
        val factory = GeometryFactory()
        val pp = factory.createMultiPolygon(
            a.map { f ->
                factory.createPolygon(
                    f.points.map { Coordinate(it.x, it.y) }.toTypedArray()
                )
            }.toTypedArray()
        )

        val union = pp.union()

        return FigurePolyline(
            union.coordinates.map { Vec2(it.x, it.y) }
        )
    }
}