package com.kos.boxdrawe.presentation

import androidx.compose.runtime.mutableStateOf
import com.kos.boxdrawe.widget.NumericTextFieldState
import com.kos.boxdrawer.detal.grid.CadGrid
import com.kos.boxdrawer.detal.grid.Coordinates
import com.kos.boxdrawer.detal.grid.Grid3D
import com.kos.boxdrawer.detal.grid.Grid3dUtils
import com.kos.boxdrawer.detal.grid.GridLoops
import com.kos.boxdrawer.detal.grid.GridOption
import com.kos.boxdrawer.detal.grid.Plane
import com.kos.boxdrawer.detal.grid.PolygonGroup
import com.kos.figure.FigureEmpty
import com.kos.figure.IFigure
import com.kos.figure.collections.FigureList
import com.kos.figure.complex.FigureCubik
import com.kos.figure.composition.FigureTranslate
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import com.kos.tortoise.ZigzagInfo
import vectors.Vec2
import kotlin.math.max

class GridData(override val tools: ITools) : SaveFigure {

    var roundChecked = mutableStateOf(false)
    var innerChecked = mutableStateOf(false)

    val widthCell = NumericTextFieldState(6.0)
    val widthFrame = NumericTextFieldState(6.0)
    val radius = NumericTextFieldState(3.0)
    val cellWidthCount = NumericTextFieldState(40.0, 0, 1000.0) { recreateGrid() }
    val cellHeightCount = NumericTextFieldState(30.0, 0, 1000.0) { recreateGrid() }
    val cellRadius = NumericTextFieldState(1.0, 0, 100.0) { recreateGrid() }
    val innerWidth = NumericTextFieldState(1.0, 2)
    val innerRadius = NumericTextFieldState(0.5, 2)
    val gridText = mutableStateOf("")
    val figurePreview = MutableStateFlow(false)
    val useGrid3d = MutableStateFlow(false)

    val redrawEvent = MutableStateFlow(0)

    var grid: Grid3D = Grid3D()

    val gridPlanes = redrawEvent.map {
        val groups = grid.findConnectedGroups()
        val planes = groups.map { g ->
            val e = g.getExternalEdges()
            g.kubik to Grid3dUtils.edgesInPlanes(e)
        }
        planes
    }

    val gridEdges = gridPlanes.map { planes ->
        planes.map { (kubik, g) ->
            val t = g.mapValues { (k, s) -> GridLoops.findPolygons(s) }
                .flatMap { (k, s) -> s }
            PolygonGroup(kubik, t)
        }
    }

    @OptIn(FlowPreview::class)
    val figure = redrawEvent.debounce(500L).combine(figurePreview) { r, p ->
        if (p) {
            createFigureOff()
        } else
            FigureEmpty
    }

    val cad = CadGrid()

    fun recreateGrid() {
        val x = cellWidthCount.decimal.toInt()
        val y = cellHeightCount.decimal.toInt()

        if (x in 1..1000 && y in 1..1000) {

            cad.recreate(x, y)
        }

        redraw()
    }

    fun createFromText() {
        val v = gridText.value

        if (useGrid3d.value) {
            try {
                grid = grid.loadFromText(v)
            } catch (e: Exception) {

            }
        } else {
            val d = v.lines()
            d.forEachIndexed { y, s ->
                s.forEachIndexed { x, c ->
                    cad.setColor(x, y, (c - '0'))
                }
            }
        }
        redraw()
    }

    fun print(): String {
        val sb = StringBuilder()
        for (y in 0 until cad.height) {
            for (x in 0 until cad.width) {
                sb.append((cad.colorAt(x, y) + '0'.code).toChar())
            }
            sb.appendLine()
        }
        return sb.toString()
    }

    fun saveToText() {
        if (useGrid3d.value) {
            gridText.value = grid.saveToText()
        } else {
            gridText.value = print()
        }
    }

    fun redraw() {
        redrawEvent.value += 1

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

        return if (!firstHorizontal && sideLengths.size>0) {
            val l = sideLengths.first()
            val e = sideLengths.last()
            if (sideLengths.size%2 ==1){
                sideLengths.drop(1).dropLast(1)+(l+e)
            } else
                sideLengths.drop(1)+l
        } else{
            if (sideLengths.size %2 ==1){
                val l = sideLengths.first()
                val e = sideLengths.last()
                listOf(l+e) +sideLengths.drop(1).dropLast(1)
            } else
                sideLengths
        }
    }

    override suspend fun createFigure(): IFigure {
        if (useGrid3d.value) {

            return create3dFigure()
        } else {
            return cad.createEntities(
                // cellWidthCount = cellWidthCount .decimal,
                // cellHeightCount = cellHeightCount .decimal,
                frameSize = widthFrame.decimal,
                gridSize = GridOption(
                    size = widthCell.decimal,
                    smooth = radius.decimal,
                    enable = roundChecked.value,
                    roundCell = cellRadius.decimal.toInt()
                ),
                innerInfo = GridOption(
                    size = innerWidth.decimal,
                    smooth = innerRadius.decimal,
                    enable = innerChecked.value
                ),
                drawerSettings = tools.ds()
            )
        }
    }

    private suspend fun create3dFigure(): FigureList {
        val xDistance = 3.0
        val yDistance = 3.0
        val kubikDistance = 5.0

        val planes = gridPlanes.first()

        val ds = tools.ds()

        val cornerRadius = if (roundChecked.value) cellRadius.decimal else 0.0
        val zigInfo = ZigzagInfo(
            width = widthCell.decimal * 2 / 5,
            delta = widthCell.decimal,
            height = ds.boardWeight,
            drop = ds.holeDrop
        )

        val kubiks = planes.map { (kubik, g) ->
            val t = g.mapValues { (k, s) -> GridLoops.findPolygons(s) }
                .mapValues { (k, s) ->
                    when (k) {
                        Plane.XY ->
                            s.map { p ->
                                val res = polygonToSideLengths(p.vertices.map { Coordinates(it.x, it.y, 0) })
                              //  println(p.vertices.joinToString(" "))
                               // println(res.joinToString(" "))
                                FigureCubik(
                                    size = widthCell.decimal,
                                    sides = res.toList(),
                                    zigInfo = zigInfo,
                                    cornerRadius = cornerRadius,
                                    enableDrop = true,
                                    reverseX = true,
                                    reverseY = false,
                                    zigFirstIndex = 0,
                                    zigDistance = 0,
                                )
                            }

                        Plane.XZ ->
                            s.map { p ->
                                val v = p.vertices.map { Coordinates(it.x, it.z, 0) }
                          //      println(v.joinToString(" "))
                                val res = polygonToSideLengths(v)
                                FigureCubik(
                                    size = widthCell.decimal,
                                    sides = res.toList(),
                                    zigInfo = zigInfo,
                                    cornerRadius = cornerRadius,
                                    enableDrop = true,
                                    reverseX = false,
                                    reverseY = true,
                                    zigFirstIndex = 0,
                                    zigDistance = 0,
                                )

                            }

                        Plane.YZ ->
                            s.map { p ->
                                val res = polygonToSideLengths(p.vertices.map { Coordinates(it.z, it.y,  0) })
                                FigureCubik(
                                    size = widthCell.decimal,
                                    sides = res.toList(),
                                    zigInfo = zigInfo,
                                    cornerRadius = cornerRadius,
                                    enableDrop = true,
                                    reverseX = false,
                                    reverseY = true,
                                    zigFirstIndex = 0,
                                    zigDistance = 0,
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
        for (k in kubiks){
            for (edges in k.values) {
                var maxHe = 0.0
                for (edge in edges) {
                    val w = edge.rect().width
                    maxHe = max(maxHe, edge.rect().height)

                    res += FigureTranslate(edge, cur - edge.rect().min)
                    cur += Vec2(w + xDistance, 0.0)
                }
                cur = Vec2(0.0,  cur.y+maxHe+yDistance)
            }
            cur= Vec2( 0.0, cur.y+ kubikDistance)
        }

        return FigureList(res.toList())
    }

    fun createFigureOff(): IFigure {
        return cad.createEntities(
            frameSize = widthFrame.decimal,
            gridSize = GridOption(
                size = widthCell.decimal,
                smooth = radius.decimal,
                enable = roundChecked.value,
                roundCell = cellRadius.decimal.toInt()
            ),
            innerInfo = GridOption(
                size = innerWidth.decimal,
                smooth = innerRadius.decimal,
                enable = false
            ),
            drawerSettings = tools.ds()
        )
    }

}