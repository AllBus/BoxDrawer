package com.kos.figure.algorithms

import vectors.Vec2

data class SegmentInfo(val polygonIndex: Int, val edgeIndex: Int)

data class Vertex(
    val point: Vec2,
    val isIntersection:Boolean,
    val entering: Boolean,
    val segments: MutableList<SegmentInfo> = mutableListOf()
)

fun intersectArbitraryPolygons(polygon1: List<Vec2>, polygon2: List<Vec2>): List<List<Vec2>> {
    // 1. Find Intersections
    val intersections = findIntersections(polygon1, polygon2)

    // 2. Build Graph
    val graph = buildGraph(polygon1, polygon2, intersections)

    // 3. Traverse Graph
    val intersectionPolygons = mutableListOf<List<Vec2>>()
    for (intersection in intersections) {
        val polygon = traverseGraph(graph, intersection)
        if (polygon.isNotEmpty()) {
            intersectionPolygons.add(polygon)
        }
    }

    // Return the list of intersection polygons (could be multiple)
    return intersectionPolygons
}

fun findIntersections(polygon1: List<Vec2>, polygon2: List<Vec2>): List<Vec2> {
    val intersections = mutableListOf<Vec2>()

    for (i in polygon1.indices) {
        val edge1Start = polygon1[i]
        val edge1End = polygon1[(i + 1) % polygon1.size]

        for (j in polygon2.indices) {
            val edge2Start = polygon2[j]
            val edge2End = polygon2[(j + 1) % polygon2.size]

            val intersection = findLineIntersection(edge1Start, edge1End, edge2Start, edge2End)
            if (intersection != null) {
                intersections.add(intersection)
            }
        }
    }

    return intersections
}

fun findLineIntersection(p1: Vec2, p2: Vec2, p3: Vec2, p4: Vec2): Vec2? {
    val denominator = (p4.y - p3.y) * (p2.x - p1.x) - (p4.x - p3.x) * (p2.y - p1.y)

    // Check if lines are parallel
    if (denominator == 0.0) return null

    val ua = ((p4.x - p3.x) * (p1.y - p3.y) - (p4.y - p3.y) * (p1.x - p3.x)) / denominator
    val ub = ((p2.x - p1.x) * (p1.y - p3.y) - (p2.y - p1.y) * (p1.x - p3.x)) / denominator

    // Check if intersection is within line segments
    if (ua in 0.0..1.0 && ub in 0.0..1.0) {
        val x = p1.x + ua * (p2.x - p1.x)
        val y = p1.y + ua * (p2.y - p1.y)
        return Vec2(x, y)
    }

    return null // No intersection within line segments
}

fun buildGraph(polygon1: List<Vec2>, polygon2: List<Vec2>, intersections: List<Vec2>): Map<Vertex, List<Vertex>> {
    val graph = mutableMapOf<Vertex, MutableList<Vertex>>()

    // Add polygon vertices to the graph
    fun addPolygonVertices(polygon: List<Vec2>, entering: Boolean) {for (i in polygon.indices) {
        val currentPoint = polygon[i]
        val nextPoint = polygon[(i + 1) % polygon.size]
        val vertex = Vertex(currentPoint, isIntersection = false, entering = entering)
        graph.getOrPut(vertex) { mutableListOf() }.add(Vertex(nextPoint, isIntersection = false, entering = entering))
    }
    }

    addPolygonVertices(polygon1, entering = true)
    addPolygonVertices(polygon2, entering = false)

    // Add intersection vertices and connectthem
    for (intersection in intersections) {
        val intersectionVertex = Vertex(intersection, isIntersection = true, entering= true)
        graph.put(intersectionVertex, mutableListOf())

        // Find edges connected to the intersection
        for (i in polygon1.indices) {
            val edgeStart = polygon1[i]
            val edgeEnd = polygon1[(i + 1) % polygon1.size]
            if (isPointOnSegment(intersection, edgeStart, edgeEnd)) {
                val segmentInfo = SegmentInfo(polygonIndex = 1, edgeIndex = i)
                intersectionVertex.segments.add(segmentInfo)
                graph.getOrPut(Vertex(edgeStart, isIntersection = false, entering = true)) { mutableListOf() }.add(intersectionVertex)
                graph.getValue(intersectionVertex).add(Vertex(edgeEnd, isIntersection = false, entering = true))
            }
        }

        for (i in polygon2.indices) {
            val edgeStart = polygon2[i]
            val edgeEnd = polygon2[(i + 1) % polygon2.size]
            if (isPointOnSegment(intersection, edgeStart, edgeEnd)) {
                val segmentInfo = SegmentInfo(polygonIndex = 2, edgeIndex = i)
                intersectionVertex.segments.add(segmentInfo)
                graph.getOrPut(Vertex(edgeStart, isIntersection = false,entering = false)) { mutableListOf() }.add(intersectionVertex)
                graph.getValue(intersectionVertex).add(Vertex(edgeEnd, isIntersection = false, entering = false))
            }
        }
    }

    return graph
}

// Helper function to check if a point lies on a line segment
fun isPointOnSegment(point: Vec2, start: Vec2, end: Vec2): Boolean {
    val crossProduct = (end.y - start.y) * (point.x - start.x) - (end.x - start.x) * (point.y - start.y)
    if (Math.abs(crossProduct) > 1e-6) return false // Not collinear

    val dotProduct = (point.x - start.x) * (end.x - start.x) + (point.y - start.y) * (end.y - start.y)
    if (dotProduct < 0) return false // Point is before start
    if (dotProduct > (end.x - start.x) * (end.x - start.x) + (end.y - start.y) * (end.y - start.y)) return false // Point is after end

    return true
}

fun traverseGraph(graph: Map<Vertex, List<Vertex>>, start:Vec2): List<Vec2> {
    val result = mutableListOf<Vec2>()
    var currentVertex = graph.keys.find { it.point == start && it.isIntersection } ?: return emptyList()

    do {
        result.add(currentVertex.point)
        val nextVertices = graph.getValue(currentVertex)

        // Choose the next vertex based on entering/leaving status
        currentVertex = if (currentVertex.entering) {
            nextVertices.find { !it.entering } ?: break // Switch to leaving edge
        } else {
            nextVertices.find { it.entering } ?: break // Switch to entering edge
        }
    } while (currentVertex.point != start) // Stop when we return to the starting point

    return result
}

fun traverseGraphForUnion(graph: Map<Vertex, List<Vertex>>, start: Vec2, followEntering: Boolean = true): List<Vec2> {
    val result = mutableListOf<Vec2>()
    var currentVertex = graph.keys.find { it.point == start && it.isIntersection } ?: return emptyList()

    do {
        result.add(currentVertex.point)
        val nextVertices = graph.getValue(currentVertex)

        // Choose the next vertex based on followEntering
        currentVertex = if (followEntering) {
            nextVertices.find { it.entering } ?: break //Follow entering edges
        } else {
            nextVertices.find { !it.entering } ?: break // Follow leaving edges
        }
    } while (currentVertex.point != start) // Stop when we return to the starting point

    return result
}

fun traverseGraphForSubtract(graph: Map<Vertex, List<Vertex>>, start: Vec2, subtractPolygonIndex: Int): List<Vec2> {
    val result = mutableListOf<Vec2>()
    var currentVertex = graph.keys.find { it.point == start && it.isIntersection } ?: return emptyList()

    do {
        result.add(currentVertex.point)
        val nextVertices = graph.getValue(currentVertex)

        // Choose the next vertex based on subtraction logic
        currentVertex = if (currentVertex.segments.any { it.polygonIndex == subtractPolygonIndex }){
            // Intersection belongs to the polygon being subtracted
            if (currentVertex.entering) {
                nextVertices.find { it.entering } ?: break // Continue along entering edge (inside subtracted polygon)
            } else {
                nextVertices.find { !it.entering } ?: break // Continue along leaving edge (outside subtracted polygon)
            }
        } else {
            // Intersection belongs to the main polygon
            if (currentVertex.entering) {
                nextVertices.find { !it.entering } ?: break // Switch to leaving edge (outside subtracted polygon)
            } else {
                nextVertices.find { it.entering } ?: break // Switch to entering edge (inside subtracted polygon)
            }
        }
    } while (currentVertex.point != start) // Stop when we return to the starting point

    return result
}


class PolygonManipulation {
}