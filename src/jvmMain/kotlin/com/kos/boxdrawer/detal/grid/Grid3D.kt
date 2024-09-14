package com.kos.boxdrawer.detal.grid

import androidx.compose.ui.graphics.Path

data class Kubik(val color: Int)

data class Coordinates(val x: Int, val y: Int, val z: Int)

data class Grid3D(val width: Int, val height: Int, val depth: Int) {
    private val kubiks: MutableMap<Coordinates, Kubik> = mutableMapOf()

    operator fun get(x: Int, y: Int, z: Int): Kubik? {
        return kubiks[Coordinates(x, y, z)]
    }

    operator fun set(x: Int, y: Int, z: Int, kubik: Kubik?) {
        if (kubik == null)
            kubiks.remove(Coordinates(x, y, z))
        else
            kubiks[Coordinates(x, y, z)] = kubik
    }

    fun remove(x: Int, y: Int, z: Int) {
        kubiks.remove(Coordinates(x, y, z))
    }

    fun findConnectedGroups(): List<KubikGroup> {
        val visited = mutableSetOf<Coordinates>()
        val groups = mutableListOf<KubikGroup>()

        for ((coords, kubik) in kubiks) {
            if (coords !in visited) {
                val group = mutableSetOf<Coordinates>()
                val queue = ArrayDeque<Coordinates>()
                queue.add(coords)
                visited.add(coords)

                while (queue.isNotEmpty()) {
                    val current = queue.removeFirst()
                    group.add(current)
                    val (x, y, z) = current

                    // Check neighbors with the same color
                    if (Coordinates(x - 1, y, z) !in visited && this[x - 1, y, z]?.color == kubik.color) {
                        queue.add(Coordinates(x - 1, y, z))
                        visited.add(Coordinates(x - 1, y, z))
                    }
                    if (Coordinates(x + 1, y, z) !in visited && this[x + 1, y, z]?.color == kubik.color) {
                        queue.add(Coordinates(x + 1, y, z))
                        visited.add(Coordinates(x + 1, y, z))
                    }
                    if (Coordinates(x, y - 1, z) !in visited && this[x, y - 1, z]?.color == kubik.color) {
                        queue.add(Coordinates(x, y - 1, z))
                        visited.add(Coordinates(x, y - 1, z))
                    }
                    if (Coordinates(x, y + 1, z) !in visited && this[x, y + 1, z]?.color == kubik.color) {
                        queue.add(Coordinates(x, y + 1, z))
                        visited.add(Coordinates(x, y + 1, z))
                    }
                    if (Coordinates(x, y, z - 1) !in visited && this[x, y, z - 1]?.color == kubik.color) {
                        queue.add(Coordinates(x, y, z - 1))
                        visited.add(Coordinates(x, y, z - 1))
                    }
                    if (Coordinates(x, y, z + 1) !in visited && this[x, y, z + 1]?.color == kubik.color) {
                        queue.add(Coordinates(x, y, z + 1))
                        visited.add(Coordinates(x, y, z + 1))
                    }
                }
                groups.add(KubikGroup(kubik,  group))
            }
        }

        return groups
    }

    fun createPolygonsForGroup(group: Set<Coordinates>, grid: Grid3D): List<Path> {
        val polygons = mutableListOf<Path>()
        val processedEdges = mutableSetOf<Set<Coordinates>>()

        for (coords in group) {
            val (x, y, z) = coords
            val neighbors = listOf(
                Coordinates(x - 1, y, z),
                Coordinates(x + 1, y, z),Coordinates(x, y - 1, z),
                Coordinates(x, y + 1, z),
                Coordinates(x, y, z - 1),
                Coordinates(x, y, z + 1)
            )

            for (neighbor in neighbors) {
                if (neighbor !in group && grid[neighbor.x, neighbor.y, neighbor.z] == null) {
                    val edge = setOf(coords, neighbor)
                    if (edge !in processedEdges) {
                        processedEdges.add(edge)
                        val (c1, c2) = edge.toList() // Get ordered coordinates from the set
                        polygons.addAll(createSidePolygons(c1, c2))
                    }
                }
            }
        }
        return polygons
    }


    private fun createSidePolygons(c1: Coordinates, c2: Coordinates): List<Path> {
        val cubeSize = 50f
        val spacing = 10f
        val polygons = mutableListOf<Path>()

        val dx = c2.x - c1.x
        val dy = c2.y - c1.y
        val dz = c2.z - c1.z

        if (dx != 0) {
            // Side perpendicular to x-axis
            polygons.add(createPolygon(c1, Coordinates(c1.x, c1.y + 1, c1.z), Coordinates(c2.x, c2.y + 1, c2.z), c2))
            polygons.add(createPolygon(c1, c2, Coordinates(c2.x, c2.y, c2.z + 1), Coordinates(c1.x, c1.y, c1.z + 1)))
        } else if (dy != 0) {
            // Side perpendicular to y-axis
            polygons.add(createPolygon(c1, Coordinates(c1.x + 1, c1.y, c1.z), Coordinates(c2.x + 1, c2.y, c2.z), c2))
            polygons.add(createPolygon(c1, c2, Coordinates(c2.x, c2.y, c2.z + 1), Coordinates(c1.x, c1.y, c1.z + 1)))
        } else if (dz != 0) {
            // Side perpendicular to z-axis
            polygons.add(createPolygon(c1, Coordinates(c1.x + 1, c1.y, c1.z), Coordinates(c2.x + 1, c2.y, c2.z), c2))
            polygons.add(createPolygon(c1, Coordinates(c1.x, c1.y + 1, c1.z), Coordinates(c2.x, c2.y + 1, c2.z), c2))
        }

        return polygons
    }

    private fun createPolygon(c1: Coordinates, c2: Coordinates, c3: Coordinates, c4: Coordinates): Path {
        val cubeSize = 50f
        val spacing = 10f
        val path = Path()
        path.moveTo(
            c1.x * (cubeSize + spacing) + c1.z * (cubeSize + spacing) / 2,
            c1.y * (cubeSize + spacing) - c1.z * (cubeSize + spacing) / 2
        )
        path.lineTo(
            c2.x * (cubeSize + spacing) + c2.z * (cubeSize + spacing) / 2,
            c2.y * (cubeSize + spacing) - c2.z * (cubeSize + spacing) / 2
        )
        path.lineTo(
            c3.x * (cubeSize + spacing) + c3.z * (cubeSize + spacing) / 2,
            c3.y * (cubeSize + spacing) - c3.z * (cubeSize + spacing) / 2
        )
        path.lineTo(
            c4.x * (cubeSize + spacing) + c4.z * (cubeSize + spacing) / 2,
            c4.y * (cubeSize + spacing) - c4.z * (cubeSize + spacing) / 2
        )
        path.close()
        return path
    }


    fun convertToLongEdges(edges: Set<KubikEdge>): Set<LongEdge> {
        val longEdges = mutableSetOf<LongEdge>()
        val processedEdges = mutableSetOf<KubikEdge>()

        // Divide edges into directions (x, y, z) using the direction property
        val xEdges = edges.filter { it.direction == Direction.X }
        val yEdges = edges.filter { it.direction == Direction.Y }
        val zEdges = edges.filter { it.direction == Direction.Z }

        longEdges.addAll(processEdgesInDirection(xEdges, processedEdges))
        longEdges.addAll(processEdgesInDirection(yEdges, processedEdges))
        longEdges.addAll(processEdgesInDirection(zEdges, processedEdges))

        return longEdges
    }

    private fun processEdgesInDirection(
        edges: List<KubikEdge>,
        processedEdges: MutableSet<KubikEdge>
    ): Set<LongEdge> {
        val longEdges = mutableSetOf<LongEdge>()

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

                longEdges.add(LongEdge(connectedEdges.first(), connectedEdges.last()))
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

    fun edgesInPlanes(longEdges: Set<LongEdge>): Map<Plane,Set<LongEdge>>{
        val edgesByPlane: MutableMap<Plane,MutableSet<LongEdge>> = mutableMapOf()

        for (edge in longEdges) {
            if (edge.start.x == edge.end.x) edgesByPlane.computeIfAbsent(Plane.YZ){ mutableSetOf()}.add(edge)
            if (edge.start.y == edge.end.y) edgesByPlane.computeIfAbsent(Plane.XZ){ mutableSetOf()}.add(edge)
            if (edge.start.z == edge.end.z) edgesByPlane.computeIfAbsent(Plane.XY){ mutableSetOf()}.add(edge)
        }

     //   println(edgesByPlane.map { "> ${it.key} : ${it.value.size}  "}.joinToString(","))
        return edgesByPlane
    }

    fun findClosedLoops(edges: Set<LongEdge>): Set<List<Coordinates>> {
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

    private fun LongEdge.isConnectedTo(other: LongEdge): Boolean {
        return start == other.end || end == other.start || start == other.start || end == other.end
    }

    fun createPolygon(edges:Set<LongEdge>): Set<Polygon> {
        return findClosedLoops(edges).map { loop ->
            Polygon(loop)
        }.toSet()
    }

}

data class KubikGroup(val kubik: Kubik, val group: Set<Coordinates>) {

    fun getExternalEdges(): Set<KubikEdge> {
        val externalEdges = mutableSetOf<KubikEdge>()

        for (coords in group) {
            val (x, y, z) = coords

            // Define all 12 edges of a kubik using KubikEdge
            val edges = listOf(
                KubikEdge(coords, Coordinates(x + 1, y, z)),
                KubikEdge(coords, Coordinates(x, y + 1, z)),
                KubikEdge(coords, Coordinates(x, y, z + 1)),
                KubikEdge(Coordinates(x + 1, y, z), Coordinates(x + 1, y + 1, z)),
                KubikEdge(Coordinates(x + 1, y, z), Coordinates(x + 1, y, z + 1)),
                KubikEdge(Coordinates(x, y + 1, z), Coordinates(x + 1, y +1, z)),
                KubikEdge(Coordinates(x, y + 1, z), Coordinates(x, y + 1, z + 1)),
                KubikEdge(Coordinates(x, y, z + 1), Coordinates(x + 1, y, z + 1)),
                KubikEdge(Coordinates(x, y, z + 1), Coordinates(x, y + 1, z + 1)),
                KubikEdge(Coordinates(x + 1, y + 1, z), Coordinates(x + 1, y + 1, z + 1)),
                KubikEdge(Coordinates(x + 1, y, z + 1), Coordinates(x + 1, y + 1, z + 1)),
                KubikEdge(Coordinates(x, y + 1, z + 1), Coordinates(x + 1, y + 1, z + 1))
            )

            for (edge in edges) {
                if (edge in externalEdges){
                    externalEdges.remove(edge)
                }else {
                    externalEdges.add(edge)
                }
            }
        }

        return externalEdges
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

enum class Direction { X, Y, Z }

data class KubikEdge(val start: Coordinates, val end: Coordinates) {
    val direction: Direction = when {
        start.x != end.x -> Direction.X
        start.y != end.y -> Direction.Y
        start.z != end.z -> Direction.Z
        else -> throw IllegalArgumentException("Invalid KubikEdge: start and end coordinates must be adjacent.")
    }

    init {
        require(
            (start.x == end.x && start.y == end.y && Math.abs(start.z - end.z) == 1) ||
                    (start.x == end.x && start.z == end.z && Math.abs(start.y - end.y) == 1) ||
                    (start.y == end.y&& start.z == end.z && Math.abs(start.x - end.x) == 1)
        ) { "Invalid KubikEdge: start and end coordinates must be adjacent." }
    }


    fun isCollinearWith(other: KubikEdge): Boolean {
        return direction== other.direction &&
                (start == other.start || start == other.end || end == other.start || end == other.end)
    }
}

data class LongEdge(val start: Coordinates, val end: Coordinates)

enum class Plane { XY, XZ, YZ }

data class Polygon(val vertices: List<Coordinates>)

data class PolygonGroup(val kubik:Kubik, val polygons: List<Polygon>)