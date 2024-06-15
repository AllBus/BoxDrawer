package com.kos.boxdrawer.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.onClick
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kos.boxdrawe.presentation.BoxSimpleListener
import com.kos.boxdrawe.presentation.DrawerViewModel
import com.kos.boxdrawe.themes.ThemeColors
import com.kos.boxdrawe.widget.BoxDrawerToolBar
import com.kos.boxdrawe.widget.TabBar
import com.kos.boxdrawer.presentation.editors.EditBoxPolka
import com.kos.boxdrawer.presentation.editors.EditPosition
import com.kos.boxdrawer.presentation.template.TemplateEditorBox
import com.kos.boxdrawer.template.TemplateGeneratorSimpleListener
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
    val helpText by remember { vm.value.tortoise.helpText }
    val matrix = remember { vm.value.tortoise.matrix }
    val alternative = remember { vm.value.box.alternative }
    val stateText = remember { mutableStateOf("") }
    val menu = vm.value.template.menu.collectAsState(TemplateInfo.EMPTY)
    val figureList = remember(figures) {
        derivedStateOf { IFigure.tree(figures.value) }
    }

    val selectedItem :State<List<FigureInfo>> = vm.value.selectedItem.collectAsState(emptyList<FigureInfo>())
    val checkboxEditor = vm.value.template.checkboxEditor.collectAsState()

    LaunchedEffect(tabIndex.value) {
        vm.value.setSelected(emptyList())
    }

    val tortoiseListener = vm.value.tortoise.editorListener

    MaterialTheme {
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
                        tabIndex,
                        tortoiseListener =  tortoiseListener,
                        boxListener = vm.value.box.boxListener,
                        helpText,
                        menu,
                        vm,
                        alternative,
                        rotateValueX,
                        rotateValueY,
                        rotateValueZ,
                        checkboxEditor,
                        figureList,
                        selectedItem,
                        onRotateDisplay = {
                            vm.value.tortoise.rotate(
                                rotateValueX.value,
                                rotateValueY.value,
                                rotateValueZ.value
                            )
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


@Composable
private fun Editor(
    modifier: Modifier,
    tabIndex: State<Int>,
    tortoiseListener: TemplateGeneratorSimpleListener,
    boxListener: BoxSimpleListener,
    helpText: AnnotatedString,
    menu: State<TemplateInfo>,
    vm: State<DrawerViewModel>,
    alternative: MutableState<Boolean>,
    dropValueX: MutableState<Float>,
    dropValueY: MutableState<Float>,
    dropValueZ: MutableState<Float>,
    checkboxEditor: State<Boolean>,
    figureList: State<List<FigureInfo>>,
    selectedItem: State<List<FigureInfo>>,
    onRotateDisplay : () -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        when (tabIndex.value) {
            BoxDrawerToolBar.TAB_TORTOISE -> {
                Column(
                    Modifier.align(Alignment.TopStart).padding(8.dp)
                ){
                    Column(
                        modifier = Modifier.width(200.dp).padding(end = 4.dp).background(
                            color = ThemeColors.editorBackground,
                            shape = ThemeColors.figureListItemShape
                        ),

                        ) {
                        EditPosition(tortoiseListener)
                    }
                    Text(
                        text = helpText,
                        modifier = Modifier.width(350.dp).wrapContentHeight(),
                        fontSize = 10.sp,
                        lineHeight = 12.sp,
                    )
                }
            }
            BoxDrawerToolBar.TAB_BOX -> {
                Column(
                    Modifier.align(Alignment.TopStart).padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier.width(350.dp).padding(end = 4.dp).background(
                            color = ThemeColors.editorBackground,
                            shape = ThemeColors.figureListItemShape
                        ),

                        ) {
                        EditBoxPolka(boxListener)
                    }
                }
            }
            BoxDrawerToolBar.TAB_TOOLS -> {
                Row(
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    TemplateEditorBox(
                        menu = menu,
                        figureLine = remember(vm.value) { vm.value.template.figureLine },
                        figureName = remember(vm.value) { vm.value.template.figureName },
                        templateGenerator = vm.value.template.templateGenerator,
                    )
                }
            }

            else -> {}
        }

        if ((tabIndex.value == BoxDrawerToolBar.TAB_BOX && !alternative.value) || tabIndex.value == BoxDrawerToolBar.TAB_TORTOISE) {
            Column(
                modifier = Modifier.align(Alignment.TopEnd).width(180.dp)
            ) {
                Slider(
                    modifier = Modifier.wrapContentHeight(),
                    onValueChange = {
                        dropValueX.value = it;
                        onRotateDisplay()
                    },
                    value = dropValueX.value,
                    valueRange = -360f..360f
                )

                Slider(
                    modifier = Modifier.wrapContentHeight(),
                    onValueChange = {
                        dropValueY.value = it;
                        onRotateDisplay()
                    },
                    value = dropValueY.value,
                    valueRange = -360f..360f
                )
                Slider(
                    modifier = Modifier.wrapContentHeight(),
                    onValueChange = {
                        dropValueZ.value = it;
                        onRotateDisplay(
                    )
                    },
                    value = dropValueZ.value,
                    valueRange = -360f..360f
                )
            }
        } else {
            //  var visible by remember { mutableStateOf(tabIndex.value == BoxDrawerToolBar.TAB_TOOLS && !checkboxEditor.value) }
            Row(
                modifier = Modifier.align(Alignment.TopEnd).width(180.dp),
                horizontalArrangement = Arrangement.End
            ) {

                AnimatedVisibility(
                    tabIndex.value == BoxDrawerToolBar.TAB_TOOLS && !checkboxEditor.value ||
                            tabIndex.value == BoxDrawerToolBar.TAB_DXF
                    //tabIndex.value == BoxDrawerToolBar.TAB_SOFT
                    ,
                    enter = expandHorizontally(
                        expandFrom = Alignment.Start
                    ),
                    exit = shrinkHorizontally(
                        shrinkTowards = Alignment.Start
                    )
                )
                {
                    FigureListBox(figureList.value, selectedItem) { f ->
                        vm.value.setSelected(listOf(f))
                    }
                }

            }
        }
    }
}

