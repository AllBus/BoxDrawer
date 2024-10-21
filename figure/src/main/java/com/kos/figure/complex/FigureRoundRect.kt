package com.kos.figure.complex

import com.kos.figure.Figure
import com.kos.figure.FigurePolyline
import com.kos.figure.IFigure
import com.kos.figure.IFigurePath
import com.kos.figure.complex.model.Arc
import com.kos.figure.complex.model.PathElement
import com.kos.figure.complex.model.Segment
import vectors.BoundingRectangle
import vectors.Vec2
import kotlin.math.PI

class FigureRoundRect(
    val left: Double,
    val top: Double,
    val right: Double,
    val bottom: Double,
    val radius: Double,
) : FigureRound(
    buildSegments(left, top, right, bottom, radius)
), IFigurePath {

    val edgeCount = 4
    val width = right - left
    val height = top - bottom

    companion object {
        fun buildSegments(
            left: Double,
            top: Double,
            right: Double,
            bottom: Double,
            radius: Double,
        ): List<PathElement> {

            val edges: List<Segment> = listOf(
                Segment(
                    Vec2(left + radius, top),
                    Vec2(right - radius, top),
                ),

                Segment(
                    Vec2(right, top + radius),
                    Vec2(right, bottom - radius),
                ),
                Segment(
                    Vec2(right - radius, bottom),
                    Vec2(left + radius, bottom),
                ),
                Segment(
                    Vec2(left, bottom - radius),
                    Vec2(left, top + radius),
                ),
            )

            val corners: List<Arc> = listOf(
                Arc(
                    Vec2(left + radius, top + radius),
                    radius,
                    true,
                    Math.toRadians(180.0),
                    PI / 2
                ),
                Arc(
                    Vec2(right - radius, top + radius),
                    radius,
                    true,
                    Math.toRadians(270.0),
                    PI / 2
                ),
                Arc(
                    Vec2(right - radius, bottom - radius),
                    radius,
                    true,
                    Math.toRadians(00.0),
                    PI / 2
                ),
                Arc(
                    Vec2(left + radius, bottom - radius),
                    radius,
                    true,
                    Math.toRadians(90.0),
                    PI / 2
                ),
            )

            return edges.zip(corners).flatMap { listOf(it.first, it.second) }
        }
    }

    override fun duplicationAtNormal(h: Double): Figure {
        if (radius + h < 0) {

            return FigurePolyline.rect(
                left = left - h,
                top = top - h,
                right = right + h,
                bottom = bottom + h,
            )
        } else {
            return FigureRoundRect(
                left = left - h,
                top = top - h,
                right = right + h,
                bottom = bottom + h,
                radius = radius + h,
            )
        }
    }

    override fun rect(): BoundingRectangle {
        return BoundingRectangle(Vec2(left, bottom), Vec2(right, top))
    }

    override fun print(): String {
        return "r $width $height $radius"
    }

    override fun collection(): List<IFigure> = emptyList()

    override fun name(): String {
        return "RoundRect"
    }

    override fun translate(translateX: Double, translateY: Double): Figure {
        return FigureRoundRect(
            left = left + translateX,
            top = top + translateY,
            right = right + translateX,
            bottom = bottom + translateY,
            radius = radius,

            )
    }
}
