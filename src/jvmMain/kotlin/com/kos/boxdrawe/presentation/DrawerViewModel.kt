package com.kos.boxdrawe.presentation

import androidx.compose.runtime.mutableStateOf
import com.kos.boxdrawe.widget.BoxDrawerToolBar
import com.kos.boxdrawe.widget.NumericTextFieldState
import turtoise.DrawerSettings

class DrawerViewModel {

    val tools = Tools()
    val tortoise = TortoiseData(tools)
    val softRez = SoftRezData(tools)
    val box = BoxData(tools)
    val grid = GridData(tools)
    val options = ToolsData(tools)
    val bezier = BezierData(tools)
    val bublik = BublikData(tools)
    val tabIndex = mutableStateOf(BoxDrawerToolBar.TAB_TORTOISE)


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

}

