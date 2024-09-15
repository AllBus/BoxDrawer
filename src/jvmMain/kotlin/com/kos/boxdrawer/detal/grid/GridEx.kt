package com.kos.boxdrawer.detal.grid

object GridEx {
    fun findClosedLoopsV2(edges: Set<LongEdge>): Set<List<Coordinates>> {
        val loops = mutableSetOf<List<Coordinates>>()
        val used = mutableSetOf<Coordinates>()

        val edgeMap = edges.flatMap { listOf(it.start to it.end, it.end to it.start) }.groupBy { it.first }.mapValues { it.value.map { it.second }.toSet() }

        for ((startPoint, connectedPoints) in edgeMap) {
            if (startPoint in used)
                continue

            val endPoint = connectedPoints.first()

            val loop = mutableListOf(startPoint, endPoint)
            var lastPoint = endPoint
            var pred = loop.first()

            used.add(startPoint)
            used.add(endPoint)
            while (true) {
                val nextPoint = edgeMap[lastPoint]?.find { it != pred }
                if (nextPoint != null) {
                    used.add(nextPoint)
                    loop.add(nextPoint)
                    pred = lastPoint
                    lastPoint = nextPoint

                    if (nextPoint == startPoint) {
                        if (loop.size > 2) {
                            loops.add(loop.toList())
                        }
                        break
                    }
                } else {
                    break
                }
            }

        }
        //    println("loops ${loops.size}")
        return loops
    }

    fun findClosedLoopsVarA(edges: Set<LongEdge>): Set<List<Coordinates>> {
        val loops = mutableSetOf<List<Coordinates>>()
        val usedEdges = mutableSetOf<LongEdge>()
        val edgeMap = edges.flatMap { listOf(it.start to it, it.end to it) }.groupBy { it.first }.mapValues { it.value.map { it.second } }


        fun findNextEdge(last: Coordinates): LongEdge? {
            return edges.find { nextEdge ->
                nextEdge !in usedEdges && (
                        (nextEdge.start == last) || (nextEdge.end == last)
                        )
            }
        }

        for (edge in edges) {
            if (edge !in usedEdges) {
                val lf = edge.start
                val loop = mutableListOf(edge.start, edge.end)
                usedEdges.add(edge)

                while (true) {
                    val ll = loop.last()
                    val nextEdge = findNextEdge(ll)

                    if (nextEdge != null) {
                        val ln: Coordinates = if (nextEdge.start == ll) {
                            nextEdge.end
                        } else {
                            nextEdge.start
                        }
                        loop.add(ln)
                        usedEdges.add(nextEdge)

                        if (ln == lf) {
                            // Closed loop found
                            if (loop.size > 2) { // Add this condition to avoid degenerate loops
                                loops.add(loop.toList()) // Add a copyof the loop to avoid modification issues
                            }
                            break
                        }
                    } else {
                        // No closed loop found for this starting edge
                        break
                    }
                }
            }
        }
        return loops
    }
}