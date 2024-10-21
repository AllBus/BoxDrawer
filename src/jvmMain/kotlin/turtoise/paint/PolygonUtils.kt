package turtoise.paint

import vectors.Vec2

object PolygonUtils {
    fun findClosestPointOnPolygon(polygon: List<Vec2>, point: Vec2): Vec2 {
        var closestPoint = polygon[0]
        var minDistance = Double.MAX_VALUE

        for (i in 1 until polygon.size) {
            val start = polygon[i-1]
            val end = polygon[i]

            val closestPointOnSegment = findClosestPointOnSegment(start, end, point)
            val distance = Vec2.distance(point, closestPointOnSegment)

            if (distance < minDistance) {
                minDistance = distance
                closestPoint = closestPointOnSegment
            }
        }

        return closestPoint
    }

// Helper function to find the closest point on a line segment
    private fun findClosestPointOnSegment(start: Vec2, end: Vec2, point: Vec2): Vec2 {
        val segmentVector = Vec2(end.x - start.x, end.y - start.y)
        val pointVector = Vec2(point.x - start.x, point.y - start.y)

        val dotProduct = Vec2.dot(segmentVector, pointVector)
        val segmentLengthSquared = segmentVector.x * segmentVector.x + segmentVector.y * segmentVector.y

        if (segmentLengthSquared == 0.0) {
            return start // Segment is a point
        }

        val t = dotProduct / segmentLengthSquared

        return if (t < 0) {
            start // Before the segment
        } else if (t > 1) {
            end // After the segment
        } else {
            Vec2(start.x + t * segmentVector.x, start.y + t * segmentVector.y) // On the segment
        }
    }
}