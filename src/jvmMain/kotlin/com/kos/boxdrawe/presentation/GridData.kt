package com.kos.boxdrawe.presentation

import androidx.compose.runtime.mutableStateOf
import com.kos.boxdrawe.widget.NumericTextFieldState
import com.kos.boxdrawer.detal.grid.CadGrid
import com.kos.boxdrawer.detal.grid.Grid3D
import com.kos.boxdrawer.detal.grid.GridLoops.arrangePolygons
import com.kos.boxdrawer.detal.grid.GridOption
import com.kos.boxdrawer.detal.grid.Plane
import com.kos.boxdrawer.detal.grid.PolyInfo
import com.kos.boxdrawer.detal.grid.PolygonGroup
import com.kos.figure.FigureEmpty
import com.kos.figure.FigurePolyline
import com.kos.figure.IFigure
import com.kos.figure.collections.FigureList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import turtoise.DrawerSettings
import turtoise.DrawingParam
import turtoise.FigureCreator
import com.kos.tortoise.ZigzagInfo
import vectors.Vec2

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
            val e = grid.convertToLongEdges(g.getExternalEdges())
            g.kubik to grid.edgesInPlanes(e)
        }
        planes
    }
    val gridEdges = gridPlanes.map { planes ->
        planes.map { (kubik, g) ->
            val t = g.mapValues { (k, s) -> grid.createPolygon(s) }
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

    override suspend fun createFigure(): IFigure {
        if (useGrid3d.value) {

            val planes = gridPlanes.first()

            val ds = tools.ds()
            val edges = planes.flatMap { (kubik, g) ->
                val t = g.mapValues { (k, s) -> grid.createPolygon(s) }
                    .flatMap { (k, s) ->
                        when (k) {
                            Plane.XY -> s.map { p ->
                                val pt = p.vertices.map { it.x }
                                val v = (pt.max() - pt.min())
                                val m = pt.min()
                                PolyInfo(
                                    p.vertices.map {
                                        Vec2(
                                            it.x.toDouble(),
                                            it.y.toDouble()
                                        ) * widthCell.decimal
                                    },
                                    p.vertices.first().z,
                                    k,
                                    m * widthCell.decimal,
                                    v * widthCell.decimal
                                )
                            }

                            Plane.XZ -> s.map { p ->
                                val pt = p.vertices.map { it.x }
                                val v = (pt.max() - pt.min())
                                val m = pt.min()
                                PolyInfo(
                                    p.vertices.map {
                                        Vec2(
                                            it.x.toDouble(),
                                            it.z.toDouble()
                                        ) * widthCell.decimal
                                    },
                                    p.vertices.first().y,
                                    k,
                                    m * widthCell.decimal,
                                    v * widthCell.decimal
                                )
                            }

                            Plane.YZ -> s.map { p ->
                                val pt = p.vertices.map { it.y }
                                val v = (pt.max() - pt.min())
                                val m = pt.min()
                                PolyInfo(
                                    p.vertices.map {
                                        Vec2(
                                            it.y.toDouble(),
                                            it.z.toDouble()
                                        ) * widthCell.decimal
                                    },
                                    p.vertices.first().x,
                                    k,
                                    m * widthCell.decimal,
                                    v * widthCell.decimal
                                )
                            }
                        }
                    }
                t
            }.map { v -> v.copy(points =  createPolygon(v, ds, widthCell.decimal))}

            val v = arrangePolygons(edges)


            // return Fi edges.map {  p -> createPolygon(p, ds, widthCell.decimal) }

            return FigureList(v.polygons.map { p -> FigurePolyline(p, true) })


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

    private fun createPolygon(p: PolyInfo, ds: DrawerSettings, width:Double): List<Vec2> {

        val param = DrawingParam(
            reverse = false,
            back = false,
        )

        val a = ZigzagInfo(width/2, width, ds.boardWeight)
        val b = ZigzagInfo(width/2 - ds.holeDrop, width, -ds.boardWeight)
        val (zig1, zig2) = when (p.orientation) {
            Plane.XY -> a to a
            Plane.XZ -> b to b
            Plane.YZ -> a to b
        }

        val result = mutableListOf<Vec2>()
        p.points.windowed(2).forEachIndexed { i, t ->
            val zig = if (i % 2 == 0) zig1 else zig2
            FigureCreator.zigzag(
                points = result,
                origin = t[0],
                width = Vec2.distance(t[1], t[0]),
                zig = zig,
                angle = Vec2.angle(t[0], t[1]),
                param = param,
                boardWeight = ds.boardWeight
            )
        }
        return result.toList()
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