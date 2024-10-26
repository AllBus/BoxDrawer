package com.kos.boxdrawer.detal.grid

object Grid3dUtils {

    /** Получить стороны по плоскостям */
    fun edgesInPlanes(longEdges: Set<KubikEdge>): Map<Plane,Set<KubikEdge>>{
        val planeXY = mutableSetOf<KubikEdge>()
        val planeYZ = mutableSetOf<KubikEdge>()
        val planeXZ = mutableSetOf<KubikEdge>()

        for (edge in longEdges) {
            if (edge.start.x == edge.end.x) planeYZ.add(edge)
            if (edge.start.y == edge.end.y) planeXZ.add(edge)
            if (edge.start.z == edge.end.z) planeXY.add(edge)
        }

        return mapOf(
            Plane.XY to planeXY,
            Plane.YZ to planeYZ,
            Plane.XZ to planeXZ
        )
    }

    fun findIntersection(polygon:Polygon): Set<Polygon> {
        val intersections = mutableSetOf<Polygon>()
        val pv = polygon.vertices
        val hasElement = mutableSetOf<Coordinates>()
        val currentSeq = mutableListOf<Coordinates>()
     //   println(pv.joinToString("+"))
        for (i in pv.indices) {
            val p1 = pv[i]
            if (hasElement.contains(p1)){
                var j = currentSeq.size-1
                val newSeq = mutableListOf<Coordinates>()
                newSeq.add(pv[i])
                while (j>= 0 && currentSeq[j]!= p1){
                    val pj = currentSeq[j]
                    newSeq.add(pj)
                    hasElement.remove(pj)
                    currentSeq.removeLast()
                    j--
                }
                newSeq.add(pv[i])

     //           println(newSeq.reversed().joinToString(" "))
                intersections.add(Polygon(newSeq.reversed()))

            } else {
                hasElement.add(p1)
                currentSeq.add(p1)
            }
        }
        return intersections
    }

    fun createPolygon(edges:Set<KubikEdge>): Set<Polygon> {
        return GridLoops.findClosedLoops(edges).map { loop ->
            Polygon(loop.points)
        }.toSet()
    }

    /** Todo: Здесь ошибка. Соеднияет стороны которые могут быть ищ разных углов в случае если кубики лежат по диагонали*/
    fun convertToLongEdges(edges: Set<KubikEdge>): Set<KubikEdge> {
        val longEdges = mutableSetOf<KubikEdge>()

        // Divide edges into directions (x, y, z) using the direction property
        val xEdges = edges.filter { it.direction == Direction.X }
        val yEdges = edges.filter { it.direction == Direction.Y }
        val zEdges = edges.filter { it.direction == Direction.Z }

        longEdges.addAll(processEdgesInDirection(xEdges))
        longEdges.addAll(processEdgesInDirection(yEdges))
        longEdges.addAll(processEdgesInDirection(zEdges))

        return longEdges
    }


    private fun processEdgesInDirection(
        edges: List<KubikEdge>,
    ): Set<KubikEdge> {
        val longEdges = mutableSetOf<KubikEdge>()
        val processedEdges = mutableSetOf<KubikEdge>()

        for (edge in edges) {
            if (edge !in processedEdges) {
                val connectedEdges = mutableListOf(edge.start, edge.end)
                processedEdges.add(edge)

                var hasChanged = true
                while (hasChanged) {
                    hasChanged = false
                    for (otherEdge in edges - processedEdges) {
                        if (isCollinearAndConnects(edge, otherEdge, connectedEdges)) {
                            processedEdges.add(otherEdge)
                            hasChanged = true
                        }
                    }
                }

                longEdges.add(KubikEdge(connectedEdges.first(), connectedEdges.last()))
            }
        }

        return longEdges
    }


    private fun isCollinearAndConnects(
        edge: KubikEdge,
        otherEdge: KubikEdge,
        connectedEdges: MutableList<Coordinates>
    ): Boolean {
        if (!edge.isCollinearWith(otherEdge)) return false

        return when (edge.direction) {
            Direction.X -> {
                if (otherEdge.start.x == connectedEdges.last().x) {
                    connectedEdges.add(otherEdge.end)
                    true
                } else if (otherEdge.end.x == connectedEdges.first().x) {
                    connectedEdges.add(0, otherEdge.start)
                    true
                } else false
            }
            Direction.Y -> {
                if (otherEdge.start.y == connectedEdges.last().y) {
                    connectedEdges.add(otherEdge.end)
                    true
                } else if (otherEdge.end.y == connectedEdges.first().y) {
                    connectedEdges.add(0, otherEdge.start)
                    true
                } else false
            }
            Direction.Z -> {
                if (otherEdge.start.z == connectedEdges.last().z) {
                    connectedEdges.add(otherEdge.end)
                    true
                } else if (otherEdge.end.z == connectedEdges.first().z) {
                    connectedEdges.add(0, otherEdge.start)
                    true
                } else false
            }
        }
    }

}