package com.kos.figure.algorithms

import com.kos.figure.complex.model.Arc
import com.kos.figure.complex.model.Curve
import com.kos.figure.complex.model.CurveList
import com.kos.figure.complex.model.Ellipse
import com.kos.figure.complex.model.Segment
import vectors.Vec2
import vectors.Vec2.Companion.lerp
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

data class Vec4(val minX: Double, val maxX: Double, val minY: Double, val maxY: Double) {
    companion object {
        val EMPTY = Vec4(0.0, 0.0, 0.0, 0.0)
    }
}

fun bezierCurve(t: Double, p0: Vec2, p1: Vec2, p2: Vec2, p3: Vec2): Vec2 {
    val oneMinusT = 1 - t
    val x = oneMinusT * oneMinusT * oneMinusT * p0.x +
            3 * oneMinusT * oneMinusT * t * p1.x +
            3 * oneMinusT * t * t * p2.x +
            t * t * t * p3.x
    val y = oneMinusT * oneMinusT * oneMinusT * p0.y +
            3 * oneMinusT * oneMinusT * t * p1.y + 3 * oneMinusT * t * t * p2.y +
            t * t * t * p3.y
    return Vec2(x, y)
}

fun approximateBezierLength(
    p0: Vec2,
    p1: Vec2,
    p2: Vec2,
    p3: Vec2,
    numSegments: Int = 100
): Double {
    var length = 0.0
    var previousPoint = bezierCurve(0.0, p0, p1, p2, p3)

    for (i in 1..numSegments) {
        val t = i.toDouble() / numSegments
        val currentPoint = bezierCurve(t, p0, p1, p2, p3)
        val segmentLength =
            hypot(currentPoint.x - previousPoint.x, currentPoint.y - previousPoint.y)
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
        val (b1a, b1b) = subdivideBezier(b1, 0.5)
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
        val t = tt * 0.1
        val point = bezierCurve(t, curve.p0, curve.p1, curve.p2, curve.p3)
        minX = minOf(minX, point.x)
        maxX = maxOf(maxX, point.x)
        minY = minOf(minY, point.y)
        maxY = maxOf(maxY, point.y)
    }

    return Vec4(minX, maxX, minY, maxY)
}

fun isSmallEnough(b: Curve, tolerance: Double): Boolean {
    val controlPointDistances = listOf(
        Vec2.distance(b.p0, b.p1),
        Vec2.distance(b.p1, b.p2),
        Vec2.distance(b.p2, b.p3)
    )

    return controlPointDistances.all { it <= tolerance }
}

fun subdivideBezier(b: Curve, t: Double): Pair<Curve, Curve> {
    val p01 = lerp(b.p0, b.p1, t)
    val p12 = lerp(b.p1, b.p2, t)
    val p23 = lerp(b.p2, b.p3, t)

    val p012 = lerp(p01, p12, t)
    val p123 = lerp(p12, p23, t)

    val p0123 = lerp(p012, p123, t)

    val left = Curve(b.p0, p01, p012, p0123)
    val right = Curve(p0123, p123, p23, b.p3)

    return Pair(left, right)
}


fun findSegmentBezierIntersections(
    segment: Segment,
    bezier: Curve,
    tolerance: Double = 1e-6
): List<Vec2> {
    val intersections = mutableListOf<Vec2>()

    // 1. Bounding Box Check (you'll need to implement a bounding box check function)
    if (!boundingBoxesIntersect(segment, bezier)) {
        return intersections
    }

    // 2. Subdivision and Intersection Finding (using a recursive helper function)
    findIntersectionsRecursive(segment, bezier, tolerance, intersections)

    return intersections
}

private fun findIntersectionsRecursive(
    segment: Segment,
    bezier: Curve,
    tolerance: Double,
    intersections: MutableList<Vec2>
) {
    val subs = subdivideBezier(bezier, 0.5)

    // 3. Flatness Check (you'll need to implement a flatness check function)
    fun checkFlat(subBezier: Curve) {
        if (isSmallEnough(subBezier, tolerance)) {
            val approxSegment = Segment(subBezier.p0, subBezier.p3)
            val intersection = findLineIntersection(
                segment.start,
                segment.end,
                approxSegment.start,
                approxSegment.end
            )
            if (intersection != null) {
                intersections.add(intersection)
            }
        } else {
            // 4. Recursive Refinement
            findIntersectionsRecursive(segment, subBezier, tolerance, intersections)
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
    val bezierMinY = minOf(bezier.p0.y, bezier.p1.y, bezier.p2.y, bezier.p3.y)
    val bezierMaxY = maxOf(bezier.p0.y, bezier.p1.y, bezier.p2.y, bezier.p3.y)

    return !(segmentMaxX < bezierMinX || bezierMaxX < segmentMinX ||
            segmentMaxY < bezierMinY || bezierMaxY < segmentMinY)
}

fun boundingBoxesIntersect(bezier: Curve, arc: Arc): Boolean {
    val bezierMinX = minOf(bezier.p0.x, bezier.p1.x, bezier.p2.x, bezier.p3.x)
    val bezierMaxX = maxOf(bezier.p0.x, bezier.p1.x, bezier.p2.x, bezier.p3.x)
    val bezierMinY = minOf(bezier.p0.y, bezier.p1.y, bezier.p2.y, bezier.p3.y)
    val bezierMaxY = maxOf(bezier.p0.y, bezier.p1.y, bezier.p2.y, bezier.p3.y)

    val arcRadius = abs(arc.radius)
    val arcMinX = arc.center.x - arcRadius
    val arcMaxX = arc.center.x + arcRadius
    val arcMinY = arc.center.y - arcRadius
    val arcMaxY = arc.center.y + arcRadius

    // Check for overlap in x and y dimensions
    if (bezierMaxX < arcMinX || arcMaxX < bezierMinX || bezierMaxY < arcMinY || arcMaxY < bezierMinY) {
        return false // No overlap
    }

    return true
    /*
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

     */
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

fun isMinimalBezier(start: Double, end: Double, tolerance: Double): Boolean {
    return end - start <= tolerance
}

object BezierManipulation {

    fun findNearestPointOnCubicBezier(bezier: CurveList, point: Vec2): Vec2 {
        if (bezier.size == 0) {
            return point
        }
        var res = bezier[0].p0
        for (i in 0 until bezier.size) {
            val b = bezier[i]
            val p = findNearestPointOnCubicBezier(b, point)
            if (Vec2.distance(p, point) < Vec2.distance(res, point)) {
                res = p
            }
        }
        return res
    }

    fun findNearestPointOnCubicBezier(bezier: Curve, point: Vec2): Vec2 {
        // 1. Define the distance function
        val distanceFunction = { t: Double ->
            val curvePoint = bezier.pointAt(t)
            (curvePoint.x - point.x).pow(2) + (curvePoint.y - point.y).pow(2)
        }

        fun distanceDerivative(t: Double): Double {
            val curvePoint = bezier.pointAt(t)
            val curveDerivative = bezier.derivative(t)
            val distance = distanceFunction(t)

            return (1 / (2 * distance)) * (2 * (curvePoint.x - point.x) * curveDerivative.x + 2 * (curvePoint.y - point.y) * curveDerivative.y)
        }

        // 2. Apply Newton-Raphson to find the minimum ofthe distance function
        var t = 0.5 // Initial guess
        val tolerance = 1e-6
        var iterations = 0
        val maxIterations = 10

        while (iterations < maxIterations) {
            // ... (calculate the derivative of the distance function)
            val nextT = t - distanceFunction(t) / distanceDerivative(t)

            if (abs(nextT - t) < tolerance) {
                break
            }

            t = nextT
            iterations++
        }

        // 3. Return the nearest point on the curve
        return bezier.pointAt(t)
    }


    fun findBezierArcIntersections(
        bezier: Curve,
        arc: Arc,
        tolerance: Double = 0.0001
    ): List<Vec2> {
        val intersections = mutableListOf<Vec2>()

        // 1. Bounding Box Check (you'll need to adapt boundingBoxesIntersect for arcs)
        if (!boundingBoxesIntersect(bezier, arc)) {
            return intersections
        }

        // 2. Bézier Subdivision and Intersection Finding
        findIntersectionsRecursive(bezier, arc, tolerance, intersections)

        return intersections
    }

    fun findClosestPointOnPolygon(polygon: List<Vec2>, point: Vec2): Vec2 {
        var closestPoint = polygon[0]
        var minDistance = Double.MAX_VALUE

        val iter = CurveList(polygon)
        for (i in 0 until iter.size) {
            val pp = findClosestPoint(iter[i], point)
            val nd = Vec2.distance(point, pp)
            if (nd < minDistance) {
                minDistance = nd
                closestPoint = pp
            }
        }

        return closestPoint
    }

    private fun findClosestPoint(curve: Curve, point: Vec2): Vec2 {
        val tolerance = 1e-6

        val res = mutableListOf<Vec2>()

        fun subdivide(curve: Curve, depth: Int) {
            // 1. Check if the curve's bounding box intersects the segment's bounding box
            if (!boundingBoxesIntersect(curve, point)) return

            if (depth > 10) {
                // Maximum recursion depth
                res.add(distancedPointOnCurve(curve, point))
                return

            }

            // 2. Check if the curve and segment are close enough to be considered intersecting
            if (isSmallEnough(curve, tolerance)) {
                res.add(distancedPointOnCurve(curve, point))
                return
            }

            // 3. Subdivide the curve into two halves
            val (leftCurve, rightCurve) = subdivideCurve(curve)

            // 4. Recursively check for intersections with the subdivided curves
            subdivide(leftCurve, depth + 1)
            subdivide(rightCurve, depth + 1)
        }

        subdivide(curve, 0)
        return res.minByOrNull { Vec2.distance(point, it) } ?: curve.p0 // Remove duplicate points
    }

    private fun findIntersectionsRecursive(
        bezier: Curve,
        arc: Arc,
        tolerance: Double,
        intersections: MutableList<Vec2>
    ) {
        //  println("check curve ${bezier.p0} ${bezier.p3} circle ${arc.center} ${arc.radius} ${arc.sweepAngle}")
        fun checkFlat(subBezier: Curve, depth: Int) {
            if (!boundingBoxesIntersect(subBezier, arc)) return

            if (isSmallEnough(subBezier, tolerance)) {
                val segment = Segment(subBezier.p0, subBezier.p3)
                intersections.addAll(findSegmentArcIntersections(segment, arc))
            } else {
                if (depth > 10) {
                    val segment = Segment(subBezier.p0, subBezier.p3)
                    intersections.addAll(findSegmentArcIntersections(segment, arc))
                    return
                }
                // 4. Recursive Refinement
                val subs = subdivideCurve(subBezier)
                checkFlat(subs.first, depth + 1)
                checkFlat(subs.second, depth + 1)
            }
        }

        val subs = subdivideCurve(bezier)
        checkFlat(subs.first, 0)
        checkFlat(subs.second, 0)

    }

    fun findBezierSegmentIntersectionSubdivision(
        curve: Curve,
        segment: Segment,
        tolerance: Double
    ): List<Vec2> {
        val intersectionPoints = mutableListOf<Vec2>()

        fun subdivide(curve: Curve, segment: Segment, depth: Int) {
         //   println("$depth $curve")
            if (depth > 10) {
                // Maximum recursion depth
                findLineIntersection(curve.start, curve.end, segment.start, segment.end)?.let { p ->
                    intersectionPoints.add(p)
                }
                return
            }

            // 1. Check if the curve's bounding box intersects the segment's bounding box
            if (!boundingBoxesIntersect(curve, segment)) return

            // 2. Check if the curve and segment are close enough to be considered intersecting
            if (curveIsCloseToSegment(curve, segment, tolerance)) {
                findLineIntersection(curve.start, curve.end, segment.start, segment.end)?.let { p ->
                    intersectionPoints.add(p)
                }
                return
            }

            // 3. Subdivide the curve into two halves
            val (leftCurve, rightCurve) = subdivideCurve(curve)

            // 4. Recursively check for intersections with the subdivided curves
            subdivide(leftCurve, segment, depth + 1)
            subdivide(rightCurve, segment, depth + 1)
        }

        subdivide(curve, segment, 0)
        return intersectionPoints.distinct() // Remove duplicate points
    }

    fun findBezierEllipseIntersection(
        curve: Curve,
        ellipse: Ellipse,
        segmentCount: Int = 100
    ): List<Vec2> {
        val bb = boundingBoxesIntersect(curve, ellipse)
        if (!bb) {
            return emptyList()
        }

        val intersectionPoints = mutableListOf<Vec2>()

        fun findIntersections(segment: Segment) {
            val circleIntersections =
                findBezierSegmentIntersectionSubdivision(curve, segment, 0.0001)
            if (circleIntersections.isNotEmpty()) {
                intersectionPoints.addAll(circleIntersections)
                return
            }
        }

        var pred = ellipse.start
        for (i in 1..segmentCount) {
            val t = i.toDouble() / segmentCount
            val n = ellipse.position(t)
            val segment = Segment(pred, n)
            pred = n
            findIntersections(segment)
        }

        return intersectionPoints
    }

    private fun boundingBoxesIntersect(curve: Curve, ellipse: Ellipse): Boolean {
        // 1. Calculate the bounding box of the curve
        val curveMinX = minOf(curve.p0.x, curve.p1.x, curve.p2.x, curve.p3.x)
        val curveMaxX = maxOf(curve.p0.x, curve.p1.x, curve.p2.x, curve.p3.x)
        val curveMinY = minOf(curve.p0.y, curve.p1.y, curve.p2.y, curve.p3.y)
        val curveMaxY = maxOf(curve.p0.y, curve.p1.y, curve.p2.y, curve.p3.y)

        val maxRadius = max(abs(ellipse.radiusX), abs(ellipse.radiusY))
        val segmentMinX = ellipse.center.x - maxRadius
        val segmentMaxX = ellipse.center.x + maxRadius
        val segmentMinY = ellipse.center.y - maxRadius
        val segmentMaxY = ellipse.center.y + maxRadius

        return (curveMinX <= segmentMaxX && curveMaxX >= segmentMinX &&
                curveMinY <= segmentMaxY && curveMaxY >= segmentMinY)
    }

    // Helper function to check if the bounding boxes of a curve and a segment intersect
    private fun boundingBoxesIntersect(curve: Curve, segment: Segment): Boolean {
        // 1. Calculate the bounding box of the curve
        val curveMinX = minOf(curve.p0.x, curve.p1.x, curve.p2.x, curve.p3.x)
        val curveMaxX = maxOf(curve.p0.x, curve.p1.x, curve.p2.x, curve.p3.x)
        val curveMinY = minOf(curve.p0.y, curve.p1.y, curve.p2.y, curve.p3.y)
        val curveMaxY = maxOf(curve.p0.y, curve.p1.y, curve.p2.y, curve.p3.y)

        // 2. Calculatethe bounding box of the segment
        val segmentMinX = minOf(segment.start.x, segment.end.x)
        val segmentMaxX = maxOf(segment.start.x, segment.end.x)
        val segmentMinY = minOf(segment.start.y, segment.end.y)
        val segmentMaxY = maxOf(segment.start.y, segment.end.y)

        // 3. Check if the bounding boxes overlap
        return (curveMinX <= segmentMaxX && curveMaxX >= segmentMinX &&
                curveMinY <= segmentMaxY && curveMaxY >= segmentMinY)
    }

    private fun boundingBoxesIntersect(curve: Curve, point: Vec2): Boolean {
        // 1. Calculate the bounding box of the curve
        val curveMinX = minOf(curve.p0.x, curve.p1.x, curve.p2.x, curve.p3.x)
        val curveMaxX = maxOf(curve.p0.x, curve.p1.x, curve.p2.x, curve.p3.x)
        val curveMinY = minOf(curve.p0.y, curve.p1.y, curve.p2.y, curve.p3.y)
        val curveMaxY = maxOf(curve.p0.y, curve.p1.y, curve.p2.y, curve.p3.y)


        // 3. Check if the bounding boxes overlap
        return (point.x in curveMinX..curveMaxX &&
                point.y in curveMinY..curveMaxY)
    }

    // Helper function to check if a curve is close enough to a segment to be considered intersecting
    private fun curveIsCloseToSegment(curve: Curve, segment: Segment, tolerance: Double): Boolean {
        // 1. Calculate the distance from each control point of the curve to the segment
        val distances = listOf(
            distancePointToSegment(curve.p0, segment),
            distancePointToSegment(curve.p1, segment),
            distancePointToSegment(curve.p2, segment),
            distancePointToSegment(curve.p3, segment)
        )

        // 2. Check if any of the distances are less than the tolerance
        return distances.any { it < tolerance }
    }

    // Helper function to subdivide a Bézier curve into two halves
    fun subdivideCurve(curve: Curve): Pair<Curve, Curve> {
        val t = 0.5
        val A = curve.p0
        val B = curve.p1
        val C = curve.p2
        val D = curve.p3
        val E = lerp(A, B, t)
        val F = lerp(B, C, t)
        val G = lerp(C, D, t)
        val H = lerp(E, F, t)
        val J = lerp(F, G, t)
        val K = lerp(H, J, t)
        return Pair(Curve(A, E, H, K), Curve(K, J, G, D))
    }


    fun findBezierSegmentIntersection(curve: Curve, segment: Segment): List<Vec2> {
        // 1. Define a function to represent the Bézier curve equation
        val bezierFunction: (Double) -> Vec2 = { t ->
            val x = (1 - t).pow(3) * curve.p0.x +
                    3 * (1 - t).pow(2) * t * curve.p1.x +
                    3 * (1 - t) * t.pow(2) * curve.p2.x +
                    t.pow(3) * curve.p3.x
            val y = (1 - t).pow(3) * curve.p0.y +
                    3 * (1 - t).pow(2) * t * curve.p1.y +
                    3 * (1 - t) * t.pow(2) * curve.p2.y +
                    t.pow(3) * curve.p3.y
            Vec2(x, y)
        }

        // 2. Define a function to represent the distance between a point on the curve and the line segment
        val distanceFunction: (Double) -> Double = { t ->
            val pointOnCurve = bezierFunction(t)
            distancePointToSegment(pointOnCurve, segment)
        }

        // 3. Find the roots of the distance function (where the distance is 0)
        val roots = findRoots(distanceFunction, 0.0, 1.0, tolerance = 0.00001)

        // 4. Calculate the intersection points using the roots
        val intersectionPoints = roots.map { t -> bezierFunction(t) }

        return intersectionPoints
    }

    fun findCircleBezierIntersection(circle: Arc, curve: Curve): List<Vec2> {
        // 1. Define a function to represent theBézier curve equation
        val bezierFunction: (Double) -> Vec2 = { t ->
            val mt = 1 - t
            val x = mt.pow(3) * curve.p0.x +
                    3 * mt.pow(2) * t * curve.p1.x +
                    3 * mt * t.pow(2) * curve.p2.x +
                    t.pow(3) * curve.p3.x
            val y = mt.pow(3) * curve.p0.y +
                    3 * mt.pow(2) * t * curve.p1.y +
                    3 * mt * t.pow(2) * curve.p2.y +
                    t.pow(3) * curve.p3.y
            Vec2(x, y)
        }

        val distanceDifferential: (Double) -> Double = { t ->
            val pointOnCurve = bezierFunction(t)
            val mt = 1 - t
            val derivativeX = 3 * mt.pow(2) * (curve.p1.x - curve.p0.x) +
                    6 * mt * t * (curve.p2.x - curve.p1.x) +
                    3 * t.pow(2) * (curve.p3.x - curve.p2.x)
            val derivativeY = 3 * mt.pow(2) * (curve.p1.y - curve.p0.y) +
                    6 * mt * t * (curve.p2.y - curve.p1.y) +
                    3 * t.pow(2) * (curve.p3.y - curve.p2.y)

            val distanceToCenter = distance(pointOnCurve, circle.center)
            ((pointOnCurve.x - circle.center.x) * derivativeX + (pointOnCurve.y - circle.center.y) * derivativeY) / distanceToCenter
        }

        // 2. Define a function to represent the distance between a point on the curve and the circle center
        val distanceFunction: (Double) -> Double = { t ->
            val pointOnCurve = bezierFunction(t)
            distance(pointOnCurve, circle.center) - circle.radius
        }

        // 3. Find the roots of the distance function (where the distance is 0)
        val roots = findRoots(distanceFunction, 0.0, 1.0, tolerance = 1e-6)

        // 4. Calculate the intersection points using the roots
        val intersectionPoints = roots.map { t -> bezierFunction(t) }

        return intersectionPoints
    }


    private fun findRoots(
        function: (Double) -> Double,
        start: Double,
        end: Double,
        tolerance: Double,
        stepCount: Int = 100,
        iterationCount: Int = 5
    ): List<Double> {
        val roots = mutableListOf<Double>()
        var t = start

        val stepSize = abs((end - start) / stepCount)
        while (t <= end) {
            var x = t
            var fx = function(x)

            var i = 0
            while (Math.abs(fx) > tolerance && i < iterationCount) {
                i++
                val dfx =
                    (function(x + tolerance) - function(x - tolerance)) / (2 * tolerance) // Numerical derivative
                x -= fx / dfx
                fx = function(x)
            }

            if (x in (start + tolerance)..(end - tolerance) && !roots.contains(x)) {
                roots.add(x)
            }

            t += stepSize
        }

        return roots
    }

    private fun distancedPointOnCurve(curve: Curve, point: Vec2): Vec2 {
        val segmentStart: Vec2 = curve.p0
        val segmentEnd: Vec2 = curve.p3

        val segmentVector = Vec2(segmentEnd.x - segmentStart.x, segmentEnd.y - segmentStart.y)
        val pointVector = Vec2(point.x - segmentStart.x, point.y - segmentStart.y)

        val dotProduct = Vec2.dot(segmentVector, pointVector)
        val segmentLengthSquared =
            segmentVector.x * segmentVector.x + segmentVector.y * segmentVector.y

        if (segmentLengthSquared == 0.0) {
            return curve.pointAt(0.5)
        }

        val t = dotProduct / segmentLengthSquared

        return if (t < 0) {
            curve.pointAt(0.0)
        } else if (t > 1) {
            curve.pointAt(1.0)
        } else {
            curve.pointAt(t)
        }
    }

    private fun distancePointToSegment(point: Vec2, segmentStart: Vec2, segmentEnd: Vec2): Double {
        val segmentVector = Vec2(segmentEnd.x - segmentStart.x, segmentEnd.y - segmentStart.y)
        val pointVector = Vec2(point.x - segmentStart.x, point.y - segmentStart.y)

        val dotProduct = Vec2.dot(segmentVector, pointVector)
        val segmentLengthSquared =
            segmentVector.x * segmentVector.x + segmentVector.y * segmentVector.y

        if (segmentLengthSquared == 0.0) {
            return distance(point, segmentStart) // Segment is a point
        }

        val t = dotProduct / segmentLengthSquared

        return if (t < 0) {
            distance(point, segmentStart) // Before the segment
        } else if (t > 1) {
            distance(point, segmentEnd) // After the segment
        } else {
            val closestPointOnSegment =
                Vec2(segmentStart.x + t * segmentVector.x, segmentStart.y + t * segmentVector.y)
            distance(point, closestPointOnSegment) // On the segment
        }
    }

    // Helper function to calculate the distance from a point to a line segment
    private fun distancePointToSegment(point: Vec2, segment: Segment): Double {
        val segmentVector = Vec2(segment.end.x - segment.start.x, segment.end.y - segment.start.y)
        val pointVector = Vec2(point.x - segment.start.x, point.y - segment.start.y)

        val dotProduct = Vec2.dot(segmentVector, pointVector)
        val segmentLengthSquared =
            segmentVector.x * segmentVector.x + segmentVector.y * segmentVector.y

        if (segmentLengthSquared == 0.0) {
            return distance(point, segment.start) // Segment is a point
        }

        val t = dotProduct / segmentLengthSquared

        return if (t < 0) {
            distance(point, segment.start) // Before the segment
        } else if (t > 1) {
            distance(point, segment.end) // After the segment
        } else {
            val closestPointOnSegment =
                Vec2(segment.start.x + t * segmentVector.x, segment.start.y + t * segmentVector.y)
            distance(point, closestPointOnSegment) // On the segment
        }
    }

    private fun distance(p1: Vec2, p2: Vec2): Double {
        return Vec2.distance(p1, p2)
    }
}

