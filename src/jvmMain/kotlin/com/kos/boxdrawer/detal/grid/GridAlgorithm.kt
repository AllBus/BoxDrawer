package com.kos.boxdrawer.detal.grid

import androidx.compose.ui.graphics.Matrix
import com.kos.boxdrawer.detal.grid.GridLoops.ensureClockwiseKubik
import com.kos.figure.IFigure
import com.kos.figure.collections.FigureList
import com.kos.figure.complex.FigureDirCubik
import com.kos.figure.complex.model.CubikDirection
import com.kos.figure.complex.model.VolumePosition
import com.kos.figure.composition.FigureTranslate
import com.kos.figure.composition.FigureVolume
import com.kos.tortoise.ZigzagInfo
import turtoise.DrawerSettings
import turtoise.FigureCreator
import turtoise.TortoiseAlgorithm
import turtoise.TortoiseBlock
import turtoise.TortoiseFigureExtractor
import turtoise.TortoiseState
import turtoise.parser.TortoiseParserStackItem
import vectors.Vec2
import kotlin.math.abs
import kotlin.math.max

class GridAlgorithm(
    val items: TortoiseParserStackItem
) : TortoiseAlgorithm {

    private var planes: List<KubikPlanes> = emptyList()
    private var needRedraw = true
    private var params: TortoiseParserStackItem? = null
    private var isVolume = false

    override fun commands(name: String, ds: DrawerSettings): List<TortoiseBlock> {
        return emptyList()
    }

    override val names: List<String>
        get() = listOf("grid")

    private fun parseGrid() {
        val line = items.getInnerAtName("c")?.innerLine.orEmpty()
        params = items.getInnerAtName("p")
        isVolume = items.getInnerAtName("v") != null

        val grid = Grid3D()
        grid.loadFromText(line)
        planes = createPlanes(grid)

    }

    override fun draw(
        name: String,
        state: TortoiseState,
        figureExtractor: TortoiseFigureExtractor
    ): IFigure {
        if (needRedraw) {
            parseGrid()
            needRedraw = false
        }

        val wh = figureExtractor.valueAt(params, 1, 30.0)
        val gp = GridParams(
            ds = figureExtractor.ds,
            cornerRadius = figureExtractor.valueAt(params, 2, 0.0),
            cellWidth = figureExtractor.valueAt(params, 1, 30.0),
            zigInfo = ZigzagInfo(
                width = figureExtractor.valueAt(params, 3, wh * 2 / 5),
                delta = wh,
                height = figureExtractor.valueAt(params, 4, figureExtractor.ds.boardWeight),
                drop = figureExtractor.valueAt(params, 5, figureExtractor.ds.holeDrop)
            )
        )
        return create3dFigure(planes, gp, isVolume)
    }

    companion object {
        fun createPlanes(grid: Grid3D): List<KubikPlanes> {
            val groups = grid.findConnectedGroups()
            val planes = groups.map { g ->
                val e = g.getExternalEdges()
                KubikPlanes(g.kubik, e.inner, Grid3dUtils.edgesInPlanes(e.edges))
            }
            return planes
        }

        fun create3dFigure(grid: Grid3D, params: GridParams): FigureList {
            return create3dFigure(createPlanes(grid), params, false)
        }

        private fun checkInnerEdge(
            pred: Coordinates,
            tek: Coordinates,
            inner: Set<KubikEdge>
        ): Boolean {
            return inner.contains(KubikEdge(start = pred, end = tek)) ||
                    inner.contains(KubikEdge(start = tek, end = pred))
        }

        fun create3dFigure(
            planes: List<KubikPlanes>,
            params: GridParams,
            isVolume: Boolean
        ): FigureList {
            val cornerRadius = params.cornerRadius
            val zigInfo = params.zigInfo

            val kubiks = planes.map { (kubik, inner, g): KubikPlanes ->
                val t = g.mapValues { (k, s) ->
                    GridLoops.findPolygons(s).flatMap { p -> Grid3dUtils.findIntersection(p) }
                        .filter { p -> p.vertices.size > 3 }
                }.mapValues { (k, s) ->
                    when (k) {
                        Plane.XY -> {
                            val pg = s.groupBy { p -> p.vertices[0].z }
                            s.map { p ->
                                val sides = mutableListOf<CubikDirection>()
                                for (i in 1 until p.vertices.size) {
                                    val pred = p.vertices[i - 1]
                                    val tek = p.vertices[i]
                                    val isInner = checkInnerEdge(pred, tek, inner)
                                    val pt = pred - tek
                                    val count = abs(pt.x) + abs(pt.y)
                                    val direction = when {
                                        pt.x > 0 -> CubikDirection.DIRECTION_RIGHT
                                        pt.x < 0 -> CubikDirection.DIRECTION_LEFT
                                        pt.y < 0 -> CubikDirection.DIRECTION_UP
                                        pt.y > 0 -> CubikDirection.DIRECTION_DOWN
                                        else -> CubikDirection.DIRECTION_RIGHT
                                    }
                                    sides.add(
                                        CubikDirection(
                                            isInnerCorner = isInner,
                                            isReverse = direction == CubikDirection.DIRECTION_LEFT || direction == CubikDirection.DIRECTION_RIGHT,
                                            isFlat = false,
                                            count = count,
                                            direction = direction
                                        )
                                    )
                                }

                                val start = p.vertices[0]
                                val isHole = isHoleXY(start, pg[start.z])
                                GridPolygoninfo(start, unionSides(sides, isHole!= null), isHole!= null,
                                    isHole,
                                    isHole?.let{ h ->
                                        Vec2((h.x -start.x)* params.cellWidth, (h.y-start.y)* params.cellWidth)
                                    }?:Vec2.Zero
                                )
                            }
                        }

                        Plane.XZ -> {
                            val pg = s.groupBy { p -> p.vertices[0].y }
                            s.map { p ->
                                val sides = mutableListOf<CubikDirection>()
                                for (i in 1 until p.vertices.size) {
                                    val pred = p.vertices[i - 1]
                                    val tek = p.vertices[i]
                                    val isInner = checkInnerEdge(pred, tek, inner)
                                    val pt = pred - tek
                                    val count = abs(pt.x) + abs(pt.z)
                                    val direction = when {
                                        pt.x > 0 -> CubikDirection.DIRECTION_RIGHT
                                        pt.x < 0 -> CubikDirection.DIRECTION_LEFT
                                        pt.z < 0 -> CubikDirection.DIRECTION_UP
                                        pt.z > 0 -> CubikDirection.DIRECTION_DOWN
                                        else -> CubikDirection.DIRECTION_RIGHT
                                    }
                                    sides.add(
                                        CubikDirection(
                                            isInnerCorner = isInner,
                                            isReverse = direction == CubikDirection.DIRECTION_DOWN || direction == CubikDirection.DIRECTION_UP,
                                            isFlat = false,
                                            count = count,
                                            direction = direction
                                        )
                                    )

                                }

                                val start = p.vertices[0]
                                val isHole = isHoleXZ(start, pg[start.y])
                                GridPolygoninfo(start, unionSides(sides, isHole!= null), isHole!= null,
                                    isHole,               isHole?.let{ h ->
                                        Vec2((h.x -start.x)* params.cellWidth, (h.z-start.z)* params.cellWidth)
                                    }?:Vec2.Zero)
                            }
                        }

                        Plane.YZ -> {
                            val pg = s.groupBy { p -> p.vertices[0].x }
                            s.map { p ->
                                val sides = mutableListOf<CubikDirection>()
                                for (i in 1 until p.vertices.size) {
                                    val pred = p.vertices[i - 1]
                                    val tek = p.vertices[i]
                                    val isInner = checkInnerEdge(pred, tek, inner)
                                    val pt = pred - tek
                                    val count = abs(pt.z) + abs(pt.y)
                                    val direction = when {
                                        pt.z > 0 -> CubikDirection.DIRECTION_RIGHT
                                        pt.z < 0 -> CubikDirection.DIRECTION_LEFT
                                        pt.y < 0 -> CubikDirection.DIRECTION_UP
                                        pt.y > 0 -> CubikDirection.DIRECTION_DOWN
                                        else -> CubikDirection.DIRECTION_RIGHT
                                    }
                                    sides.add(
                                        CubikDirection(
                                            isInnerCorner = isInner,
                                            isReverse = direction == CubikDirection.DIRECTION_DOWN || direction == CubikDirection.DIRECTION_UP,
                                            isFlat = false,
                                            count = count,
                                            direction = direction
                                        )
                                    )
                                }
                                val start = p.vertices[0]
                                val isHole = isHoleYZ(start, pg[start.x])
                                GridPolygoninfo(start, unionSides(sides, isHole!= null), isHole!= null,
                                    isHole,
                                    isHole?.let{ h ->
                                        Vec2((h.y -start.y)* params.cellWidth, (h.z-start.z)* params.cellWidth)
                                    }?:Vec2.Zero
                                )
                            }
                        }
                    }
                }.mapValues { (k, s) ->
                    s.map { p ->
                        val f = FigureDirCubik(
                            size = params.cellWidth,
                            sides = p.sides,
                            zigInfo = zigInfo,
                            cornerRadius = cornerRadius,
                            enableDrop = true,
                        )
                        p to if (p.isHole){
                            FigureCreator.colorDxf(4, f)
                        } else f
                    }
                }
                t
            }

            val res = mutableListOf<IFigure>()
            if (isVolume) {
                drawVolume(kubiks, res, params.cellWidth, params.ds.boardWeight)
            } else {
                val xDistance = 3.0
                val yDistance = 3.0
                val kubikDistance = 5.0
                drawPlane(kubiks, res, xDistance, yDistance, kubikDistance)

            }
            return FigureList(res.toList())
        }

        private fun sign(a:Int): Int{
            return if (a<0)
                -1
            else if (a == 0)
                0
            else
                1
        }


        private fun isHoleXY(start: Coordinates, polygons: List<Polygon>?): Coordinates? {
            if (polygons.isNullOrEmpty())
                return null

            for (r in polygons) {
                if (r.vertices[0] == start)
                    continue
                var count = 0
                var z = sign(r.vertices.last().y - r.vertices[r.vertices.size-2].y)
                for (i in 1 until r.vertices.size) {
                    val pred = r.vertices[i - 1]
                    val next = r.vertices[i]

                    val nz = sign(next.y - pred.y)
                    if (nz!= 0) {
                        if (next.x > start.x && pred.y == start.y) {
                            if (nz == z) {
                                count++
                            }
                        }
                        z = nz
                    }
                }
                if (count % 2 == 1)
                    return r.vertices.first()
            }

            return null
        }

        private fun isHoleXZ(start: Coordinates, polygons: List<Polygon>?): Coordinates? {
            if (polygons.isNullOrEmpty())
                return null

            for (r in polygons) {
                if (r.vertices[0] == start)
                    continue
                var count = 0
                var z = sign(r.vertices.last().z - r.vertices[r.vertices.size-2].z)
                for (i in 1 until r.vertices.size) {
                    val pred = r.vertices[i - 1]
                    val next = r.vertices[i]

                    val nz = sign(next.z - pred.z)
                    if (nz!= 0) {
                        if (next.x > start.x && pred.z == start.z) {
                            if (nz == z) {
                                count++
                            }
                        }
                        z = nz
                    }
                }
                if (count % 2 == 1)
                    return r.vertices.first()
            }

            return null
        }

        private fun isHoleYZ(start: Coordinates, polygons: List<Polygon>?): Coordinates? {
            if (polygons.isNullOrEmpty())
                return null

            for (r in polygons) {
                if (r.vertices[0] == start)
                    continue

                var count = 0
                var z = sign(r.vertices.last().y - r.vertices[r.vertices.size-2].y)
                for (i in 1 until r.vertices.size) {
                    val pred = r.vertices[i - 1]
                    val next = r.vertices[i]

                    val nz = sign(next.y - pred.y)
                    if (nz!= 0) {
                        if (next.z > start.z && pred.y == start.y) {
                            if (nz == z) {
                                count++
                            }

                        }
                        z = nz
                    }
                }
                if (count % 2 == 1)
                    return r.vertices.first()
            }
            return null
        }

        private fun drawVolume(
            kubiks: List<Map<Plane, List<Pair<GridPolygoninfo, IFigure>>>>,
            res: MutableList<IFigure>,
            size: Double,
            boardWeight: Double,
        ) {
            var cur = Vec2.Zero

            val volumes = mutableListOf<VolumePosition>()

            for (k in kubiks) {
                for (plane in k.keys) {
                    val rotateX = when (plane) {
                        Plane.XY -> 0f
                        Plane.XZ -> 90f
                        Plane.YZ -> 0f
                    }
                    val rotateY = when (plane) {
                        Plane.XY -> 0f
                        Plane.XZ -> 00f
                        Plane.YZ -> -90f
                    }
                    val rotateZ = when (plane) {
                        Plane.XY -> 0f
                        Plane.XZ -> 0f
                        Plane.YZ -> 0f
                    }
                    val mr = Matrix()
                    mr.rotateX(rotateX)
                    mr.rotateY(rotateY)
                    mr.rotateZ(rotateZ)
                    //mr.scale(-1.0f, 1f, 1f)

                    k[plane]?.let { edges ->

                        var maxHe = 0.0
                        for (edge in edges) {
                            val p = edge.first
                            val pf = p.start
                            val f = edge.second
                            maxHe = max(maxHe, f.rect().height)

                            val mf = Matrix()

                            /* for plane XY
                            mf.translate(
                                x = -pf.x*size.toFloat(),
                                y = -pf.y*size.toFloat(),
                                z = -pf.z*size.toFloat()
                            )*/
                            mf.translate(
                                x = -pf.x * size.toFloat(),
                                y = -pf.y * size.toFloat(),
                                z = -pf.z * size.toFloat()
                            )

                            // mf*=mr

                            //    println("rect ${edge.rect()} ${cur}  >> ${edge.sides}")
//                            res += Figure3dTransform(vectors.Matrix(mf.values),
//                                Figure3dTransform(vectors.Matrix(mr.values),
//                                    f
//                                )
//                            )
                            volumes += VolumePosition(
                                figure = f,
                                x = -pf.x * size,
                                y = -pf.y * size,
                                z = -pf.z * size,
                                rotateX = rotateX,
                                rotateY = rotateY,
                                rotateZ = rotateZ
                            )
                        }

                    }
                }

            }
            res += FigureVolume(volumes)
        }

        private fun drawPlane(
            kubiks: List<Map<Plane, List<Pair<GridPolygoninfo, IFigure>>>>,
            res: MutableList<IFigure>,
            xDistance: Double,
            yDistance: Double,
            kubikDistance: Double
        ) {
            var cur = Vec2.Zero

            for (k in kubiks) {
                for (plane in k.keys) {
                    val putPosition = mutableMapOf<Coordinates,Vec2>()
                    k[plane]?.let { edges ->

                        var maxHe = 0.0
                        for (edge in edges) {
                            val f = edge.second
                            val info = edge.first
                            val w = f.rect().width
                            if (!info.isHole) {
                                maxHe = max(maxHe, f.rect().height)

                                //    println("rect ${edge.rect()} ${cur}  >> ${edge.sides}")
                                val position = cur  - f.rect().min
                                putPosition[info.start] = position
                                res += FigureTranslate(f, cur - f.rect().min)

                                cur += Vec2(w + xDistance, 0.0)
                            }
                        }
                        for (edge in edges) {
                             val info = edge.first

                            if (info.isHole) {
                                putPosition[info.parentCoordinate]?.let { pos ->
                                    val f = edge.second
                                    res += FigureTranslate(f, pos+info.offset)
                                }
                            }
                        }

                        cur = Vec2(0.0, cur.y + maxHe + yDistance)
                    }
                }
                cur = Vec2(0.0, cur.y + kubikDistance)
            }
        }

        private fun unionSides(list: List<CubikDirection>, isHole: Boolean): List<CubikDirection> {
            if (list.isEmpty())
                return list

            var current = list.first()
            val res = mutableListOf<CubikDirection>()
            var count = 1
            for (i in 1 until list.size) {
                val tek = list[i]
                if (tek == current) {
                    count++
                } else {
                    res.add(current.copy(count = count * current.count))
                    count = 1
                    current = tek
                }
            }

            /*
            if (res.size > 1 && list.first() == current) {
                res[0] = (current.copy(count = count  * current.count+ res[0].count))
            } else {
                res.add(current.copy(count = count * current.count))
            }
            return ensureClockwiseKubik(res)

             */
            res.add(current.copy(count = count * current.count))
            return ensureClockwiseKubik(res, isHole)
        }

        fun polygonToSideLengths(coordinates: List<Coordinates>): List<Int> {
            val vertices = GridLoops.ensureClockwise(coordinates)
            val sideLengths = mutableListOf<Int>()
            var currentLength = 0
            var previousIsHorizontal: Boolean? = null // Initialize to null
            var firstHorizontal: Boolean = true

            for (i in vertices.indices) {
                val current = vertices[i]
                val next = vertices[(i + 1) % vertices.size]

                val isHorizontal = current.y == next.y
                val sideLength = if (isHorizontal) {
                    next.x - current.x
                } else {
                    next.y - current.y
                }

                if (sideLength == 0)
                    continue

                if (previousIsHorizontal == null) { // First side
                    currentLength = sideLength
                    previousIsHorizontal = isHorizontal
                    firstHorizontal = isHorizontal
                } else if (isHorizontal == previousIsHorizontal) { // Same direction
                    currentLength += sideLength
                } else { // Different direction
                    sideLengths.add(currentLength)
                    currentLength = sideLength
                    previousIsHorizontal = isHorizontal
                }
            }

            sideLengths.add(currentLength) // Add the last side length

            return if (!firstHorizontal && sideLengths.size > 0) {
                val l = sideLengths.first()
                val e = sideLengths.last()
                if (sideLengths.size % 2 == 1) {
                    sideLengths.drop(1).dropLast(1) + (l + e)
                } else
                    sideLengths.drop(1) + l
            } else {
                if (sideLengths.size % 2 == 1) {
                    val l = sideLengths.first()
                    val e = sideLengths.last()
                    listOf(l + e) + sideLengths.drop(1).dropLast(1)
                } else
                    sideLengths
            }
        }
    }
}