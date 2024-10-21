package com.kos.figure.algorithms

import com.kos.figure.complex.model.Arc
import com.kos.figure.complex.model.Curve
import com.kos.figure.complex.model.Segment
import vectors.Vec2
import vectors.Vec2.Companion.lerp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

data class Vec4(val minX: Double, val maxX: Double, val minY: Double, val maxY: Double)

fun bezierCurve(t: Double, p0: Vec2, p1: Vec2, p2: Vec2, p3: Vec2): Vec2 {val oneMinusT = 1 - t
    val x = oneMinusT * oneMinusT * oneMinusT * p0.x +
            3 * oneMinusT * oneMinusT * t * p1.x +
            3 * oneMinusT * t * t * p2.x +
            t * t * t * p3.x
    val y = oneMinusT * oneMinusT * oneMinusT * p0.y +
            3 * oneMinusT * oneMinusT * t * p1.y +3 * oneMinusT * t * t * p2.y +
            t * t * t * p3.y
    return Vec2(x, y)
}

fun approximateBezierLength(p0: Vec2, p1: Vec2, p2: Vec2, p3: Vec2, numSegments: Int = 100): Double {
    var length = 0.0
    var previousPoint = bezierCurve(0.0, p0, p1, p2, p3)

    for (i in 1..numSegments) {
        val t = i.toDouble() / numSegments
        val currentPoint = bezierCurve(t, p0, p1, p2, p3)
        val segmentLength = hypot(currentPoint.x - previousPoint.x, currentPoint.y - previousPoint.y)
        length += segmentLength
        previousPoint = currentPoint
    }

    return length
}

fun findBezierIntersections(curve1: Curve, curve2: Curve, tolerance: Double = 0.001): List<Vec2> {
    val intersections = mutableListOf<Vec2>()

    fun findIntersectionsRecursive(b1: Curve, b2: Curve) {
        // Bounding box check (simplified - you'd need to implement bounding box calculation)
        if (!boundingBoxesIntersect(b1, b2)) return

        // Check if segments are small enough
        if (isSmallEnough(b1, tolerance) && isSmallEnough(b2, tolerance)) {
            // Approximate intersection using line segments (simplified)
            val intersection = findLineIntersection(b1.p0, b1.p3, b2.p0, b2.p3)
            if (intersection != null) intersections.add(intersection)
            return
        }

        // Subdivide curves
        val (b1a, b1b) = subdivideBezier(b1,0.5)
        val (b2a, b2b) = subdivideBezier(b2, 0.5)

        // Recursively check intersections
        findIntersectionsRecursive(b1a, b2a)
        findIntersectionsRecursive(b1a, b2b)
        findIntersectionsRecursive(b1b, b2a)
        findIntersectionsRecursive(b1b, b2b)
    }

    findIntersectionsRecursive(curve1, curve2)
    return intersections
}

fun boundingBoxesIntersect(b1: Curve, b2: Curve): Boolean {
    val (minX1, maxX1, minY1, maxY1) = getBoundingBox(b1)
    val (minX2, maxX2, minY2, maxY2) = getBoundingBox(b2)

    return !(maxX1 < minX2 || minX1 > maxX2 || maxY1 < minY2 || minY1 > maxY2)
}

fun getBoundingBox(curve: Curve): Vec4 {
    var minX = Double.POSITIVE_INFINITY
    var maxX = Double.NEGATIVE_INFINITY
    var minY = Double.POSITIVE_INFINITY
    var maxY = Double.NEGATIVE_INFINITY

    for (tt in 0..10) { // Adjust step for finer granularity
        val t = tt*0.1
        val point= bezierCurve(t, curve.p0, curve.p1, curve.p2, curve.p3)
        minX = minOf(minX, point.x)
        maxX = maxOf(maxX, point.x)
        minY = minOf(minY, point.y)
        maxY = maxOf(maxY, point.y)
    }

    return Vec4(minX, maxX, minY, maxY)
}

fun isSmallEnough(b: Curve, tolerance: Double): Boolean {
    val controlPointDistances = listOf(
        distance(b.p0, b.p1),
        distance(b.p1, b.p2),
        distance(b.p2, b.p3)
    )

    return controlPointDistances.all { it <= tolerance }
}

fun distance(p1: Vec2, p2: Vec2): Double {
    val dx = p2.x - p1.x
    val dy = p2.y - p1.y
    return hypot(dx, dy)
}

fun subdivideBezier(b: Curve, t: Double): Pair<Curve, Curve> {
    val p01 = lerp(b.p0, b.p1, t)
    val p12 = lerp(b.p1, b.p2, t)
    val p23 = lerp(b.p2, b.p3, t)

    val p012 = lerp(p01, p12, t)
    val p123 = lerp(p12, p23, t)

    val p0123 = lerp(p012, p123, t)

    val left = Curve(b.p0, p01, p012, p0123)
    val right = Curve(p0123, p123,p23, b.p3)

    return Pair(left, right)
}


fun findSegmentBezierIntersections(segment: Segment, bezier: Curve, tolerance: Double = 1e-6): List<Vec2> {
    val intersections = mutableListOf<Vec2>()

    // 1. Bounding Box Check (you'll need to implement a bounding box check function)
    if (!boundingBoxesIntersect(segment, bezier)) {
        return intersections
    }

    // 2. Subdivision and Intersection Finding (using a recursive helper function)
    findIntersectionsRecursive(segment, bezier, 0.0, 1.0, tolerance, intersections)

    return intersections
}

fun findIntersectionsRecursive(
    segment: Segment,
    bezier: Curve,
    tStart: Double,
    tEnd: Double,
    tolerance: Double,
    intersections: MutableList<Vec2>
) {
    val midT = (tStart + tEnd) / 2
    val subs = subdivideBezier(bezier, midT)

    // 3. Flatness Check (you'll need to implement a flatness check function)
    fun checkFlat(subBezier: Curve){
        if (isBezierFlat(subBezier, tolerance)) {
            val approxSegment = Segment(subBezier.p0, subBezier.p3)
            val intersection = findLineIntersection(segment.start, segment.end, approxSegment.start, approxSegment.end)
            if (intersection != null) {
                intersections.add(intersection)
            }
        } else {
            // 4. Recursive Refinement
            findIntersectionsRecursive(segment, subBezier, tStart, midT, tolerance, intersections)
        }
    }

    checkFlat(subs.first)
    checkFlat(subs.second)
}

fun findBezierArcIntersections(bezier: Curve, arc: Arc, tolerance: Double = 1e-6): List<Vec2> {
    val intersections = mutableListOf<Vec2>()

    // 1. Bounding Box Check (you'll need to adapt boundingBoxesIntersect for arcs)
    if (!boundingBoxesIntersect(bezier, arc)) {
        return intersections
    }

    // 2. Bézier Subdivision and Intersection Finding
    findIntersectionsRecursive(bezier, arc, 0.0, 1.0, tolerance, intersections)

    return intersections
}

fun findIntersectionsRecursive(
    bezier: Curve,
    arc: Arc,
    tStart: Double,
    tEnd: Double,
    tolerance: Double,
    intersections: MutableList<Vec2>
) {
    val midT = (tStart + tEnd) / 2
    val subs = subdivideBezier(bezier, midT)

    fun checkFlat(subBezier: Curve){
        if (isBezierFlat(subBezier, tolerance)) {
            val approxSegment = Segment(subBezier.p0, subBezier.p3)
            intersections.addAll(findSegmentArcIntersections(approxSegment, arc))
        } else {
            // 4. Recursive Refinement
            findIntersectionsRecursive(subBezier, arc, tStart, midT, tolerance, intersections)
        }
    }

    checkFlat(subs.first)
    checkFlat(subs.second)

}

fun boundingBoxesIntersect(segment: Segment, bezier: Curve): Boolean {
    val segmentMinX = min(segment.start.x, segment.end.x)
    val segmentMaxX = max(segment.start.x, segment.end.x)
    val segmentMinY = min(segment.start.y, segment.end.y)
    val segmentMaxY = max(segment.start.y, segment.end.y)

    val bezierMinX = minOf(bezier.p0.x, bezier.p1.x, bezier.p2.x, bezier.p3.x)
    val bezierMaxX = maxOf(bezier.p0.x, bezier.p1.x, bezier.p2.x, bezier.p3.x)
    val bezierMinY = minOf(bezier.p0.y,bezier.p1.y, bezier.p2.y, bezier.p3.y)
    val bezierMaxY = maxOf(bezier.p0.y, bezier.p1.y, bezier.p2.y, bezier.p3.y)

    return !(segmentMaxX < bezierMinX || bezierMaxX < segmentMinX ||
            segmentMaxY < bezierMinY || bezierMaxY < segmentMinY)
}

fun boundingBoxesIntersect(bezier: Curve, arc: Arc): Boolean {
    val bezierMinX = minOf(bezier.p0.x, bezier.p1.x, bezier.p2.x, bezier.p3.x)
    val bezierMaxX = maxOf(bezier.p0.x, bezier.p1.x, bezier.p2.x, bezier.p3.x)
    val bezierMinY = minOf(bezier.p0.y, bezier.p1.y, bezier.p2.y, bezier.p3.y)
    val bezierMaxY = maxOf(bezier.p0.y, bezier.p1.y, bezier.p2.y, bezier.p3.y)

    val arcMinX = arc.center.x - arc.radius
    val arcMaxX = arc.center.x + arc.radius
    val arcMinY = arc.center.y - arc.radius
    val arcMaxY = arc.center.y + arc.radius

    // Check for overlap in x and y dimensions
    if (bezierMaxX < arcMinX || arcMaxX < bezierMinX || bezierMaxY < arcMinY || arcMaxY < bezierMinY) {
        return false // No overlap
    }

    // Additional check for cases where the arc's bounding box is entirely within the Bézier's
    if (bezierMinX <= arcMinX && arcMaxX <= bezierMaxX && bezierMinY <= arcMinY && arcMaxY <= bezierMaxY) {
        return true // Arc's bounding box is inside Bézier's
    }

    // More precise check for potential intersection (optional, but improves accuracy)
    val arcAngles = listOf(0.0, PI / 2, PI, 3 * PI / 2) // Check points at 0, 90, 180, 270 degrees
    for (angle in arcAngles) {
        val pointOnArc = Vec2(arc.center.x + arc.radius * cos(angle), arc.center.y + arc.radius * sin(angle))
        if (pointIsWithinBezierBoundingBox(pointOnArc, bezier)) {
            return true // A point on the arc is within the Bézier's bounding box
        }
    }

    return false // No clear intersection based on bounding boxes
}

// Helper function to check if a point is within the bounding box of a Bézier curve
fun pointIsWithinBezierBoundingBox(point: Vec2, bezier: Curve): Boolean {
    val minX = minOf(bezier.p0.x, bezier.p1.x, bezier.p2.x, bezier.p3.x)
    val maxX = maxOf(bezier.p0.x, bezier.p1.x, bezier.p2.x, bezier.p3.x)
    val minY = minOf(bezier.p0.y, bezier.p1.y, bezier.p2.y, bezier.p3.y)
    val maxY = maxOf(bezier.p0.y, bezier.p1.y, bezier.p2.y, bezier.p3.y)

    return point.x in minX..maxX && point.y in minY..maxY
}

fun isBezierFlat(bezier: Curve, tolerance: Double): Boolean {
    val controlPointDistance =
        (bezier.p1 - bezier.p0).distance(bezier.p2 - bezier.p3) / 2 // Average distance of control points from the line

    return controlPointDistance <= tolerance
}

class BezierManipulation {
}