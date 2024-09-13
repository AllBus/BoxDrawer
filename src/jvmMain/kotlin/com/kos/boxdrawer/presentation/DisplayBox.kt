package com.kos.boxdrawer.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Matrix
import com.kos.boxdrawe.presentation.DrawerViewModel
import com.kos.boxdrawe.widget.BoxDrawerToolBar
import com.kos.boxdrawer.presentation.display.DisplayBezier
import com.kos.boxdrawer.presentation.display.DisplayGrid
import com.kos.boxdrawer.presentation.display.DisplayTortoise
import com.kos.figure.FigureInfo
import com.kos.figure.IFigure
import kotlinx.coroutines.launch
import vectors.MazeSolver

@Composable
fun DisplayBox(
    tabIndex: State<Int>,
    displayScale: MutableFloatState,
    pos:MutableState<Offset>,
    matrix: MutableState<Matrix>,
    figures: State<IFigure>,
    stateText: MutableState<String>,
    alternative: MutableState<Boolean>,
    vm: State<DrawerViewModel>,
    selectedItem: State<List<FigureInfo>>,
) {
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(tabIndex.value) {
        stateText.value = ""
    }

    when (tabIndex.value) {
        BoxDrawerToolBar.TAB_TORTOISE -> {
            DisplayTortoise(
                displayScale = displayScale,
                pos = pos,
                matrix = matrix,
                enableMatrix = true,
                figures = figures.value,
                selectedItem = selectedItem,
                onStateChange =  { text ->
                    stateText.value = text
                },
                onPress = { point, button , scale ->
                    coroutineScope.launch {
                        vm.value.onPress(point, button, scale)
                    }
                }
            )
        }

        BoxDrawerToolBar.TAB_BOX -> {
            DisplayTortoise(
                displayScale = displayScale,
                pos = pos,
                matrix = matrix,
                enableMatrix = !alternative.value,
                figures = figures.value,
                selectedItem = selectedItem,
                onStateChange =  { text ->
                    stateText.value = text
                },
                onPress = { point, button , scale ->
                    coroutineScope.launch {
                        vm.value.onPress(point, button, scale)
                    }
                }
            )
        }

        BoxDrawerToolBar.TAB_GRID -> {
            DisplayGrid(vm.value.grid)
        }

        BoxDrawerToolBar.TAB_BEZIER -> {
            DisplayBezier(
                displayScale = displayScale,
                pos = pos,
                vm = vm.value.bezier,
                onSetStateText = {stateText.value = it},
                )

        }

        BoxDrawerToolBar.TAB_REKA,
        BoxDrawerToolBar.TAB_SOFT,
        BoxDrawerToolBar.TAB_BUBLIK,
        BoxDrawerToolBar.TAB_DXF,
        BoxDrawerToolBar.TAB_TOOLS -> {
            DisplayTortoise(
                displayScale = displayScale,
                pos = pos,
                matrix = matrix,
                enableMatrix = false,
                figures = figures.value,
                selectedItem = selectedItem,
                onStateChange =  { text ->
                    stateText.value = text
                },
                onPress = { point, button , scale ->
                    coroutineScope.launch {
                        vm.value.onPress(point, button, scale)
                    }
                }
            )
        }
        BoxDrawerToolBar.TAB_IMAGE -> {
            MazeSolver()
            //DisplayImageProcessing(vm.value.imageData)
        }


        else -> {

        }
    }
}

