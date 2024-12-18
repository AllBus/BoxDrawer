package com.kos.boxdrawer.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.getSelectedText
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.presentation.DrawerViewModel
import com.kos.boxdrawer.presentation.tabbar.BoxDrawerToolBar
import com.kos.boxdrawer.presentation.tabbar.TabBar
import com.kos.boxdrawer.presentation.model.ImageMap
import com.kos.boxdrawer.presentation.theme.BoxTypography
import com.kos.boxdrawer.template.TemplateInfo
import com.kos.figure.FigureEmpty
import com.kos.figure.FigureInfo
import com.kos.figure.IFigure




@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Preview
fun App(vm: State<DrawerViewModel>) {
    val figures = vm.value.figures.collectAsState(FigureEmpty)

    val displayScale = remember { mutableFloatStateOf(2.0f) }
    val pos = rememberSaveable("DisplayTortoiseOffset") { mutableStateOf(Offset.Zero) }

    val rotateValueX = remember { mutableStateOf(0f) }
    val rotateValueY = remember { mutableStateOf(0f) }
    val rotateValueZ = remember { mutableStateOf(0f) }

    val tabIndex = vm.value.tabIndex.collectAsState()
    val helpText = remember {vm.value.tortoise.helpText }
    val matrix = remember { vm.value.tortoise.matrix }
    val alternative = remember { vm.value.box.alternative }
    val stateText = remember { mutableStateOf("") }
    val menu = vm.value.template.menu.collectAsState(TemplateInfo.EMPTY)
    val figureList = remember(figures) {
        derivedStateOf { IFigure.tree(figures.value) }
    }
    val images = vm.value.images.collectAsState(ImageMap.EMPTY)

    val commands = vm.value.helpInfoList.collectAsState(emptyList())

    val selectedItem: State<List<FigureInfo>> =
        vm.value.selectedItem.collectAsState(emptyList<FigureInfo>())
    val checkboxEditor = vm.value.template.checkboxEditor.collectAsState()

    LaunchedEffect(tabIndex.value) {
        vm.value.setSelected(emptyList())
    }

    val tortoiseListener = remember(vm.value) { mutableStateOf(vm.value.tortoise.editorListener) }
    val tortoiseMoveListener = remember(vm.value) { mutableStateOf(vm.value.tortoise.moveListener)}

    MaterialTheme(
        typography = BoxTypography.typography
    ) {
        Column {
            TabBar(BoxDrawerToolBar.tabs, vm)

            Box(modifier = Modifier.fillMaxSize().background(Color(0xFF02007C))) {
                DisplayBox(
                    tabIndex = tabIndex,
                    displayScale = displayScale,
                    pos = pos,
                    matrix = matrix,
                    figures = figures,
                    images = images,
                    stateText = stateText,
                    alternative = alternative,
                    vm = vm,
                    selectedItem = selectedItem,
                )

                Column {
                    val calculatorVisible = remember { mutableStateOf(false) }
                    Row(
                        modifier = Modifier.weight(1f)
                    ) {
                        Editor(
                            modifier = Modifier,
                            tabIndex = tabIndex,
                            moveListener = tortoiseMoveListener,
                            editorListener = tortoiseListener,
                            boxListener = vm.value.box.boxListener,
                            helpText = helpText,
                            menu = menu,
                            vm = vm,
                            alternative = alternative,
                            dropValueX = rotateValueX,
                            dropValueY = rotateValueY,
                            dropValueZ = rotateValueZ,
                            checkboxEditor = checkboxEditor,
                            figureList = figureList,
                            selectedItem = selectedItem,
                            commands = commands,
                            onRotateDisplay = {
                                vm.value.tortoise.rotate(
                                    rotateValueX.value,
                                    rotateValueY.value,
                                    rotateValueZ.value
                                )
                            },
                            onPickSelected = {
                                vm.value.tortoise.text.value.getSelectedText().toString()
                            }
                        )
                    }
                    AnimatedVisibility(calculatorVisible.value && tabIndex.value != BoxDrawerToolBar.TAB_GRID) {
                        Box {
                            CalculatorBox(modifier = Modifier.align(Alignment.BottomStart).padding(start = 8.dp), vm.value.calculatorData)
                        }
                    }

                    AnimatedVisibility(tabIndex.value != BoxDrawerToolBar.TAB_GRID) {
                        StatusBar(displayScale, stateText, onHomeClick = {
                            rotateValueX.value = 0f
                            rotateValueY.value = 0f
                            rotateValueZ.value = 0f
                            pos.value = Offset.Zero
                            vm.value.tortoise.rotate(
                                rotateValueX.value,
                                rotateValueY.value,
                                rotateValueZ.value
                            )
                        },
                            onCalculatorClick = {
                                calculatorVisible.value = !calculatorVisible.value
                            }
                        )
                    }

                }
            }
        }
    }
}

