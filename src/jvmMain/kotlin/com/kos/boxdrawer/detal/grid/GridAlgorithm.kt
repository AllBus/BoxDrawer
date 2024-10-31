package com.kos.boxdrawer.detal.grid

import com.kos.boxdrawer.detal.grid.GridLoops.ensureClockwise
import com.kos.boxdrawer.detal.grid.GridLoops.ensureClockwiseKubik
import com.kos.figure.IFigure
import com.kos.figure.collections.FigureList
import com.kos.figure.complex.FigureDirCubik
import com.kos.figure.complex.model.CubikDirection
import com.kos.figure.composition.FigureTranslate
import com.kos.tortoise.ZigzagInfo
import turtoise.DrawerSettings
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
        return create3dFigure(planes, gp)
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
            return create3dFigure(createPlanes(grid), params)
        }

        private fun checkInnerEdge(
            pred: Coordinates,
            tek: Coordinates,
            inner: Set<KubikEdge>
        ): Boolean {
            return inner.contains(KubikEdge(start = pred, end = tek)) ||
                    inner.contains(KubikEdge(start = tek, end = pred))
        }

        fun create3dFigure(planes: List<KubikPlanes>, params: GridParams): FigureList {
            val xDistance = 3.0
            val yDistance = 3.0
            val kubikDistance = 5.0

            val cornerRadius = params.cornerRadius
            val zigInfo = params.zigInfo


            val kubiks = planes.map { (kubik, inner, g): KubikPlanes ->
                val t = g.mapValues { (k, s) ->
                    GridLoops.findPolygons(s).flatMap { p -> Grid3dUtils.findIntersection(p) }
                }.mapValues { (k, s) ->
                    when (k) {
                        Plane.XY ->
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



                                FigureDirCubik(
                                    size = params.cellWidth,
                                    sides = unionSides(sides.toList()),
                                    zigInfo = zigInfo,
                                    cornerRadius = cornerRadius,
                                    enableDrop = true,
                                )
                            }

                        Plane.XZ -> s.map { p ->
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

                            FigureDirCubik(
                                size = params.cellWidth,
                                sides = unionSides(sides.toList()),
                                zigInfo = zigInfo,
                                cornerRadius = cornerRadius,
                                enableDrop = true,
                            )
                        }

                        Plane.YZ ->
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

                                FigureDirCubik(
                                    size = params.cellWidth,
                                    sides = unionSides(sides.toList()),
                                    zigInfo = zigInfo,
                                    cornerRadius = cornerRadius,
                                    enableDrop = true,
                                )
                            }

                    }
                }
                t
            }

            //   val v = arrangePolygons(edges)


            // return Fi edges.map {  p -> createPolygon(p, ds, widthCell.decimal) }

            var cur = Vec2.Zero
            val res = mutableListOf<IFigure>()
            for (k in kubiks) {
                for (edges in k.values) {
                    var maxHe = 0.0
                    for (edge in edges) {
                        val w = edge.rect().width
                        maxHe = max(maxHe, edge.rect().height)

                    //    println("rect ${edge.rect()} ${cur}  >> ${edge.sides}")
                        res += FigureTranslate(edge, cur - edge.rect().min)
                        cur += Vec2(w + xDistance, 0.0)
                    }
                    cur = Vec2(0.0, cur.y + maxHe + yDistance)
                }
                cur = Vec2(0.0, cur.y + kubikDistance)
            }

            return FigureList(res.toList())
        }

        private fun unionSides(list: List<CubikDirection>): List<CubikDirection> {
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

            if (res.size > 1 && list.first() == current) {
                res[0] = (current.copy(count = count  * current.count+ res[0].count))
            } else {
                res.add(current.copy(count = count * current.count))
            }
            return ensureClockwiseKubik(res)
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