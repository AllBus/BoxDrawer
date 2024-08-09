package com.kos.boxdrawer.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.getSelectedText
import com.kos.boxdrawe.presentation.DrawerViewModel
import com.kos.boxdrawe.widget.BoxDrawerToolBar
import com.kos.boxdrawe.widget.TabBar
import com.kos.boxdrawer.presentation.theme.BoxTypography
import com.kos.boxdrawer.template.TemplateInfo
import com.kos.figure.FigureEmpty
import com.kos.figure.FigureInfo
import com.kos.figure.IFigure
import turtoise.help.TortoiseHelpInfo

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
    val helpText by remember { vm.value.tortoise.helpText }
    val matrix = remember { vm.value.tortoise.matrix }
    val alternative = remember { vm.value.box.alternative }
    val stateText = remember { mutableStateOf("") }
    val menu = vm.value.template.menu.collectAsState(TemplateInfo.EMPTY)
    val figureList = remember(figures) {
        derivedStateOf { IFigure.tree(figures.value) }
    }

    val commands = vm.value.helpInfoList.collectAsState(emptyList())

    val selectedItem :State<List<FigureInfo>> = vm.value.selectedItem.collectAsState(emptyList<FigureInfo>())
    val checkboxEditor = vm.value.template.checkboxEditor.collectAsState()

    LaunchedEffect(tabIndex.value) {
        vm.value.setSelected(emptyList())
    }

    val tortoiseListener = vm.value.tortoise.editorListener
    val tortoiseMoveListener = vm.value.tortoise.moveListener

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
                    stateText = stateText,
                    alternative = alternative,
                    vm = vm,
                    selectedItem = selectedItem,
                )

                Column {
                    Editor(
                        modifier = Modifier.weight(1f),
                        tabIndex = tabIndex,
                        moveListener =  tortoiseMoveListener,
                        editorListener =  tortoiseListener,
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

                    val tabIndex = vm.value.tabIndex.collectAsState()
                    AnimatedVisibility (tabIndex.value!= BoxDrawerToolBar.TAB_GRID) {
                        StatusBar(displayScale, pos, stateText, onHomeClick = {
                            rotateValueX.value = 0f
                            rotateValueY.value = 0f
                            rotateValueZ.value = 0f
                            pos.value = Offset.Zero
                            vm.value.tortoise.rotate(
                                rotateValueX.value,
                                rotateValueY.value,
                                rotateValueZ.value
                            )
                        })
                    }
                }
            }
        }
    }
}


