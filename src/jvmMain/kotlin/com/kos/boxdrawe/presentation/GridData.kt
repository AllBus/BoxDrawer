package com.kos.boxdrawe.presentation

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import com.kos.boxdrawe.widget.NumericTextFieldState
import com.kos.boxdrawer.detal.grid.CadGrid
import com.kos.boxdrawer.detal.grid.Grid3D
import com.kos.boxdrawer.detal.grid.GridAlgorithm
import com.kos.boxdrawer.detal.grid.GridLoops
import com.kos.boxdrawer.detal.grid.GridOption
import com.kos.boxdrawer.detal.grid.GridParams
import com.kos.boxdrawer.detal.grid.PolygonGroup
import com.kos.figure.FigureEmpty
import com.kos.figure.IFigure
import com.kos.tortoise.ZigzagInfo
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@Stable
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
        GridAlgorithm.createPlanes(grid)
    }

    val gridEdges = gridPlanes.map { planes ->

        planes.map { (kubik, inner, g) ->
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

    private suspend fun create3dFigure(): IFigure {
        val planes = gridPlanes.first()
        val ds = tools.ds()
        val gp = GridParams(
            ds = ds,
            cornerRadius = if (roundChecked.value) cellRadius.decimal else 0.0,
            cellWidth = widthCell.decimal,
            zigInfo = ZigzagInfo(
                width = widthCell.decimal * 2 / 5,
                delta = widthCell.decimal,
                height = ds.boardWeight,
                drop = ds.holeDrop
            )
        )
        return GridAlgorithm.create3dFigure(planes, gp, false)
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