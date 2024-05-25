package com.kos.figure

import vectors.Vec2

interface IFigurePath {
    fun positionInPath(delta: Double): PointWithNormal
    fun positionInPath(edge: Int, delta: Double): PointWithNormal
   // fun positionInPathAtMM(edge: Int, mm: Double): PointWithNormal
    fun pathLength():Double
    fun pathLength(edge: Int):Double
    fun edgeCount():Int
}

class PointWithNormal(
    val point: Vec2,
    val normal: Vec2,
) {
    fun revert(xy: Vec2, radians: Double) = PointWithNormal(
        point = point.rotate(-radians) - xy,
        normal = normal.rotate(-radians)
    )

    companion object {
        val EMPTY = PointWithNormal(Vec2.Zero, Vec2.Zero)

        fun fromPreviousPoint(position: Vec2, linePoint: Vec2): PointWithNormal {
            return PointWithNormal(
                position,
                Vec2.normal(linePoint, position)
            )
        }

        fun from(position: Vec2, linePoint: Vec2): PointWithNormal {
            return PointWithNormal(
                position,
                Vec2.normal(position, linePoint)
            )
        }

        fun from(position: Vec2, startPoint: Vec2, endPoint: Vec2): PointWithNormal {
            return PointWithNormal(
                position,
                Vec2.normal(startPoint, endPoint)
            )
        }
    }
}