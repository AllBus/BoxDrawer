package turtoise.svg

import androidx.compose.ui.graphics.vector.PathNode
import com.kos.figure.FigureBezier
import com.kos.figure.FigureEllipse
import com.kos.figure.FigureLine
import com.kos.figure.IFigure
import com.kos.figure.collections.FigureList
import vectors.Vec2

object NodeParser {

    fun quadraticToCubicBezier(p0: Vec2,
                               p1: Vec2,
                               p2: Vec2
    ): List<Vec2> {
        // Calculate control points for the cubic Bézier curve
        val cp1X = p0.x + (2.0 / 3.0) * (p1.x - p0.x)
        val cp1Y = p0.y + (2.0 / 3.0) * (p1.y - p0.y)
        val cp2X = p2.x + (2.0 / 3.0) * (p1.x - p2.x)
        val cp2Y = p2.y + (2.0 / 3.0) * (p1.y - p2.y)

        // Return the control points for the cubic Bézier curve
        return listOf(p0, Vec2(cp1X, cp1Y), Vec2(cp2X, cp2Y), p2)
    }

    fun convertPathToFigure(nodes: List<PathNode>,currentPosition:Vec2): IFigure{
        var pos = currentPosition
        var start = currentPosition
        var pred : PathNode? = null
        var isBegin = true

        val result = mutableListOf<IFigure>()
        for( node in nodes) {
            when (node) {
                is PathNode.ArcTo -> {
                    isBegin = false
                    val sarc = SvgArc(
                        startX = pos.x,
                        startY = pos.y,
                        radiusX = node.horizontalEllipseRadius.toDouble(),
                        radiusY = node.verticalEllipseRadius.toDouble(),
                        rotation = node.theta.toDouble(),
                        largeArcFlag = node.isMoreThanHalf,
                        sweepFlag = node.isPositiveArc,
                        endX = node.arcStartX.toDouble(),
                        endY = node.arcStartY.toDouble()
                    )
                    val darc = convertSvgArcToDxfArc(sarc)

                    result+=FigureEllipse(
                        center = Vec2(darc.centerX, darc.centerY),
                        radius = darc.radius,
                        radiusMinor = darc.minorRadius,
                        rotation = darc.rotation,
                        outSide = sarc.sweepFlag,
                        segmentStartAngle = darc.startAngle,
                        segmentSweepAngle = darc.endAngle
                    )
                    //Todo проверить правильность вычислений
                    pos = Vec2(node.arcStartX.toDouble(), node.arcStartY.toDouble())
                }
                    PathNode.Close -> if (!isBegin){
                        result+=FigureLine(pos, start)
                        isBegin = true
                    }
                is PathNode.CurveTo -> {
                    isBegin = false
                    result+=FigureBezier(
                        listOf(
                            pos,
                            Vec2(node.x1.toDouble(), node.y1.toDouble()),
                            Vec2(node.x2.toDouble(), node.y2.toDouble()),
                            Vec2(node.x3.toDouble(), node.y3.toDouble()),
                        )
                    )
                    pos = Vec2(node.x3.toDouble(), node.y3.toDouble())
                }
                is PathNode.HorizontalTo -> {
                    isBegin = false
                    result+=FigureLine(pos,Vec2(node.x.toDouble(),pos.y))
                    pos = Vec2(node.x.toDouble(),pos.y)
                }
                is PathNode.LineTo -> {
                    isBegin = false
                    result+=FigureLine(pos,Vec2(node.x.toDouble(),node.y.toDouble()))
                    pos = Vec2(node.x.toDouble(),node.y.toDouble())
                }
                is PathNode.MoveTo ->  {
                    pos = Vec2(node.x.toDouble(),node.y.toDouble())
                    if (isBegin){
                        start = pos
                    }
                }
                is PathNode.QuadTo -> {
                    isBegin = false
                    result+=FigureBezier(
                        quadraticToCubicBezier(
                            pos,
                            Vec2(node.x1.toDouble(),node.y1.toDouble()),
                            Vec2(node.x2.toDouble(),node.y2.toDouble())
                        )
                    )
                    pos = Vec2(node.x2.toDouble(),node.y2.toDouble())
                }

                is PathNode.ReflectiveCurveTo -> {
                    isBegin = false
                    val b = when (pred){
                        is PathNode.CurveTo -> Vec2(pred.x2.toDouble(), pred.y2.toDouble())-pos
                        is PathNode.RelativeCurveTo -> -Vec2(pred.dx2.toDouble(), pred.dy2.toDouble())
                        is PathNode.ReflectiveCurveTo -> Vec2(pred.x1.toDouble(), pred.y1.toDouble())-pos
                        is PathNode.RelativeReflectiveCurveTo -> -Vec2(pred.dx1.toDouble(), pred.dy1.toDouble())
                        else -> Vec2.Zero
                    }

                    result+=FigureBezier(
                        listOf(
                            pos,
                            pos+ b,
                            Vec2(node.x1.toDouble(), node.y1.toDouble()),
                            Vec2(node.x2.toDouble(), node.y2.toDouble()),
                        )
                    )
                    pos = Vec2(node.x2.toDouble(), node.y2.toDouble())
                }
                is PathNode.ReflectiveQuadTo -> {
                    isBegin = false

                    val b = when (pred){
                        is PathNode.QuadTo -> Vec2(pred.x2.toDouble(), pred.y2.toDouble())-pos
                        is PathNode.RelativeQuadTo -> -Vec2(pred.dx2.toDouble(), pred.dy2.toDouble())
                        is PathNode.ReflectiveQuadTo -> Vec2(pred.x.toDouble(), pred.y.toDouble())-pos
                        is PathNode.RelativeReflectiveQuadTo -> -Vec2(pred.dx.toDouble(), pred.dy.toDouble())
                        else -> Vec2.Zero
                    }
                    result+=FigureBezier(
                        quadraticToCubicBezier(
                            pos,
                            pos+b,
                            Vec2(node.x.toDouble(), node.y.toDouble())
                        )
                    )
                    pos = Vec2(node.x.toDouble(), node.y.toDouble())
                }
                is PathNode.RelativeArcTo -> {
                    isBegin = false
                    //Todo
                    val sarc = SvgArc(
                        startX = pos.x,
                        startY = pos.y,
                        radiusX = node.horizontalEllipseRadius.toDouble(),
                        radiusY = node.verticalEllipseRadius.toDouble(),
                        rotation = node.theta.toDouble(),
                        largeArcFlag = node.isMoreThanHalf,
                        sweepFlag = node.isPositiveArc,
                        endX = pos.x+node.arcStartDx.toDouble(),
                        endY = pos.y+node.arcStartDy.toDouble()
                    )
                    val darc = convertSvgArcToDxfArc(sarc)

                    result+=FigureEllipse(
                        center = Vec2(darc.centerX, darc.centerY),
                        radius = darc.radius,
                        radiusMinor = darc.minorRadius,
                        rotation = darc.rotation,
                        outSide = sarc.sweepFlag,
                        segmentStartAngle = darc.startAngle,
                        segmentSweepAngle = darc.endAngle
                    )
                    pos += Vec2(node.arcStartDx.toDouble(), node.arcStartDy.toDouble())
                }
                is PathNode.RelativeCurveTo -> {
                    isBegin = false
                    result+=FigureBezier(
                        listOf(
                            pos,
                            pos +Vec2(node.dx1.toDouble(), node.dy1.toDouble()),
                            pos +Vec2(node.dx2.toDouble(), node.dy2.toDouble()),
                            pos +Vec2(node.dx3.toDouble(), node.dy3.toDouble()),
                        )
                    )
                    pos += Vec2(node.dx3.toDouble(), node.dy3.toDouble())
                }
                is PathNode.RelativeHorizontalTo -> {
                    isBegin = false
                    result+=FigureLine(pos,Vec2(pos.x+node.dx.toDouble(),pos.y))
                    pos = Vec2(pos.x+node.dx.toDouble(),pos.y)
                }
                is PathNode.RelativeLineTo -> {
                    isBegin = false
                    result+=FigureLine(pos,Vec2(pos.x+node.dx.toDouble(),pos.y+node.dy.toDouble()))
                    pos = Vec2(pos.x+node.dx.toDouble(),pos.y+node.dy.toDouble())
                }
                is PathNode.RelativeMoveTo -> {
                    pos += Vec2(node.dx.toDouble(),node.dy.toDouble())
                    if (isBegin){
                        start = pos
                    }
                }
                is PathNode.RelativeQuadTo -> {
                    isBegin = false
                    result+=FigureBezier(
                        quadraticToCubicBezier(
                            pos,
                            pos + Vec2(node.dx1.toDouble(),node.dy1.toDouble()),
                            pos + Vec2(node.dx2.toDouble(),node.dy2.toDouble())
                        )
                    )
                    pos += Vec2(node.dx2.toDouble(),node.dy2.toDouble())
                }
                is PathNode.RelativeReflectiveCurveTo -> {
                    isBegin = false
                    val b = when (pred){
                        is PathNode.CurveTo -> Vec2(pred.x2.toDouble(), pred.y2.toDouble())-pos
                        is PathNode.RelativeCurveTo -> -Vec2(pred.dx2.toDouble(), pred.dy2.toDouble())
                        is PathNode.ReflectiveCurveTo -> Vec2(pred.x1.toDouble(), pred.y1.toDouble())-pos
                        is PathNode.RelativeReflectiveCurveTo -> -Vec2(pred.dx1.toDouble(), pred.dy1.toDouble())
                        else -> Vec2.Zero
                    }
                    result+=FigureBezier(
                        listOf(
                            pos,
                            pos+ b,
                            pos + Vec2(node.dx1.toDouble(), node.dy1.toDouble()),
                            pos +Vec2(node.dx2.toDouble(), node.dy2.toDouble()),
                        )
                    )
                    pos += Vec2(node.dx2.toDouble(), node.dy2.toDouble())
                }
                is PathNode.RelativeReflectiveQuadTo -> {
                    isBegin = false
                    val b = when (pred){
                        is PathNode.QuadTo -> Vec2(pred.x2.toDouble(), pred.y2.toDouble())-pos
                        is PathNode.RelativeQuadTo -> -Vec2(pred.dx2.toDouble(), pred.dy2.toDouble())
                        is PathNode.ReflectiveQuadTo -> Vec2(pred.x.toDouble(), pred.y.toDouble())-pos
                        is PathNode.RelativeReflectiveQuadTo -> -Vec2(pred.dx.toDouble(), pred.dy.toDouble())
                        else -> Vec2.Zero
                    }
                    result+=FigureBezier(
                        quadraticToCubicBezier(
                            pos,
                            pos+b,
                            pos+Vec2(node.dx.toDouble(), node.dy.toDouble())
                        )
                    )
                    pos += Vec2(node.dx.toDouble(), node.dy.toDouble())
                }
                is PathNode.RelativeVerticalTo ->  {
                    isBegin = false
                    result+=FigureLine(pos,Vec2(pos.x,pos.y+node.dy.toDouble()))
                    pos = Vec2(pos.x,pos.y+node.dy.toDouble())
                }
                is PathNode.VerticalTo -> {
                    isBegin = false
                    result+=FigureLine(pos,Vec2(pos.x,node.y.toDouble()))
                    pos = Vec2(pos.x,node.y.toDouble())
                }
            }
            pred = node
        }
        return FigureList(result.toList())
    }

    fun convertSvgArcToDxfArc(svgArc: SvgArc): DxfArc {
        // 1. Calculate the center of the arc
        val center = calculateArcCenter(svgArc)

        // 2. Calculate the start and end angles
        val startAngle = calculateAngle(svgArc.startX, svgArc.startY, center)
        val endAngle = calculateAngle(svgArc.endX, svgArc.endY, center)

        // 3. Create the DXF arc
        return DxfArc(
            centerX = center.x,
            centerY = center.y,
            radius = svgArc.radiusX,
            minorRadius = svgArc.radiusY,
            startAngle = startAngle,
            endAngle = endAngle,
            rotation =  Math.toRadians(svgArc.rotation)
        )
    }

    // Helper function to calculate the center of an SVG arc
    private fun calculateArcCenter(svgArc: SvgArc): Vec2 {
        // 1. Convert to radians
        val radiansRotation = Math.toRadians(svgArc.rotation)

        // 2. Adjust end points for rotation
        val cosRotation = Math.cos(radiansRotation)
        val sinRotation = Math.sin(radiansRotation)
        val x1Prime = cosRotation * (svgArc.startX - svgArc.endX) / 2.0 + sinRotation * (svgArc.startY - svgArc.endY) / 2.0
        val y1Prime = -sinRotation * (svgArc.startX - svgArc.endX) / 2.0 + cosRotation * (svgArc.startY - svgArc.endY) / 2.0

        // 3. Calculate thecenter
        val rxSquared = svgArc.radiusX * svgArc.radiusX
        val rySquared = svgArc.radiusY * svgArc.radiusY
        val x1PrimeSquared = x1Prime * x1Prime
        val y1PrimeSquared = y1Prime * y1Prime

        var radicand = (rxSquared * rySquared - rxSquared * y1PrimeSquared - rySquared * x1PrimeSquared) /
                (rxSquared * y1PrimeSquared + rySquared * x1PrimeSquared)
        radicand = if (radicand < 0) 0.0 else radicand
        val coefficient = (if (svgArc.largeArcFlag == svgArc.sweepFlag) -1 else 1) * Math.sqrt(radicand)

        val cxPrime = coefficient * ((svgArc.radiusX * y1Prime) / svgArc.radiusY)
        val cyPrime = coefficient * -((svgArc.radiusY * x1Prime) / svgArc.radiusX)

        // 4. Rotate back to original coordinates
        val cx = cosRotation * cxPrime - sinRotation * cyPrime + (svgArc.startX +svgArc.endX) / 2.0
        val cy = sinRotation * cxPrime + cosRotation * cyPrime + (svgArc.startY + svgArc.endY) / 2.0

        return Vec2(cx, cy)
    }

    // Helper function to calculate the angle of a point relative to the center
    private fun calculateAngle(x: Double, y: Double, center: Vec2): Double {
        // 1. Calculate the difference in coordinates
        val dx = x - center.x
        val dy = y - center.y

        // 2. Calculate the angle using arctangent
        var angle = Math.atan2(dy, dx)

        // 3. Convert to degrees
        angle = Math.toDegrees(angle)

        // 4. Adjust angle to be within 0-360 degrees
        if (angle < 0) {
            angle += 360.0
        }

        return angle
    }
}

data class SvgArc(
    val startX: Double,
    val startY: Double,
    val radiusX: Double,
    val radiusY: Double,
    val rotation: Double,
    val largeArcFlag: Boolean,
    val sweepFlag: Boolean,
    val endX: Double,
    val endY: Double
)

data class DxfArc(
    val centerX: Double,
    val centerY: Double,
    val radius: Double,
    val minorRadius: Double,
    val startAngle: Double,
    val endAngle: Double,
    val rotation: Double
)