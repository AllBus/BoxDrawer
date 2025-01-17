package com.kos.boxdrawer.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Matrix
import com.kos.boxdrawe.presentation.DrawerViewModel
import com.kos.boxdrawe.presentation.Instruments
import com.kos.boxdrawer.presentation.tabbar.BoxDrawerToolBar
import com.kos.boxdrawer.presentation.display.DisplayBezier
import com.kos.boxdrawer.presentation.display.DisplayDxf
import com.kos.boxdrawer.presentation.display.DisplayGrid
import com.kos.boxdrawer.presentation.display.DisplayTortoise
import com.kos.boxdrawer.presentation.model.ImageMap
import com.kos.compose.FigureInfo
import com.kos.compose.ImmutableList
import com.kos.figure.IFigure
import kotlinx.coroutines.launch

@Composable
fun DisplayBox(
    tabIndex: State<Int>,
    displayScale: MutableFloatState,
    pos:MutableState<Offset>,
    matrix: MutableState<Matrix>,
    figures: State<IFigure>,
    images: State<ImageMap>,
    stateText: MutableState<String>,
    view3d: MutableState<Boolean>,
    vm: State<DrawerViewModel>,
    selectedItem: State<ImmutableList<FigureInfo>>,
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
                images = images.value,
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
                enableMatrix = view3d.value,
                figures = figures.value,
                images = images.value,
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
        BoxDrawerToolBar.TAB_FORMULA,
        BoxDrawerToolBar.TAB_TOOLS -> {
            DisplayTortoise(
                displayScale = displayScale,
                pos = pos,
                matrix = matrix,
                enableMatrix = false,
                figures = figures.value,
                images = images.value,
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
            //MazeSolver()
            DisplayImageProcessing(vm.value.imageData)
        }
        BoxDrawerToolBar.TAB_DXF ->
            DisplayDxf(
                displayScale = displayScale,
                pos = pos,
                matrix = matrix,
                enableMatrix = false,
                figures = figures.value,
                images = images.value,
                selectedItem = selectedItem,
                onStateChange =  { text ->
                    stateText.value = text
                },
                onPress = { point, button , scale ->
                    coroutineScope.launch {
                        vm.value.onPress(point, button, scale)
                    }
                },
                onMove = { point, button , scale ->
                    coroutineScope.launch {
                        vm.value.onMove(point, button, scale)
                    }
                },
                onRelease = { point, button , scale ->
                    coroutineScope.launch {
                        vm.value.onRelease(point, button, scale)
                    }
                },
                instrument = vm.value.dxfData.instrumentState.collectAsState(Instruments.NONE)
            )
        else -> {

        }
    }
}

