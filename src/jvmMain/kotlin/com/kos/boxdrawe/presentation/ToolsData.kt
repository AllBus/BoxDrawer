package com.kos.boxdrawe.presentation

import com.kos.boxdrawe.widget.NumericTextFieldState
import com.kos.boxdrawer.figure.FigureExtractor
import org.kabeja.dxf.DXFDocument
import org.kabeja.parser.DXFParser
import org.kabeja.parser.ParserBuilder
import turtoise.DrawerSettings
import turtoise.TemplateAlgorithm
import turtoise.TortoiseAlgorithm
import java.io.File
import java.io.FileInputStream

class ToolsData(val tools: Tools, val templateData: TemplateData) {
    val boardWeight =
        NumericTextFieldState(4.0) { tools.selectSettings(tools.settings.value.copy(boardWeight = it)) }
    val holeWeight =
        NumericTextFieldState(4.05) { tools.selectSettings(tools.settings.value.copy(holeWeight = it)) }
    val holeDrop =
        NumericTextFieldState(0.3) { tools.selectSettings(tools.settings.value.copy(holeDrop = it)) }
    val holeDropHeight =
        NumericTextFieldState(0.0) { tools.selectSettings(tools.settings.value.copy(holeDropHeight = it)) }
    val holeOffset =
        NumericTextFieldState(2.0) { tools.selectSettings(tools.settings.value.copy(holeOffset = it)) }

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
            tools.updateChooserDir(fileName)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun selectFigure(a: TortoiseAlgorithm, name:String) {
        when(a){
            is TemplateAlgorithm -> {
                templateData.setTemplate(a,name)
            }
            else -> {
                templateData.clearTemplate()
            }
        }
    }
}