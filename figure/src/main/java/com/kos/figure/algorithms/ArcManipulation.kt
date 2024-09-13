package com.kos.figure.algorithms

import com.kos.figure.complex.Arc
import com.kos.figure.complex.Segment
import vectors.Vec2
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.pow
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
    val intersections = mutableListOf<Vec2>()

    // 1. Find Circle-Line Intersections
    val d = segment.start.distance(arc.center)
    val a = (segment.end.x - segment.start.x) * (arc.center.x - segment.start.x) +
            (segment.end.y - segment.start.y) * (arc.center.y - segment.start.y)
    val b = 2 * ((segment.end.x - segment.start.x) * (segment.start.x - arc.center.x) +
            (segment.end.y - segment.start.y) * (segment.start.y - arc.center.y))
    val c = arc.center.x.pow(2) + arc.center.y.pow(2) + segment.start.x.pow(2) +
            segment.start.y.pow(2) - 2 * (arc.center.x * segment.start.x + arc.center.y * segment.start.y) - arc.radius.pow(2)

    val discriminant = b.pow(2) - 4 * a * c
    if (discriminant >= 0) {
        val t1 = (-b + sqrt(discriminant)) / (2 * a)
        val t2 = (-b - sqrt(discriminant)) / (2 * a)

        val potentialIntersections = listOf(
            segment.start.lerp(segment.end, t1),
            segment.start.lerp(segment.end, t2)
        )

        // 2. Check Segment Boundaries
        // 3. Check Arc Boundaries
        for (intersection in potentialIntersections) {
            if (isPointOnSegment(intersection, segment.start, segment.end) &&
                arc.containsAngle(atan2(intersection.y - arc.center.y, intersection.x - arc.center.x))
            ) {
                intersections.add(intersection)
            }
        }
    }

    return intersections
}

class ArcManipulation {
}