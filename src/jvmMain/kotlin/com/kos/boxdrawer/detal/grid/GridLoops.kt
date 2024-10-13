package com.kos.boxdrawer.detal.grid

import vectors.BoundingRectangle
import vectors.Vec2
import kotlin.math.min

object GridLoops {

    fun arrangePolygons(polygons: List<PolyInfo>): PolyLayer {
        val groupedPolygons = groupPolygons(polygons)
        println("a ${groupedPolygons.size}")
        val arrangedGroups = groupedPolygons.map { arrangeGroup2(it.value) }
        val stackedGroups = stackGroups2(arrangedGroups)
        return stackedGroups
    }

    fun groupPolygons(polygons: List<PolyInfo>): Map<Pair<Int, Plane>, List<PolyInfo>> {
        println("group ${polygons.size}")
        val grouped = mutableMapOf<Pair<Int, Plane>, MutableList<PolyInfo>>()
        for (polygon in polygons) {
            val key = Pair(polygon.plane, polygon.orientation)
            grouped.getOrPut(key) { mutableListOf() }.add(polygon)
        }
        return grouped
    }


    fun stackGroups2( layers : List<PolyLayer>):PolyLayer{
        var offset = Vec2.Zero

        val rest = mutableListOf<List<Vec2>>(emptyList())
        for (l in layers){
            rest+=l.polygons.map { p ->
                p.map {  v -> v +  offset }
            }

            offset +=  Vec2(0.0, l.bounds.height+1.0)
        }
        return PolyLayer(rest , BoundingRectangle.Empty)
    }

    fun arrangeGroup2(polygons: List<PolyInfo>): PolyLayer {
        val mp = polygons.map {  p ->
            p to getBoundingBox(p.points)
        }


        val mps = mp.sortedBy { p -> p.second.first }

        val vx = mps.minOf { t -> t.second.first }
        val vy = mps.minOf { t -> t.second.second }

        val v2 = Vec2(vx, vy)

        val pp = mps.map { t -> t.first.points.map { p -> p - v2 } }


        return PolyLayer(  pp, flattenBoundingBoxes(mp.map { it.second })  )
    }


    fun findClosedLoops(edges: Set<LongEdge>): Set<Loop> {
        val loops = mutableSetOf<Loop>()
        val used = mutableSetOf<Coordinates>()

        val edgeMap = edges.flatMap { listOf(it.start to Pair(it.end, it), it.end to Pair(it.start, it)) }
            .groupBy { it.first }
            .mapValues { it.value.map { it.second }.toSet() }

        for ((startPoint, connectedPoints) in edgeMap) {
            if (startPoint in used)
                continue

            val (endPoint, firstEdge) = connectedPoints.first()

            val loopPoints = mutableListOf(startPoint, endPoint)
            val loopEdges = mutableListOf<LongEdge>()
            loopEdges.add(firstEdge)

            var lastPoint = endPoint
            var pred = loopPoints.first()

            used.add(startPoint)
            used.add(endPoint)
            while (true) {
                val p = edgeMap[lastPoint]?.find { it.first != pred }
                if (p != null) {
                val (nextPoint, nextEdge) =  p
                    loopEdges.add(nextEdge)
                    loopPoints.add(nextPoint)
                    used.add(nextPoint)
                    pred = lastPoint
                    lastPoint = nextPoint

                    if (nextPoint == startPoint) {
                        if (loopPoints.size > 2) {
                            loops.add(Loop(loopPoints.toList(), loopEdges.toList()))
                        }
                        break
                    }
                } else {
                    break
                }
            }
        }
        return loops
    }


    fun arrangePolygons(polygons: List<PolygonVec>, sideTouches: List<SideTouch>): List<List<Vec2>> {
        val graph = buildGraph(polygons.size, sideTouches)
        val cycles = findCycles(graph)

        val pieces = mutableListOf<List<Vec2>>()
        for (cycle in cycles) {
            val piece = arrangeCycle(polygons, sideTouches, cycle)
            pieces.add(piece)
        }

        return pieces
    }

    fun buildGraph(numPolygons: Int, sideTouches: List<SideTouch>): List<MutableSet<Int>> {
        val graph = List(numPolygons) { mutableSetOf<Int>() }
        for (touch in sideTouches) {
            graph[touch.polygon1Index].add(touch.polygon2Index)
            graph[touch.polygon2Index].add(touch.polygon1Index)
        }
        return graph
    }

    fun findCycles(graph: List<MutableSet<Int>>): List<List<Int>> {
        val cycles = mutableListOf<List<Int>>()
        val visited = mutableSetOf<Int>()
        val stack = mutableListOf<Int>()

        fun dfs(node: Int, parent: Int) {
            visited.add(node)
            stack.add(node)

            for (neighbor in graph[node]) {
                if (neighbor != parent) {
                    if (neighbor in stack) {
                        val cycle = stack.subList(stack.indexOf(neighbor), stack.size)
                        cycles.add(cycle)
                    } else if (neighbor !in visited) {
                        dfs(neighbor, node)
                    }
                }
            }

            stack.remove(node)
        }

        for (node in graph.indices) {
            if (node !in visited) {
                dfs(node, -1)
            }
        }

        return cycles
    }

    fun arrangeCycle(polygons: List<PolygonVec>, sideTouches: List<SideTouch>, cycle: List<Int>): List<Vec2> {
        val piece = mutableListOf<Vec2>()
        var currentPolygon = polygons[cycle[0]]
        piece.addAll(currentPolygon.points) // Add first polygon

        for (i in 1 until cycle.size) {
            val nextPolygonIndex = cycle[i]
            val touch = sideTouches.find {
                (it.polygon1Index == cycle[i - 1] && it.polygon2Index == nextPolygonIndex) ||
                        (it.polygon1Index == nextPolygonIndex && it.polygon2Index == cycle[i - 1])
            }!! // Assuming touch is always found

            val nextPolygon = polygons[nextPolygonIndex]
            val sharedSide = if (touch.polygon1Index == cycle[i - 1]) {
                currentPolygon.sides[touch.side1Index]
            } else {
                nextPolygon.sides[touch.side2Index]
            }

            val (p1, p2) = sharedSide
            val currentVec2 = currentPolygon.points
            val nextVec2 = nextPolygon.points
            val currentStartIndex = currentVec2.indexOf(p1)
            val nextStartIndex = nextVec2.indexOf(p1)

            val alignedNext = if (currentVec2[(currentStartIndex + 1) % currentVec2.size] == p2) {
                nextVec2.subList(nextStartIndex, nextVec2.size) + nextVec2.subList(0, nextStartIndex)
            } else {
                nextVec2.subList(nextStartIndex, nextVec2.size).asReversed() + nextVec2.subList(0, nextStartIndex).asReversed()
            }

            for (point in alignedNext) {
                if (point !in piece) {
                    piece.add(point)}
            }

            currentPolygon = nextPolygon
        }

        return piece
    }



    fun createPolygonTree(polygons: List<PolyInfo>): List<PolygonNode> {
        val roots = mutableListOf<PolygonNode>()
        val polygonNodes = polygons.map { PolygonNode(it) }

        fun buildTree(node: PolygonNode, remainingNodes: List<PolygonNode>) {
            val (children, newRemainingNodes) = remainingNodes.partition { otherNode ->
                polygonInPolygon(otherNode.polyInfo.points, node.polyInfo.points)
            }

            node.children.addAll(children)
            for (child in children) {
                buildTree(child, newRemainingNodes)
            }
        }

        for (node in polygonNodes) {
            buildTree(node, polygonNodes - node)
            if (node.children.isEmpty()) {
                roots.add(node)
            }
        }

        return roots
    }

    fun arrangeGroup(polygons: List<PolyInfo>): PolyLayer {
        val placedPolygons = mutableListOf<Pair<PolyInfo, List<Vec2>>>()
        var minX = Double.MAX_VALUE
        var minY = Double.MAX_VALUE
        var maxX = Double.MIN_VALUE
        var maxY = Double.MIN_VALUE

        val remainingPolygons = polygons.toMutableList()

        while (remainingPolygons.isNotEmpty()) {
            var bestPlacement: Pair<PolyInfo, List<Vec2>>? = null
            var bestArea =Double.MAX_VALUE
            var isNested = false

            for (polygon in remainingPolygons) {
                val polyVec2 = polygon.points
                if (placedPolygons.isEmpty()) {
                    bestPlacement = Pair(polygon, polyVec2)
                    break
                }

                var currentPlacement: Pair<List<Vec2>, Vec2>? = null
                var currentArea = Double.MAX_VALUE
                var currentIsNested = false

                for (placedPolygon in placedPolygons) {
                    if (polygonInPolygon(polyVec2, placedPolygon.second)) {
                        val offset = placedPolygon.second[0] - polyVec2[0]
                        val translated = polyVec2.map { it + offset }
                        currentPlacement = Pair(translated, offset)
                        currentIsNested = true
                        break
                    } else if (polygonInPolygon(placedPolygon.second, polyVec2)) {
                        val offset = polyVec2[0] - placedPolygon.second[0]
                        val translated = placedPolygon.second.map { it + offset }
                        placedPolygons.remove(placedPolygon)
                        placedPolygons.add(Pair(placedPolygon.first, translated))
                        currentPlacement = Pair(polyVec2, Vec2(0.0, 0.0))
                        currentIsNested = true
                        break
                    } else {
                        for (i in polyVec2.indices) {
                            val translated = polyVec2.map { it + (placedPolygon.second[0] - polyVec2[i]) }
                            if (!polygonsOverlap(placedPolygons.map { it.second }, translated)) {
                                val (newMinX, newMinY, newMaxX, newMaxY) = getBoundingBox(placedPolygons.flatMap { it.second } + translated)
                                val area = (newMaxX - newMinX) * (newMaxY - newMinY)
                                if (area < currentArea) {
                                    currentArea = area
                                    currentPlacement = Pair(translated, placedPolygon.second[0] - polyVec2[i])
                                }
                            }
                        }
                    }
                }

                if (currentPlacement != null && currentArea < bestArea) {
                    bestPlacement = Pair(polygon, currentPlacement.first)
                    bestArea = currentArea
                    isNested = currentIsNested
                }
            }

            if (bestPlacement != null) {
                placedPolygons.add(bestPlacement)
                remainingPolygons.remove(bestPlacement.first)
                if (!isNested) {
                    for (point in bestPlacement.second) {
                        minX = minOf(minX, point.x)
                        minY = minOf(minY, point.y)
                        maxX = maxOf(maxX, point.x)
                        maxY = maxOf(maxY, point.y)
                    }
                }
            } else {
                break
            }
        }

        val points = placedPolygons.flatMap { it.second }
        val bounds = BoundingRectangle(Vec2(minX, minY), Vec2(maxX, maxY))
        return PolyLayer(listOf(points), bounds)
    }

    fun polygonsOverlap(placedPolygons: List<List<Vec2>>, newPolygon: List<Vec2>): Boolean {
        val (newMinX, newMinY, newMaxX, newMaxY) = getBoundingBox(newPolygon)
        for (placedPolygon in placedPolygons) {
            val (placedMinX, placedMinY, placedMaxX, placedMaxY) = getBoundingBox(placedPolygon)
            if (newMinX < placedMaxX && newMaxX > placedMinX && newMinY < placedMaxY && newMaxY > placedMinY) {
                return true // Overlap found
            }
        }

        return false // No overlap with any placed polygon
    }


    fun polygonsOverlap1(poly1: List<Vec2>, poly2: List<Vec2>): Boolean {
        val (minX1,minY1, maxX1, maxY1) = getBoundingBox(poly1)
        val (minX2, minY2, maxX2, maxY2) = getBoundingBox(poly2)

        return minX1 < maxX2 && maxX1 > minX2 && minY1 < maxY2 && maxY1> minY2
    }

    fun getBoundingBox(points: List<Vec2>): Quadruple<Double, Double, Double, Double> {
        var minX = Double.MAX_VALUE
        var minY = Double.MAX_VALUE
        var maxX = Double.MIN_VALUE
        var maxY = Double.MIN_VALUE

        for (point in points) {
            minX = minOf(minX, point.x)
            minY = minOf(minY, point.y)
            maxX = maxOf(maxX, point.x)
            maxY = maxOf(maxY, point.y)
        }

        return Quadruple(minX, minY, maxX, maxY)
    }

    fun flattenBoundingBoxes(boxes: List<Quadruple<Double, Double, Double, Double>>): BoundingRectangle {
        if (boxes.isEmpty()) {
            return BoundingRectangle.Empty
        }

        var minX = boxes[0].first
        var minY= boxes[0].second
        var maxX = boxes[0].third
        var maxY = boxes[0].fourth

        for (box in boxes.drop(1)) {
            minX = minOf(minX, box.first)
            minY = minOf(minY, box.second)
            maxX = maxOf(maxX, box.third)
            maxY = maxOf(maxY, box.fourth)
        }

        return BoundingRectangle(Vec2(minX, minY), Vec2(maxX, maxY))
    }

    fun stackGroups(groups: List<PolyLayer>): PolyLayer {
        val placedGroups = mutableListOf<Pair<PolyLayer, Vec2>>()
        var minX = Double.MAX_VALUE
        var minY = Double.MAX_VALUE
        var maxX = Double.MIN_VALUE
        var maxY = Double.MIN_VALUE

        val remainingGroups = groups.toMutableList()

        while (remainingGroups.isNotEmpty()){
            var bestPlacement: Pair<PolyLayer, Vec2>? = null
            var bestArea = Double.MAX_VALUE

            for (group in remainingGroups) {
                if (placedGroups.isEmpty()) {
                    placedGroups.add(Pair(group, Vec2(0.0, 0.0)))
                    minX = group.bounds.min.x
                    minY = group.bounds.min.y
                    maxX = group.bounds.max.x
                    maxY = group.bounds.max.y
                    break
                }

                var currentPlacement: Vec2? = null
                var currentArea = Double.MAX_VALUE

                for (placedGroup in placedGroups) {
                    val (placedGroupLayer, placedGroupOffset) = placedGroup

                    // Try placing above
                    val translateUp = Vec2(0.0, placedGroupLayer.bounds.max.y - placedGroupLayer.bounds.min.y)
                    val newOffsetUp = placedGroupOffset + translateUp
                    if (!groupsOverlap(placedGroups.map { it.first }, group, newOffsetUp)) {
                        val (newMinX, newMinY, newMaxX, newMaxY) = getBoundingBox(placedGroups.map { it.first } + group, newOffsetUp)
                        val area = (newMaxX - newMinX) * (newMaxY - newMinY)
                        if (area < currentArea) {
                            currentArea = area
                            currentPlacement = newOffsetUp
                        }
                    }

                    // Try placing to the right
                    val translateRight = Vec2(placedGroupLayer.bounds.max.x - placedGroupLayer.bounds.min.x, 0.0)
                    val newOffsetRight = placedGroupOffset + translateRight
                    if (!groupsOverlap(placedGroups.map { it.first }, group, newOffsetRight)) {
                        val (newMinX, newMinY, newMaxX, newMaxY) = getBoundingBox(placedGroups.map { it.first } + group, newOffsetRight)
                        val area = (newMaxX - newMinX) * (newMaxY - newMinY)
                        if (area < currentArea) {
                            currentArea = area
                            currentPlacement = newOffsetRight
                        }
                    }
                }

                if (currentPlacement != null && currentArea < bestArea) {
                    bestPlacement = Pair(group, currentPlacement)
                    bestArea = currentArea
                }
            }

            if (bestPlacement != null) {
                placedGroups.add(bestPlacement)
                remainingGroups.remove(bestPlacement.first)
                val (newMinX, newMinY, newMaxX, newMaxY) = getBoundingBox(placedGroups.map { it.first }, bestPlacement.second)
                minX = newMinX
                minY = newMinY
                maxX = newMaxX
                maxY = newMaxY
            } else {
                // Handle case where no placement is found for any remaining group (e.g., try different strategies)
                break
            }
        }

        val allPolygons = placedGroups.flatMap { (group, offset) ->
            group.polygons.map { polygon ->
                polygon.map { point -> point + offset }
            }
        }
        val bounds = BoundingRectangle(Vec2(minX, minY), Vec2(maxX, maxY))
        return PolyLayer(allPolygons, bounds)
    }

    fun groupsOverlap(placedGroups: List<PolyLayer>, newGroup: PolyLayer, newOffset: Vec2): Boolean {
        for (placedGroup in placedGroups) {
            val placedMinX = placedGroup.bounds.min.x
            val placedMinY = placedGroup.bounds.min.y
            val placedMaxX = placedGroup.bounds.max.x
            val placedMaxY = placedGroup.bounds.max.y

            val newMinX = newGroup.bounds.min.x
            val newMinY = newGroup.bounds.min.y
            val newMaxX = newGroup.bounds.max.x
            val newMaxY = newGroup.bounds.max.y


            if (placedMinX + newOffset.x < placedMaxX && placedMaxX > newMinX + newOffset.x &&
                placedMinY+ newOffset.y < placedMaxY && placedMaxY > newMinY + newOffset.y) {
                return true
            }
        }
        return false
    }

    fun getBoundingBox(groups: List<PolyLayer>, offset: Vec2 = Vec2(0.0, 0.0)): Quadruple<Double, Double, Double, Double> {
        var minX = Double.MAX_VALUE
        var minY = Double.MAX_VALUE
        var maxX = Double.MIN_VALUE
        var maxY = Double.MIN_VALUE

        for (group in groups) {
            minX = minOf(minX, group.bounds.min.x + offset.x)
            minY = minOf(minY, group.bounds.min.y + offset.y)
            maxX = maxOf(maxX, group.bounds.max.x + offset.x)
            maxY = maxOf(maxY, group.bounds.max.y + offset.y)
        }

        return Quadruple(minX, minY, maxX, maxY)
    }

    fun polygonInPolygon(inner: List<Vec2>, outer: List<Vec2>): Boolean {
        if (inner.isEmpty()|| outer.isEmpty()) {
            return false
        }

        for (point in inner) {
            if (!pointInPolygon(point, outer)) {
                return false
            }
        }

        return true
    }

    fun pointInPolygon(point: Vec2, polygon:List<Vec2>): Boolean {
        if (polygon.size < 3) {
            return false
        }

        var intersections = 0
        val ray = Vec2(point.x, Double.MAX_VALUE) // Ray pointing upwards

        for (i in polygon.indices) {
            val p1 = polygon[i]
            val p2 = polygon[(i + 1) % polygon.size]

            if (rayIntersectsSegment(point, ray, p1, p2)) {
                intersections++
            }
        }

        return intersections % 2 != 0 // Odd number of intersections means point is inside
    }

    fun rayIntersectsSegment(point: Vec2, ray: Vec2, p1: Vec2, p2: Vec2): Boolean {
        // Check if the ray and segment are collinear
        if ((p1.x - point.x) * (ray.y - point.y) == (ray.x - point.x) * (p1.y - point.y)) {
            return false
        }

        // Check if the ray intersects the segment
        val t = ((p1.x - point.x) * (p2.y - p1.y) - (p2.x - p1.x) * (p1.y - point.y)) /
                ((ray.x - point.x) * (p2.y - p1.y) - (p2.x - p1.x) * (ray.y - point.y))
        val u = ((p1.x - point.x) * (ray.y - point.y) - (ray.x - point.x) * (p1.y - point.y)) /
                ((ray.x - point.x) * (p2.y - p1.y) - (p2.x - p1.x) * (ray.y - point.y))

        return t > 0 && u > 0 && u < 1
    }

}

data class SideTouch(val polygon1Index: Int, val side1Index: Int, val polygon2Index: Int, val side2Index: Int)

data class PolygonVec(val points: List<Vec2>){
    val sides: List<Pair<Vec2, Vec2>>
        get() = points.zipWithNext() + Pair(points.last(), points.first())
}

data class Quadruple<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

data class PolygonNode(val polyInfo: PolyInfo, val children: MutableList<PolygonNode> = mutableListOf())