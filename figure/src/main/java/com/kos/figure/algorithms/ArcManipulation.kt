package com.kos.figure.algorithms

import com.kos.figure.complex.model.Arc
import com.kos.figure.complex.model.Ellipse
import com.kos.figure.complex.model.Segment
import vectors.BoundingRectangle
import vectors.Vec2
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

fun findArcIntersections(arc1: Arc, arc2: Arc): List<Vec2> {
    val intersections = mutableListOf<Vec2>()

    // 1. Find Circle Intersections
    val d = Vec2.distance(arc1.center, arc2.center)
    if (d > arc1.radius + arc2.radius || d < abs(arc1.radius - arc2.radius)) {
        return intersections // Circles don't intersect
    }

    val a = (arc1.radius.pow(2) - arc2.radius.pow(2) +d.pow(2)) / (2 * d)
    val h = sqrt(arc1.radius.pow(2) - a.pow(2))

    val p2 = Vec2.lerp( arc1.center, arc2.center, a / d)
    val p3x = p2.x + h * (arc2.center.y - arc1.center.y) / d
    val p3y = p2.y - h * (arc2.center.x - arc1.center.x) / d
    val p3 = Vec2(p3x, p3y)

    val p4x = p2.x - h * (arc2.center.y - arc1.center.y) / d
    val p4y = p2.y + h * (arc2.center.x - arc1.center.x) / d
    val p4 = Vec2(p4x, p4y)

    // 2. Check Arc Boundaries
    val angleP3 = atan2(p3.y - arc1.center.y, p3.x - arc1.center.x)
    if (arc1.containsAngle(angleP3) && arc2.containsAngle(angleP3)) {
        intersections.add(p3)
    }

    val angleP4 = atan2(p4.y - arc1.center.y, p4.x - arc1.center.x)
    if (arc1.containsAngle(angleP4) && arc2.containsAngle(angleP4)) {
        intersections.add(p4)
    }

    return intersections
}

fun findSegmentArcIntersections(segment: Segment, arc: Arc): List<Vec2> {
    // 1. Convert segment to line equation (ax + by + c = 0)
    val a = segment.end.y - segment.start.y
    val b = segment.start.x - segment.end.x
    val c = -a * segment.start.x - b * segment.start.y

    val aabb = a * a + b * b

    // 2. Calculate the distance from the circle center to the line
    val distanceToLine = Math.abs(a * arc.center.x + b * arc.center.y + c) / Math.sqrt(aabb)

    // 3. Check if the lineintersects the arc
    if (distanceToLine > arc.radius) {
        return emptyList() // No intersection
    }

    val intersections = mutableListOf<Vec2>()

    // Calculate the intersection point(s) between the line and the circle
    val discriminant = (aabb) * arc.radius * arc.radius -
            (a * arc.center.x + b * arc.center.y + c) * (a * arc.center.x + b * arc.center.y + c)

    if (discriminant >= 0) {
        // There is at least one intersection point
        val x1 =
            ((b * (b * arc.center.x - a * arc.center.y) - a * c) + (if (discriminant > 0) b else 0.0) * Math.sqrt(
                discriminant
            )) / (aabb)
        val y1 =
            ((a * (-b * arc.center.x + a * arc.center.y) - b * c) - (if (discriminant > 0) a else 0.0) * Math.sqrt(
                discriminant
            )) / (aabb)

        val intersectionPoint1 = Vec2(x1, y1)
        if (isPointOnSegment(intersectionPoint1, segment.start, segment.end) &&
            arc.containsAngle(
                atan2(
                    intersectionPoint1.y - arc.center.y,
                    intersectionPoint1.x - arc.center.x
                )
            )
        ) {
            intersections.add(intersectionPoint1)
        }

        if (discriminant > 0) {
            // There are two intersection points
            val x2 =
                ((b * (b * arc.center.x - a * arc.center.y) - a * c) - b * Math.sqrt(discriminant)) / (a * a + b * b)
            val y2 =
                ((a * (-b * arc.center.x + a * arc.center.y) - b * c) + a * Math.sqrt(discriminant)) / (a * a + b * b)

            val intersectionPoint2 = Vec2(x2, y2)

            // Check if the intersection point is within the segment bounds
            if (isPointOnSegment(intersectionPoint2, segment.start, segment.end) &&
                arc.containsAngle(
                    atan2(
                        intersectionPoint2.y - arc.center.y,
                        intersectionPoint2.x - arc.center.x
                    )
                )
            ) {
                intersections.add(intersectionPoint2)
            }
        }
    }
    return intersections
}

fun findClosestPointOnEllipse(ellipse: Ellipse, point: Vec2): Vec2 {
    // 1. Transform the point to the ellipse's local coordinate system
    val transformedPoint = transformPointToEllipseLocal(ellipse, point)

    // 2. Find the closest point on the ellipse in the local coordinate system
    val closestPointLocal = findClosestPointOnEllipseLocal(ellipse, transformedPoint)

    // 3. Transform the closest point back to the original coordinate system
    return transformPointFromEllipseLocal(ellipse, closestPointLocal)
}

// Helper function to transform a point to the ellipse's local coordinate system
private fun transformPointToEllipseLocal(ellipse: Ellipse, point: Vec2): Vec2 {
    val dx = point.x - ellipse.center.x
    val dy = point.y - ellipse.center.y
    val cosRotation = cos(ellipse.rotation)
    val sinRotation = sin(ellipse.rotation)

    val transformedX = cosRotation * dx + sinRotation * dy
    val transformedY = -sinRotation * dx + cosRotation * dy

    return Vec2(transformedX, transformedY)
}

// Helper function to find the closest point on the ellipse in the local coordinate system
private fun findClosestPointOnEllipseLocal(ellipse: Ellipse, point: Vec2): Vec2 {
    if (ellipse.radiusX == 0.0)
        return findClosestPointOnSegment(
            Segment(
                Vec2(ellipse.center.x, ellipse.center.y - ellipse.radiusY),
                Vec2(ellipse.center.x, ellipse.center.y + ellipse.radiusY))
            , point
        )

    // 1. Transform the ellipse to a circle by scaling the y-coordinate
    val scaledPoint = Vec2(point.x, point.y / (ellipse.radiusY / ellipse.radiusX))

    // 2. Find the closest point on the circle
    val closestPointOnCircle = findClosestPointOnCircle(ellipse.radiusX, scaledPoint)

    // 3. Transform the closest point back to the ellipse's local coordinate system
    return Vec2(closestPointOnCircle.x, closestPointOnCircle.y * (ellipse.radiusY / ellipse.radiusX))
}


fun findClosestPointOnCircle(radius:Double, point: Vec2): Vec2 {
    val distanceToCenter = Vec2.distance(point, Vec2(0.0, 0.0))

    if (distanceToCenter == 0.0) {
        // Point is at the center of the circle, return any point on the circle
        return Vec2(radius, 0.0)
    }

    // Calculate the closest point using vector scaling
    return Vec2(point.x * radius / distanceToCenter, point.y * radius / distanceToCenter)
}

// Helper function to transform a point from the ellipse's local coordinate system back to the original coordinate system
private fun transformPointFromEllipseLocal(ellipse: Ellipse, point: Vec2): Vec2 {
    val cosRotation = cos(ellipse.rotation)
    val sinRotation = sin(ellipse.rotation)

    val transformedX = cosRotation * point.x - sinRotation * point.y + ellipse.center.x
    val transformedY = sinRotation * point.x +cosRotation * point.y + ellipse.center.y

    return Vec2(transformedX, transformedY)
}


fun findSegmentEllipseIntersection(segment: Segment, ellipse: Ellipse): List<Vec2> {
    // 1. Transform the segment to the ellipse'slocal coordinate system
    val transformedSegment = transformSegmentToEllipseLocal(ellipse, segment)

    // 2. Find the intersection points in the local coordinate system
    val intersectionPointsLocal = findSegmentEllipseIntersectionLocal(transformedSegment, ellipse)

    // 3. Transform the intersection points back to the original coordinate system
    return intersectionPointsLocal.map { transformPointFromEllipseLocal(ellipse, it) }.filter {  p -> ellipse.containsAngle(Vec2.angle(ellipse.center, p))  }
}

private fun transformSegmentToEllipseLocal(ellipse: Ellipse, segment: Segment): Segment {
    return Segment(
        start = transformPointToEllipseLocal(ellipse, segment.start),
        end = transformPointToEllipseLocal(ellipse, segment.end)
    )
}

private fun findSegmentEllipseIntersectionLocal(segment: Segment, ellipse: Ellipse): List<Vec2> {
    val intersectionPoints = mutableListOf<Vec2>()

    // 1. Represent the segment as a parametric equation:
    // x = segment.start.x + t * (segment.end.x - segment.start.x)
    // y = segment.start.y + t * (segment.end.y - segment.start.y)
    // where 0 <= t <= 1

    // 2. Substitute the parametric equations into the ellipse equation:
    // (x / radiusX)^2 + (y / radiusY)^2 = 1
    val a = ellipse.radiusX
    val b = ellipse.radiusY
    val x1 = segment.start.x
    val y1 =segment.start.y
    val x2 = segment.end.x
    val y2 = segment.end.y

    val A = (x2 - x1).pow(2) / a.pow(2) + (y2 - y1).pow(2) / b.pow(2)
    val B = 2 * (x1 * (x2 - x1) / a.pow(2) + y1 * (y2 - y1) / b.pow(2))
    val C = x1.pow(2) / a.pow(2) + y1.pow(2) / b.pow(2) - 1

    // 3. Solve the resulting quadratic equation for t:
    // At^2 + Bt + C = 0
    val discriminant = B.pow(2) - 4 * A * C

    if (discriminant >= 0) {
        val t1 = (-B + sqrt(discriminant)) / (2 * A)
        val t2 = (-B - sqrt(discriminant)) / (2 * A)

        // 4. If there are realsolutions for t in the range [0, 1], calculate the corresponding intersection points using the parametric equations.
        if (t1 in 0.0..1.0) {
            intersectionPoints.add(Vec2(x1 + t1 * (x2 - x1), y1 + t1 * (y2 - y1)))
        }
        if (t2 in 0.0..1.0 && t1 != t2) {
            intersectionPoints.add(Vec2(x1 + t2 * (x2 - x1), y1 + t2 * (y2 - y1)))
        }
    }

    return intersectionPoints
}

fun findEllipseEllipseIntersection(ellipse1: Ellipse, ellipse2: Ellipse): List<Vec2> {
    val b1 = ellipseBoundingBox(ellipse1)
    val b2 = ellipseBoundingBox(ellipse2)

    if (intersectBoundingBoxes(b1, b2) == Vec4.EMPTY)
        return emptyList()

    val intersectionPoints = mutableListOf<Vec2>()

    fun findIntersections(segment: Segment) {
        val circleIntersections = findSegmentEllipseIntersection(segment, ellipse2)
        if (circleIntersections.isNotEmpty()) {
            intersectionPoints.addAll(circleIntersections)
            return
        }
    }

    val segmentCount = 1000

    var pred = ellipse1.start
    for (i in 1 .. segmentCount){
        val t = i.toDouble() / segmentCount
        val n = ellipse1.position(t)
        val segment = Segment(pred, n)
        pred = n
        findIntersections(segment)
    }

    return intersectionPoints
}


// Helper function to calculate the bounding box of an ellipse
fun ellipseBoundingBox(ellipse: Ellipse): Vec4 {
//    val extremePoints = listOf(
//        ellipse.center + Vec2(ellipse.radiusX, 0.0).rotate(ellipse.rotation),
//        ellipse.center + Vec2(-ellipse.radiusX, 0.0).rotate(ellipse.rotation),
//        ellipse.center + Vec2(0.0, ellipse.radiusY).rotate(ellipse.rotation),
//        ellipse.center + Vec2(0.0, -ellipse.radiusY).rotate(ellipse.rotation)
//    )
//
//    // 2. Find the minimum and maximum x and y values
//    val minX = extremePoints.minOf { it.x }
//    val minY = extremePoints.minOf { it.y }
//    val maxX = extremePoints.maxOf { it.x }
//    val maxY = extremePoints.maxOf { it.y }

    val maxRadius = max(abs(ellipse.radiusX), abs(ellipse.radiusY))

    return Vec4(
        ellipse.center.x-maxRadius,
        ellipse.center.y-maxRadius,
        ellipse.center.x+maxRadius,
        ellipse.center.y+maxRadius
    )
}

// Helper function to calculate the intersection of two bounding boxes
private fun intersectBoundingBoxes(bbox1: Vec4, bbox2: Vec4): Vec4 {
    val minX = maxOf(bbox1.minX, bbox2.minX)
    val minY = maxOf(bbox1.minY, bbox2.minY)
    val maxX = minOf(bbox1.maxX, bbox2.maxX)
    val maxY = minOf(bbox1.maxY, bbox2.maxY)

    // 2. Check if the intersection is valid
    if (maxX < minX || maxY < minY) {
        // Bounding boxes do not intersect
        // You might want to throw an exception or handle this case differently
        // For now, we'll return an empty rectangle
        return Vec4.EMPTY
    }

    // 3. Return the intersection bounding box
    return Vec4(minX, minY, maxX, maxY)
}


data class Matrix2x2(val a: Double, val b: Double, val c: Double, val d: Double) {
    fun inverse(): Matrix2x2 {
        val determinant = a * d - b * c

        if (determinant == 0.0) {
            // Matrix is singular (non-invertible)
            // You might want to throw an exception or handle this case differently
            // For now, we'll return the original matrix
            return this
        }

        return Matrix2x2(
            d / determinant, -b / determinant,
            -c / determinant, a / determinant
        )
    }

    operator fun times(other: Vec2): Vec2 {
        return Vec2(
            a * other.x + b * other.y,
            c * other.x + d * other.y
        )
    }
}

/** Поиск пересечения окружности и эллипса */
fun findCircleEllipseIntersectionSubdivision(
    circle: Arc,
    ellipse: Ellipse,
): List<Vec2> {
    val intersectionPoints = mutableListOf<Vec2>()

    fun findIntersections(segment: Segment) {
        val circleIntersections = findSegmentArcIntersections(segment, circle)
        if (circleIntersections.isNotEmpty()) {
            intersectionPoints.addAll(circleIntersections)
            return
        }
    }

    val segmentCount = 1000

    var pred = ellipse.start
    for (i in 1 .. segmentCount){
        val t = i.toDouble() / segmentCount
        val n = ellipse.position(t)
        val segment = Segment(pred, n)
        pred = n
        findIntersections(segment)
    }

    return intersectionPoints.filter {  p -> circle.containsAngle(Vec2.angle(circle.center, p))  }
}