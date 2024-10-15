package com.kos.boxdrawer.detal.grid

import androidx.compose.ui.graphics.Path
import kotlinx.serialization.Serializable
import vectors.BoundingRectangle
import vectors.Vec2
import java.lang.Math.abs
import kotlin.math.max
import kotlin.math.min

@Serializable
data class Kubik(val color: Int)

data class Parallelepiped(val minX: Int, val minY: Int, val minZ: Int, val maxX: Int, val maxY: Int, val maxZ: Int, val color: Int)


@Serializable
data class Coordinates(val x: Int, val y: Int, val z: Int){
    operator fun minus (other : Coordinates):Coordinates{
        return Coordinates(x-other.x, y-other.y, z-other.z)
    }

    fun dotProduct(other: Coordinates): Int {
        return x * other.x + y * other.y + z * other.z
    }

    fun crossProduct(other: Coordinates): Coordinates {
        return Coordinates(
            y * other.z - z * other.y,
            z * other.x - x * other.z,
            x * other.y - y * other.x
        )
    }

    override fun toString(): String {
        return "($x, $y, $z)"
    }
}


class ResultInt(val value:Int, val move:Int)

@Serializable
class Grid3D() {
    private val kubiks: MutableMap<Coordinates, Kubik> = mutableMapOf()

    operator fun get(x: Int, y: Int, z: Int): Kubik? {
        return kubiks[Coordinates(x, y, z)]
    }

    operator fun get(xyz: Coordinates): Kubik? {
        return kubiks[xyz]
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

    fun saveToText(): String {
        val sb = StringBuilder()
        val parallelepipeds = findParallelepipedsWithOneColor()

        for (parallelepiped in parallelepipeds) {
            sb.append("C${parallelepiped.color} ")
            sb.append("M")
            sb.append("X${parallelepiped.minX}")
            sb.append("Y${parallelepiped.minY}")
            sb.append("Z${parallelepiped.minZ}")
            sb.append(" ")
            sb.append("F")
            sb.append("X${parallelepiped.maxX - parallelepiped.minX + 1}")
            sb.append("Y${parallelepiped.maxY - parallelepiped.minY + 1}")
            sb.append("Z${parallelepiped.maxZ - parallelepiped.minZ + 1}")
            sb.append(" \n")
        }

        return sb.toString().trim()
    }

    fun loadFromText(lines: String): Grid3D {
        var i = 0
        var color = 1
        var current= Coordinates(0,0,0)
        val text = lines +" "
        while (i < text.length) {
            val c= text[i]
            when (c){
                'C' -> {
                    i++
                    color = (text[i]-'0')
                }
                'M' -> {
                    i++
                    var n = text[i]

                    while (n in "XYZ") {
                        i++
                        val resX = readInt(text, i)
                        i = resX.move

                        when (n) {
                            'X' -> current = current.copy(x = resX.value)
                            'Y' -> current = current.copy(y = resX.value)
                            'Z' -> current = current.copy(z = resX.value)
                        }
                        n = text[i]
                    }
                }
                'F' -> {
                    i++
                    val prev = current

                    var n = text[i]
                    while (n in "XYZ") {
                        i++
                        val resX = readInt(text, i)
                        i = resX.move

                        when (n) {
                            'X' -> current = current.copy(x = current.x + resX.value)
                            'Y' -> current = current.copy(y = current.y + resX.value)
                            'Z' -> current = current.copy(z = current.z + resX.value)
                        }

                        n = text[i]
                    }

                    val pp = Coordinates(
                        min(prev.x, current.x),
                        min(prev.y, current.y),
                        min(prev.z, current.z)
                    )
                    val cx = max(abs(current.x - pp.x),1)
                    val cy = max(abs(current.y - pp.y),1)
                    val cz = max(abs(current.z - pp.z),1)
                    val drawKubik = if (color == 0) null else Kubik(color)

                    for (x in 0 until cx) {
                        for (y in 0 until cy) {
                            for (z in 0 until cz) {
                                set(pp.x+x, pp.y+y, pp.z+z, drawKubik)
                            }
                        }
                    }
                }
            }
            i++
        }
        return this
    }



    fun readInt(text: String, i: Int): ResultInt{
        var j =i
        if (j<text.length && text[j]=='-'){
            j++
        }

        while(j <text.length && text[j] in '0'..'9'){
            j++
        }
        if (j == i)
            return ResultInt(0, j)
        return ResultInt(text.substring(i, j).toInt(), j)
    }

    fun findParallelepipedsWithOneColor(): List<Parallelepiped> {
        val parallelepipeds = mutableListOf<Parallelepiped>()
        val visited = mutableSetOf<Coordinates>() // Keep track of visited kubiks

        for ((coordinates, kubik) in kubiks) {
            if (coordinates !in visited) {
                val (maxX, maxY, maxZ) = findMaxDimensions(coordinates, kubik.color)
                parallelepipeds.add(
                    Parallelepiped(
                        coordinates.x, coordinates.y, coordinates.z,
                        maxX, maxY, maxZ, kubik.color
                    )
                )
                // Mark all kubiks within the parallelepiped as visited
                for (x in coordinates.x..maxX) {
                    for (y in coordinates.y..maxY) {
                        for (z in coordinates.z..maxZ) {
                            visited.add(Coordinates(x, y, z))
                        }
                    }
                }
            }
        }

        return parallelepipeds
    }

    private fun findMaxDimensions(startCoordinates: Coordinates, color: Int): Coordinates {
        var maxX = startCoordinates.x
        var maxY = startCoordinates.y
        var maxZ = startCoordinates.z

        // Iterate to find maximum X dimension
        while (this[maxX + 1, startCoordinates.y, startCoordinates.z]?.color == color) {
            maxX++
        }

        // Iterate to find maximum Y dimension
        // Мне нужно проверить только заполнение новой строки так как предудущую я проверил на предыдущей итерации
        while (isFilledXLineWithColor(startCoordinates.x, Coordinates(maxX, maxY + 1, maxZ), color)) {
            maxY++
        }

        // Iterate to find maximum Z dimension
        // Мне нужно проверить толко один слой так как я проверял на предыдущей итерации
        while (isFilledXYWithColor(startCoordinates, Coordinates(maxX, maxY, maxZ + 1), color)) {
            maxZ++
        }

        return Coordinates(maxX, maxY, maxZ)
    }

    private fun isFilledXLineWithColor(minX: Int, maxCoordinates: Coordinates, color: Int): Boolean {
        for (x in minX..maxCoordinates.x) {
            if (this[x, maxCoordinates.y, maxCoordinates.z]?.color != color) {
                return false
            }
        }
        return true
    }

    private fun isFilledXYWithColor(minCoordinates: Coordinates, maxCoordinates: Coordinates, color: Int): Boolean {
        for (x in minCoordinates.x..maxCoordinates.x) {
            for (y in minCoordinates.y..maxCoordinates.y) {
                if (this[x, y, maxCoordinates.z]?.color != color) {
                    return false
                }
            }
        }
        return true
    }

    private fun isParallelepipedFilledWithColor(minCoordinates: Coordinates, maxCoordinates: Coordinates, color: Int): Boolean {
        for (x in minCoordinates.x..maxCoordinates.x) {
            for (y in minCoordinates.y..maxCoordinates.y) {
                for (z in minCoordinates.z..maxCoordinates.z) {
                    if (this[x, y, z]?.color != color) {
                        return false
                    }
                }
            }
        }
        return true
    }

    /** Поиск кубиков стоящих рядом */
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
                    val cl = Coordinates(x - 1, y, z)
                    if (cl !in visited && this[cl]?.color == kubik.color) {
                        queue.add(cl)
                        visited.add(cl)
                    }
                    val cr = Coordinates(x + 1, y, z)
                    if (cr !in visited && this[cr]?.color == kubik.color) {
                        queue.add(cr)
                        visited.add(cr)
                    }
                    val cu = Coordinates(x, y - 1, z)
                    if (cu !in visited && this[cu]?.color == kubik.color) {
                        queue.add(cu)
                        visited.add(cu)
                    }
                    val cd = Coordinates(x, y + 1, z)
                    if (cd !in visited && this[cd]?.color == kubik.color) {
                        queue.add(cd)
                        visited.add(cd)
                    }
                    val cf = Coordinates(x, y, z - 1)
                    if (cf !in visited && this[cf]?.color == kubik.color) {
                        queue.add(cf)
                        visited.add(cf)
                    }
                    val cb = Coordinates(x, y, z + 1)
                    if (cb !in visited && this[cb]?.color == kubik.color) {
                        queue.add(cb)
                        visited.add(cb)
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








    fun createPolygon(edges:Set<LongEdge>): Set<Polygon> {
        return GridLoops.findClosedLoops(edges).map { loop ->
            Polygon(loop.points)
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

data class LongEdge(val start: Coordinates, val end: Coordinates){
    fun isConnectedTo(other: LongEdge): Boolean {
        return start == other.end || end == other.start || start == other.start || end == other.end
    }
}

enum class Plane { XY, XZ, YZ }

data class Polygon(val vertices: List<Coordinates>)

data class PolygonGroup(val kubik:Kubik, val polygons: List<Polygon>)

data class Loop(val points: List<Coordinates>, val edges: List<LongEdge>){

    fun toVec2List(): List<Vec2> {
        val origin = points.first() // Choose the first point as the origin
        val xAxis = (points[1] - origin)
        val yAxis = xAxis.crossProduct(Coordinates(0, 0, 1))

        val lengthXAxis = Math.sqrt((xAxis.x * xAxis.x + xAxis.y * xAxis.y + xAxis.z * xAxis.z).toDouble())
        val lengthYAxis = Math.sqrt((yAxis.x * yAxis.x + yAxis.y* yAxis.y + yAxis.z * yAxis.z).toDouble())

        return points.map { point ->
            val v = point - origin
            Vec2((v.dotProduct(xAxis) / lengthXAxis), (v.dotProduct(yAxis) / lengthYAxis))
        }
    }
}

data class PolyInfo(val points: List<Vec2>, val plane: Int, val orientation:Plane, val min:Double, val width:Double)

data class GroupedPolygons(
    val plane: Int,
    val orientation: Plane,
    val polygons: List<PolyInfo>
)

data class PolyLayer(val polygons : List<List<Vec2>>, val bounds: BoundingRectangle)

