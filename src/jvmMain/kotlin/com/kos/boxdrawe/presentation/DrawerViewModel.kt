package com.kos.boxdrawe.presentation

import androidx.compose.runtime.mutableStateOf
import com.kos.boxdrawe.widget.BoxDrawerToolBar
import com.kos.boxdrawe.widget.NumericTextFieldState
import com.kos.boxdrawer.figure.FigureExtractor
import com.kos.figure.FigureEmpty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import org.kabeja.dxf.DXFDocument
import org.kabeja.parser.DXFParser
import org.kabeja.parser.ParserBuilder
import turtoise.DrawerSettings
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class DrawerViewModel {

    val tools = Tools()
    val tortoise = TortoiseData(tools)
    val softRez = SoftRezData(tools, tortoise)
    val box = BoxData(tools)
    val grid = GridData(tools)
    val options = ToolsData(tools)
    val bezier = BezierData(tools)
    val bublik = BublikData(tools)
    val tabIndex = MutableStateFlow(BoxDrawerToolBar.TAB_TORTOISE)

    private val noneFigure = MutableStateFlow(FigureEmpty).asStateFlow()

    val figures = tabIndex.flatMapLatest { tab -> when (tab) {
            BoxDrawerToolBar.TAB_TORTOISE -> tortoise.figures
            BoxDrawerToolBar.TAB_SOFT -> softRez.figures
            BoxDrawerToolBar.TAB_BOX -> box.figures
            BoxDrawerToolBar.TAB_BUBLIK -> bublik.figures
            BoxDrawerToolBar.TAB_TOOLS -> tools.currentFigure
            else ->  noneFigure
        }
    }


    init {
        println("DrawerViewModel")
    }

    fun loadSettings(){
        tools.loadSettings()
        options.selectSettings(tools.ds())
    }

}

class ToolsData(val tools: Tools) {
    val boardWeight = NumericTextFieldState(4.0) { tools.selectSettings(tools.settings.value.copy(boardWeight = it)) }
    val holeWeight = NumericTextFieldState(4.05) { tools.selectSettings( tools.settings.value.copy(holeWeight = it)) }
    val holeDrop = NumericTextFieldState(0.3) { tools.selectSettings( tools.settings.value.copy(holeDrop = it)) }
    val holeDropHeight = NumericTextFieldState(0.0) { tools.selectSettings( tools.settings.value.copy(holeDropHeight = it)) }
    val holeOffset = NumericTextFieldState(2.0) { tools.selectSettings( tools.settings.value.copy(holeOffset = it)) }

    fun selectSettings(newDs: DrawerSettings){
        tools.selectSettings(newDs.copy())
        boardWeight.decimal = tools.settings.value.boardWeight
        holeWeight.decimal = tools.settings.value.holeWeight
        holeDrop.decimal = tools.settings.value.holeDrop
        holeDropHeight.decimal = tools.settings.value.holeDropHeight
        holeOffset.decimal = tools.settings.value.holeOffset
    }

    fun loadDxf(fileName: String) {
        try {
            val f = File(fileName)
            val parser = ParserBuilder.createDefaultParser()

            parser.parse(FileInputStream(f), DXFParser.DEFAULT_ENCODING)
            val doc: DXFDocument = parser.getDocument()

            val extractor = FigureExtractor()
            tools.currentFigure.value = extractor.extractFigures(doc)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



}

