package com.kos.boxdrawe.presentation

import androidx.compose.runtime.mutableStateOf
import com.kos.boxdrawe.widget.BoxDrawerToolBar
import com.kos.boxdrawe.widget.NumericTextFieldState
import com.kos.boxdrawer.detal.box.BoxCad
import com.kos.boxdrawer.detal.box.BoxInfo
import com.kos.boxdrawer.detal.box.PazForm
import com.kos.boxdrawer.detal.box.WaldParam
import com.kos.boxdrawer.detal.soft.SoftRez
import figure.CropSide
import figure.Figure
import figure.FigureLine
import figure.IFigure
import turtoise.*
import vectors.Vec2

class DrawerViewModel {

    val tools = Tools()
    val tortoise = TortoiseData(tools)
    val softRez = SoftRezData(tools)
    val box = BoxData(tools)
    val grid = GridData(tools)
    val options = ToolsData(tools)
    val tabIndex = mutableStateOf(BoxDrawerToolBar.TAB_TORTOISE)


}

class TortoiseData(val tools: ITools) {
    val figures = mutableStateOf<IFigure>(Figure.Empty)
    val fig = mutableStateOf<IFigure>(Figure.Empty)

    private val t = Tortoise()

    private val memory = SimpleTortoiseMemory()

    fun saveTortoise(fileName: String, lines: String) {
        val program = tortoiseProgram(lines)
        val fig = t.draw(program, Vec2.Zero, tools.ds(), memory)
        tools.saveFigures(fileName, fig)
    }

    private fun tortoiseProgram(lines: String): TortoiseProgram {
        return TortoiseProgram(commands = lines.split("\n").map { line ->
            TortoiseParser.extractTortoiseCommands(line)
        })
    }

    fun createTortoise(lines: String) {
        val program = tortoiseProgram(lines)
        fig.value = t.draw(program, Vec2.Zero, tools.ds(), memory)
        figures.value = t.draw(program, Vec2.Zero, tools.ds(), memory)
    }

    fun drop(dropValueX: Float, dropValueY: Float) {
        figures.value =
            fig.value.crop(dropValueX.toDouble(), CropSide.BOTTOM).crop(dropValueY.toDouble(), CropSide.LEFT)
    }

    val text = mutableStateOf("")
}

class SoftRezData(val tools: ITools) {
    val sr = SoftRez()

    fun saveRez(fileName: String, figure: IFigure) {
        tools.saveFigures(fileName, drawRez(figure))
    }

    fun drawRez(figure: IFigure): IFigure {
        val f = if (figure.count == 0) {
            FigureLine(Vec2(0.0, 0.0), Vec2(2.0, 0.0))
        } else
            figure

        return sr.drawRect(
            w = width.decimal,
            h = height.decimal,
            sdx = cellWidthDistance.decimal,
            sdy = cellHeightDistance.decimal,
            xCount = cellWidthCount.decimal.toInt(),
            yCount = cellHeightCount.decimal.toInt(),
            fit = innerChecked.value,
            form = f,
        )
    }

    var innerChecked = mutableStateOf(true)
    val width = NumericTextFieldState(60.0)
    val height = NumericTextFieldState(60.0)
    val cellWidthCount = NumericTextFieldState(5.0, 0, 1000.0)
    val cellHeightCount = NumericTextFieldState(6.0, 0, 1000.0)
    val cellWidthDistance = NumericTextFieldState(2.0, 2)
    val cellHeightDistance = NumericTextFieldState(2.0, 2)
}

class BoxData(val tools: ITools) {

    val figures = mutableStateOf<IFigure>(Figure.Empty)


    private val box = BoxCad()

    fun boxFigures(line: String): IFigure {

        val wald = WaldParam(
            topOffset = tools.ds().holeOffset,
            bottomOffset = tools.ds().holeOffset,
            holeOffset = tools.ds().holeOffset,
            holeDrop = tools.ds().holeDrop,
            holeWeight = tools.ds().holeWeight,
            topForm = PazForm.Paz,
            bottomForm = PazForm.Hole,
        )

        return box.box(
            boxInfo = BoxInfo(width.decimal, height.decimal, weight.decimal),
            zigW = ZigzagInfo(
                width = 15.0,
                delta = 35.0
            ),
            zigH = ZigzagInfo(
                width = 15.0,
                delta = 35.0
            ),
            zigWe = ZigzagInfo(
                width = 15.0,
                delta = 35.0
            ),
            drawerSettings = tools.ds(),
            wald = wald
        )
    }

    fun createBox(line: String) {
        val fig = boxFigures(line)
        figures.value = fig
    }

    fun saveBox(fileName: String, line: String) {
        val fig = boxFigures(line)
        figures.value = fig

        tools.saveFigures(fileName, fig)
    }

    val width = NumericTextFieldState(100.0){ createBox(text.value)}
    val height = NumericTextFieldState(50.0){ createBox(text.value)}
    val weight = NumericTextFieldState(60.0){ createBox(text.value)}
    var insideChecked = mutableStateOf(false)
    val text = mutableStateOf("")
}

class GridData(val tools: ITools) {

    var roundChecked = mutableStateOf(false)
    var innerChecked = mutableStateOf(false)

    val widthCell = NumericTextFieldState(6.0)
    val widthFrame = NumericTextFieldState(6.0)
    val radius = NumericTextFieldState(3.0)
    val cellWidthCount = NumericTextFieldState(40.0, 0, 1000.0)
    val cellHeightCount = NumericTextFieldState(30.0, 0, 1000.0)
    val innerWidth = NumericTextFieldState(1.0, 2)
    val innerRadius = NumericTextFieldState(0.5, 2)

}

class ToolsData(val tools: Tools) {
    val boardWeight = NumericTextFieldState(4.0) { tools.drawingSettings = tools.drawingSettings.copy(boardWeight = it) }
    val holeWeight = NumericTextFieldState(4.05) { tools.drawingSettings = tools.drawingSettings.copy(holeWeight = it) }
    val holeDrop = NumericTextFieldState(0.5) { tools.drawingSettings = tools.drawingSettings.copy(holeDrop = it) }
    val holeOffset = NumericTextFieldState(2.0) { tools.drawingSettings = tools.drawingSettings.copy(holeOffset = it) }
}

