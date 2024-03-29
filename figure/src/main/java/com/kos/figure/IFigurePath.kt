package com.kos.figure

import vectors.Vec2

interface IFigurePath {
    fun positionInPath(delta: Double): PointWithNormal
}

class PointWithNormal(
    val point: Vec2,
    val normal: Vec2,
) {
    companion object {
        val EMPTY = PointWithNormal(Vec2.Zero, Vec2.Zero)

        fun fromPreviousPoint(position:Vec2, linePoint:Vec2):PointWithNormal{
            return PointWithNormal(
                position,
                Vec2.normal(linePoint, position)
            )
        }

        fun from(position:Vec2, linePoint:Vec2):PointWithNormal{
            return PointWithNormal(
                position,
                Vec2.normal(position, linePoint)
            )
        }

        fun from(position:Vec2, startPoint:Vec2, endPoint:Vec2):PointWithNormal{
            return PointWithNormal(
                position,
                Vec2.normal(startPoint, endPoint)
            )
        }
    }
}