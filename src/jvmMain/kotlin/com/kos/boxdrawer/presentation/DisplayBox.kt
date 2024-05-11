package com.kos.boxdrawer.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.Matrix
import com.kos.boxdrawe.presentation.DrawerViewModel
import com.kos.boxdrawe.widget.BoxDrawerToolBar
import com.kos.boxdrawe.widget.display.DisplayBezier
import com.kos.boxdrawe.widget.display.DisplayGrid
import com.kos.boxdrawe.widget.display.DisplayTortoise
import com.kos.figure.IFigure

@Composable
fun DisplayBox(
    tabIndex: State<Int>,
    displayScale: MutableFloatState,
    matrix: MutableState<Matrix>,
    figures: State<IFigure>,
    stateText: MutableState<String>,
    alternative: MutableState<Boolean>,
    vm: State<DrawerViewModel>,
    selectedItem: State<IFigure>,
) {
    when (tabIndex.value) {
        BoxDrawerToolBar.TAB_TORTOISE -> {
            DisplayTortoise(
                displayScale = displayScale,
                matrix = matrix,
                enableMatrix = true,
                figures = figures.value,
                selectedItem = selectedItem
            ) { text ->
                stateText.value = text
            }
        }

        BoxDrawerToolBar.TAB_BOX -> {
            DisplayTortoise(
                displayScale = displayScale,
                matrix = matrix,
                enableMatrix = !alternative.value,
                figures = figures.value,
                selectedItem = selectedItem
            ) { text ->
                stateText.value = text
            }
        }

        BoxDrawerToolBar.TAB_GRID -> {
            DisplayGrid(vm.value.grid)
        }

        BoxDrawerToolBar.TAB_BEZIER -> {
            DisplayBezier(displayScale, vm.value.bezier)
        }

        BoxDrawerToolBar.TAB_RECT,
        BoxDrawerToolBar.TAB_SOFT,
        BoxDrawerToolBar.TAB_BUBLIK -> {
            DisplayTortoise(
                displayScale = displayScale,
                matrix = matrix,
                enableMatrix = false,
                figures = figures.value,
                selectedItem = selectedItem
            ) { text ->
                stateText.value = text
            }
        }

        BoxDrawerToolBar.TAB_TOOLS -> {
            DisplayTortoise(
                displayScale = displayScale,
                matrix = matrix,
                enableMatrix = false,
                figures = figures.value,
                selectedItem = selectedItem
            ) { text ->
                stateText.value = text
            }
        }

        else -> {

        }
    }
}