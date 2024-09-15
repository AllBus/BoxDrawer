package com.kos.boxdrawer.detal.grid

object GridLoops {
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
}