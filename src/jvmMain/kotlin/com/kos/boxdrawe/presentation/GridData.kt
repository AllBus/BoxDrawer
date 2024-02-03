package com.kos.boxdrawe.presentation

import androidx.compose.runtime.mutableStateOf
import com.kos.boxdrawe.widget.NumericTextFieldState
import com.kos.boxdrawer.detal.grid.CadGrid
import com.kos.boxdrawer.detal.grid.GridOption

class GridData(val tools: ITools) {

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


    val cad = CadGrid()

    fun recreateGrid() {
        val x = cellWidthCount.decimal.toInt()
        val y = cellHeightCount.decimal.toInt()

        if (x in 1..1000 && y in 1..1000) {

            cad.recreate(x, y)
        }
    }

    fun createFromText() {
        val v = gridText.value
        val d = v.lines()
        d.forEachIndexed { y, s ->
            s.forEachIndexed { x, c ->
                cad.setColor(x, y, (c - '0'))
            }
        }
    }

    fun saveToText() {

        val sb = StringBuilder()
        for (y in 0 until cad.height) {
            for (x in 0 until cad.width) {
                sb.append((cad.colorAt(x, y) + '0'.code).toChar())
            }
            sb.appendLine()
        }
        gridText.value = sb.toString()
    }

    fun save(fileName: String) {
        val fig =cad.createEntities(
           // cellWidthCount = cellWidthCount .decimal,
           // cellHeightCount = cellHeightCount .decimal,
            frameSize = widthFrame.decimal,
            gridSize = GridOption(
                size = widthCell.decimal,
                smooth = radius .decimal,
                enable = roundChecked.value,
                roundCell = cellRadius.decimal.toInt()
            ),
            innerInfo = GridOption(
                size = innerWidth .decimal,
                smooth = innerRadius.decimal,
                enable = innerChecked .value
            ),
            drawerSettings = tools.ds()
        )

        tools.saveFigures(fileName, fig)
    }


}