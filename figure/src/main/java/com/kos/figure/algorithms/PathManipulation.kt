package com.kos.figure.algorithms

import com.kos.figure.complex.model.Arc
import com.kos.figure.complex.model.Curve
import com.kos.figure.complex.model.PathElement
import com.kos.figure.complex.model.Segment
import vectors.Vec2

data class IntersectionInfo(val point: Vec2, val element1: PathElement, val element2: PathElement)

data class IntersectionVertex(val point: Vec2, val connectedEdges: MutableList<PathElement> = mutableListOf())

fun findPathIntersections(path1: List<PathElement>, path2: List<PathElement>): List<IntersectionInfo> {
    val intersections = mutableListOf<IntersectionInfo>()

    for (element1 in path1) {
        for (element2 in path2) {
            when {
                element1 is Segment && element2 is Segment -> {
                    // Calculate segment-segment intersections
                    val intersection = findLineIntersection(element1.start, element1.end, element2.start, element2.end)
                    if (intersection != null) intersections.add(IntersectionInfo( intersection, element1, element2))
                }
                element1 is Segment && element2 is Arc -> {
                    val intersection =  findSegmentArcIntersections(element1, element2)
                    intersections.addAll(intersection.map { IntersectionInfo(it, element1, element2) })
                }
                element1 is Segment && element2 is Curve -> {
                    val intersection =  findSegmentBezierIntersections(element1, element2)
                    intersections.addAll(intersection.map { IntersectionInfo(it, element1, element2) })
                }
                element1 is Arc && element2 is Segment -> {
                    val intersection =  findSegmentArcIntersections(element2, element1)
                    intersections.addAll(intersection.map { IntersectionInfo(it, element1, element2) })
                }
                element1 is Arc && element2 is Arc -> {
                    val intersection = findArcIntersections(element1, element2)
                    intersections.addAll(intersection.map { IntersectionInfo(it, element1, element2) })
                }
                element1 is Arc && element2 is Curve -> {
                    val intersection = findBezierArcIntersections(element2, element1)
                    intersections.addAll(intersection.map { IntersectionInfo(it, element1, element2) })
                }
                element1 is Curve && element2 is Segment -> {
                    val intersection =  findSegmentBezierIntersections(element2, element1)
                    intersections.addAll(intersection.map { IntersectionInfo(it, element1, element2) })
                }
                element1 is Curve && element2 is Arc -> {
                    val intersection = findBezierArcIntersections(element1, element2)
                    intersections.addAll(intersection.map { IntersectionInfo(it, element1, element2) })
                }
                element1 is Curve && element2 is Curve -> {
                    val intersection = findBezierIntersections(element1, element2, 0.001)
                    intersections.addAll(intersection.map { IntersectionInfo(it, element1, element2) })
                }
            }
        }
    }

    return intersections
}



fun createIntersectionPath(path1: List<PathElement>, path2: List<PathElement>): List<Vec2> {
    // 1. Find Intersections
    val intersectionInfos = findPathIntersections(path1, path2)

    // 2. Build Graph
    val graph = buildIntersectionGraph(intersectionInfos)

    // 3. Traverse Graph (starting from an arbitrary intersection point)
    val startVertex = graph.keys.firstOrNull() ?: return emptyList() //Handle case with no intersections
    return traverseIntersectionGraph(graph, startVertex)
}

fun buildIntersectionGraph(intersectionInfos: List<IntersectionInfo>): Map<IntersectionVertex, List<IntersectionVertex>> {
    val graph = mutableMapOf<IntersectionVertex, MutableList<IntersectionVertex>>()

    // Create vertices for intersection points
    for (info in intersectionInfos) {
        graph[IntersectionVertex(info.point)] = mutableListOf()
    }

    // Connect vertices based on path segments
    for (i in intersectionInfos.indices) {
        for (j in i + 1 until intersectionInfos.size) {
            val info1 = intersectionInfos[i]
            val info2 = intersectionInfos[j]

            // Check if the two intersections share a common path element
            if (info1.element1 == info2.element1 || info1.element1 == info2.element2 ||
                info1.element2 == info2.element1 || info1.element2 == info2.element2
            ) {
                // Connect the corresponding vertices in the graph
                val vertex1 = graph.keys.find { it.point == info1.point }!!
                val vertex2 = graph.keys.find { it.point == info2.point }!!
                graph[vertex1]?.add(vertex2)
                graph[vertex2]?.add(vertex1)

                // Store connected edges for traversal (optional, but useful)
                vertex1.connectedEdges.add(if (info1.element1 == info2.element1 || info1.element1 == info2.element2) info1.element1 else info1.element2)
                vertex2.connectedEdges.add(if (info2.element1 == info1.element1 || info2.element1 == info1.element2) info2.element1 else info2.element2)
            }
        }
    }

    return graph
}

fun traverseIntersectionGraph(
    graph: Map<IntersectionVertex, List<IntersectionVertex>>,
    startVertex: IntersectionVertex
): List<Vec2> {
    val result = mutableListOf<Vec2>()
    val visited = mutableSetOf<IntersectionVertex>()
    var currentVertex = startVertex

    do {
        result.add(currentVertex.point)
        visited.add(currentVertex)

        // Choose the next unvisited vertex connected by the shortest edge (simple strategy)
        val nextVertex = currentVertex.connectedEdges
            .flatMap { edge ->
                graph[currentVertex]?.filter { it != currentVertex && !visited.contains(it) && it.connectedEdges.contains(edge) } ?: emptyList()
            }
            .minByOrNull { (it.point - currentVertex.point).magnitude }

        currentVertex = nextVertex ?:break // Stop if there are no more unvisited connected vertices
    } while (currentVertex != startVertex) // Stop when we return to the starting point

    return result
}

class PathManipulation {
}