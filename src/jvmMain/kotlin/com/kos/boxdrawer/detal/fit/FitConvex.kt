package com.kos.boxdrawer.detal.fit

import vectors.Vec2

object FitConvex {
//    fun packConvexPolygons(polygons: List<ConvexPolygon>): Rectangle {
//        val sortedPolygons = polygons.sortedByDescending { it.area } // Sort by area
//        var boundingBox = Rectangle(0.0, 0.0) // Initial bounding boxval placements = mutableMapOf<ConvexPolygon, Placement>()
//
//        for (polygon in sortedPolygons) {
//            var bestPlacement: Placement? = null
//            var bestBoundingBox = boundingBox
//            var minWastedArea = Double.MAX_VALUE
//
//            // Try different positions and rotations
//            for (rotation in 0..360 step 10) { // Example: rotate in 10-degree increments
//                val rotatedPolygon = polygon.rotate(Math.toRadians(rotation.toDouble()))
//                for ((placedPolygon, placement) in placements) {
//                    val nfp = calculateNFP(rotatedPolygon, placedPolygon)
//                    for (point in nfp.vertices) {
//                        val position = placement.position - point
//                        if (fits(rotatedPolygon, position, Math.toRadians(rotation.toDouble()), placements)) {
//                            val newBoundingBox = boundingBox(placements + (polygon to Placement(position, Math.toRadians(rotation.toDouble()))))
//                            val wastedArea = area(newBoundingBox) - area(boundingBox) - polygon.area
//                            if (wastedArea < minWastedArea) {
//                                minWastedArea = wastedArea
//                                bestPlacement = Placement(position, Math.toRadians(rotation.toDouble()))
//                                bestBoundingBox = newBoundingBox
//                            }
//                        }
//                    }
//                }
//            }
//
//            // If no fit, expand bounding box
//            if (bestPlacement == null) {
//                val position = Vec2(boundingBox.width, 0.0) // Example: place to the right
//                bestPlacement = Placement(position, 0.0)
//                bestBoundingBox = Rectangle(boundingBox.width + polygon.width, maxOf(boundingBox.height, polygon.height))}
//
//            placements[polygon] = bestPlacement!!
//            boundingBox = bestBoundingBox
//        }
//
//        return boundingBox
//    }
//
//    // Helper functions
//    fun calculateNFP(polygonA: ConvexPolygon, polygonB: ConvexPolygon): ConvexPolygon {
//        val nfpVertices = mutableListOf<Vec2>()
//        // 1. Initialization (find initial contact point and edge)
//        // ...
//
//        // 2. Orbiting
//        var currentVertexA = polygonA.vertices[0] // Example reference point
//        var currentEdgeB = findInitialContactEdge(polygonA, polygonB, currentVertexA)
//        while (/* haven't returned to the starting position */) {
//            // Slide along currentEdgeB, update currentVertexA if needed
//            //...
//            // Find the next edge of B to contact
//            currentEdgeB = findNextContactEdge(polygonA, polygonB, currentVertexA, currentEdgeB)
//            // Add the contact point to nfpVertices
//            // ...
//        }
//
//        return ConvexPolygon(nfpVertices)
//    }
//
//    fun findNextContactEdge(polygonA: ConvexPolygon, polygonB: ConvexPolygon,
//                            currentVertexA: Vec2, currentEdgeB: Edge): Edge {
//
//        // 1. Get the next vertex of polygon B (following the current edge)val nextVertexB = currentEdgeB.end
//
//        // 2. Find candidate edges of A and B that could be in contact
//        val candidateEdgesA = findCandidateEdges(polygonA, currentVertexA, nextVertexB)
//        val candidateEdgesB = findCandidateEdges(polygonB, nextVertexB, currentVertexA)
//
//        // 3. Choose the edge with the minimum angle
//        return findMinimumAngleEdge(candidateEdgesA, candidateEdgesB, currentVertexA, nextVertexB)
//    }
//
//    fun findInitialContactEdge(polygonA: ConvexPolygon, polygonB: ConvexPolygon, referencePoint: Vec2): Edge {
//        for (edge in polygonB.edges) {
//            // 1. Calculate the outward normal of the edge
//            val normal = edge.outwardNormal
//
//            // 2. Project the reference point onto the normal
//            val projection = referencePoint.projectOnto(normal)
//
//            // 3. Check if the projection lies within the edge's segment
//            if (edge.containsPoint(projection)) {
//                // 4. Check if polygon A lies entirely "outside" the edge (using the normal)
//                if (polygonA.vertices.all { (it - projection).dot(normal) >= 0 }) {
//                    return edge
//                }
//            }
//        }
//        // If no suitable edge isfound, you might need to handle this case (e.g., polygons are overlapping)
//        throw IllegalStateException("No initial contact edge found.")
//    }
//
//
//    fun fits(polygon: ConvexPolygon, position: Vec2, rotation: Double, placements: Map<ConvexPolygon, Placement>): Boolean {
//        val transformedPolygon = polygon.rotate(rotation).translate(position)
//        for ((placedPolygon, placement) in placements) {
//            val placedTransformedPolygon = placedPolygon.rotate(placement.rotation).translate(placement.position)
//            val nfp = calculateNFP(transformedPolygon, placedTransformedPolygon)
//            // Check if the NFP contains the origin (0, 0) - indicates overlap
//            if (nfp.contains(Vec2(0.0, 0.0))) {
//                return false
//            }
//        }
//        return true
//    }

    fun boundingBox(placements: Map<ConvexPolygon,Placement>): Rectangle {
        if (placements.isEmpty()) {
            return Rectangle(0.0, 0.0)
        }

        var minX = Double.MAX_VALUE
        var minY = Double.MAX_VALUE
        var maxX = Double.MIN_VALUE
        var maxY = Double.MIN_VALUE

        for ((polygon, placement) in placements) {
            val transformedPolygon = polygon.rotate(placement.rotation).translate(placement.position)
            for (vertex in transformedPolygon.vertices) {
                minX = minOf(minX, vertex.x)
                minY = minOf(minY, vertex.y)
                maxX = maxOf(maxX, vertex.x)
                maxY = maxOf(maxY, vertex.y)
            }
        }

        return Rectangle(maxX - minX, maxY - minY)
    }


}