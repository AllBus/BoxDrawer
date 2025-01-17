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
import com.kos.boxdrawer.presentation.model.ImageMap
import com.kos.boxdrawer.presentation.tabbar.BoxDrawerToolBar
import com.kos.boxdrawer.presentation.tabbar.TabBar
import com.kos.boxdrawer.presentation.theme.BoxTypography
import com.kos.boxdrawer.template.TemplateInfo
import com.kos.compose.FigureInfo
import com.kos.compose.ImmutableList
import com.kos.figure.FigureEmpty

@Composable
@Preview
fun App(vm: State<DrawerViewModel>) {
    val vmv = vm.value
    val figures = vmv.figures.collectAsState(FigureEmpty)

    val displayScale = remember { mutableFloatStateOf(2.0f) }
    val pos = rememberSaveable("DisplayTortoiseOffset") { mutableStateOf(Offset.Zero) }

    val rotateValueX = remember { mutableFloatStateOf(0f) }
    val rotateValueY = remember { mutableFloatStateOf(0f) }
    val rotateValueZ = remember { mutableFloatStateOf(0f) }

    val tabIndex = vmv.tabIndex.collectAsState()
    val helpText = remember { vmv.tortoise.helpText }
    val matrix = remember { vmv.tortoise.matrix }
    val view3d = remember { vmv.box.view3d }
    val stateText = remember { mutableStateOf("") }
    val menu = vmv.template.menu.collectAsState(TemplateInfo.EMPTY)
    val figureList = remember(figures) {
        derivedStateOf { ImmutableList(FigureInfo.tree(figures.value)) }
    }
    val images = vmv.images.collectAsState(ImageMap.EMPTY)

    val commands = vmv.helpInfoList.collectAsState(ImmutableList(emptyList()))

    val selectedItem: State<ImmutableList<FigureInfo>> =
        vmv.selectedItem.collectAsState(ImmutableList(emptyList<FigureInfo>()))
    val checkboxEditor = vmv.template.checkboxEditor.collectAsState()

    LaunchedEffect(tabIndex.value) {
        vm.value.setSelected(ImmutableList(emptyList()))
    }

    val tortoiseListener = remember(vmv) { mutableStateOf(vmv.tortoise.editorListener) }
    val tortoiseMoveListener = remember(vmv) { mutableStateOf(vmv.tortoise.moveListener) }

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
                    view3d = view3d,
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
                            boxListener = vmv.box.boxListener,
                            helpText = helpText,
                            menu = menu,
                            vm = vm,
                            view3d = view3d,
                            dropValueX = rotateValueX,
                            dropValueY = rotateValueY,
                            dropValueZ = rotateValueZ,
                            checkboxEditor = checkboxEditor,
                            figureList = figureList,
                            selectedItem = selectedItem,
                            commands = commands,
                            onRotateDisplay = remember(vmv) {
                                {
                                    vmv.tortoise.rotate(
                                        rotateValueX.value,
                                        rotateValueY.value,
                                        rotateValueZ.value
                                    )
                                }
                            },
                            onPickSelected = remember(vmv) {
                                {
                                    vmv.tortoise.text.value.getSelectedText().toString()
                                }
                            }
                        )
                    }
                    AnimatedVisibility(calculatorVisible.value && tabIndex.value != BoxDrawerToolBar.TAB_GRID) {
                        Box {
                            CalculatorBox(
                                modifier = Modifier.align(Alignment.BottomStart)
                                    .padding(start = 8.dp), vmv.calculatorData
                            )
                        }
                    }

                    AnimatedVisibility(tabIndex.value != BoxDrawerToolBar.TAB_GRID) {
                        StatusBar(
                            displayScale, stateText, onHomeClick =
                                remember(vmv) {
                                    {
                                        rotateValueX.value = 0f
                                        rotateValueY.value = 0f
                                        rotateValueZ.value = 0f
                                        pos.value = Offset.Zero
                                        vmv.tortoise.rotate(
                                            rotateValueX.value,
                                            rotateValueY.value,
                                            rotateValueZ.value
                                        )
                                    }
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

