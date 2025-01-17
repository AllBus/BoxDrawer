package com.kos.boxdrawe.presentation

import androidx.compose.runtime.Stable
import com.kos.boxdrawe.widget.NumericTextFieldState
import kotlinx.coroutines.runBlocking
import turtoise.DrawerSettings
import turtoise.TemplateAlgorithm
import turtoise.TortoiseAlgorithm

@Stable
class ToolsData(val tools: Tools, val templateData: TemplateData) : PrintCode {
    val boardWeight =
        NumericTextFieldState(4.0) { tools.selectSettings(tools.settings.value.copy(boardWeight = it)) }
    val holeWeight =
        NumericTextFieldState(4.05) { tools.selectSettings(tools.settings.value.copy(holeWeight = it)) }
    val holeDrop =
        NumericTextFieldState(0.3) { tools.selectSettings(tools.settings.value.copy(holeDrop = it)) }
    val zigDrop =
        NumericTextFieldState(0.3) { tools.selectSettings(tools.settings.value.copy(zigDrop = it)) }
    val holeDropHeight =
        NumericTextFieldState(0.0) { tools.selectSettings(tools.settings.value.copy(holeDropHeight = it)) }
    val holeOffset =
        NumericTextFieldState(2.0) { tools.selectSettings(tools.settings.value.copy(holeOffset = it)) }

    fun selectSettings(newDs: DrawerSettings) {
        tools.selectSettings(newDs.copy())
        boardWeight.update(tools.settings.value.boardWeight)
        holeWeight.update(tools.settings.value.holeWeight)
        holeDrop.update(tools.settings.value.holeDrop)
        zigDrop.update(tools.settings.value.zigDrop)
        holeDropHeight.update(tools.settings.value.holeDropHeight)
        holeOffset.update(tools.settings.value.holeOffset)
    }

    fun selectFigure(a: TortoiseAlgorithm, name: String) {
        when (a) {
            is TemplateAlgorithm -> {
                templateData.setTemplate(a, name)
            }

            else -> {
                templateData.clearTemplate()
            }
        }
    }

    override fun print(): String {
        return runBlocking {
            templateData.print()
        }
    }
}