package com.kos.boxdrawe.presentation

import androidx.compose.runtime.mutableStateOf
import com.kos.boxdrawe.widget.BoxDrawerToolBar
import com.kos.boxdrawe.widget.NumericTextFieldState

class DrawerViewModel {

    val tools = Tools()
    val tortoise = TortoiseData(tools)
    val softRez = SoftRezData(tools)
    val box = BoxData(tools)
    val grid = GridData(tools)
    val options = ToolsData(tools)
    val tabIndex = mutableStateOf(BoxDrawerToolBar.TAB_TORTOISE)

}

class ToolsData(val tools: Tools) {
    val boardWeight =
        NumericTextFieldState(4.0) { tools.drawingSettings = tools.drawingSettings.copy(boardWeight = it) }
    val holeWeight = NumericTextFieldState(4.05) { tools.drawingSettings = tools.drawingSettings.copy(holeWeight = it) }
    val holeDrop = NumericTextFieldState(0.3) { tools.drawingSettings = tools.drawingSettings.copy(holeDrop = it) }
    val holeDropHeight = NumericTextFieldState(0.0) { tools.drawingSettings = tools.drawingSettings.copy(holeDropHeight = it) }
    val holeOffset = NumericTextFieldState(2.0) { tools.drawingSettings = tools.drawingSettings.copy(holeOffset = it) }
}

