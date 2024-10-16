package com.kos.boxdrawer.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kos.boxdrawe.presentation.BoxSimpleListener
import com.kos.boxdrawe.presentation.DrawerViewModel
import com.kos.boxdrawe.themes.ThemeColors
import com.kos.boxdrawer.presentation.tabbar.BoxDrawerToolBar
import com.kos.boxdrawer.presentation.editors.EditBoxPolka
import com.kos.boxdrawer.presentation.editors.EditPosition
import com.kos.boxdrawer.presentation.template.TemplateEditorBox
import com.kos.boxdrawer.template.TemplateFigureBuilderListener
import com.kos.boxdrawer.template.TemplateGeneratorSimpleListener
import com.kos.boxdrawer.template.TemplateInfo
import com.kos.figure.FigureInfo
import turtoise.help.HelpInfoCommand

@Composable
fun Editor(
    modifier: Modifier,
    tabIndex: State<Int>,
    moveListener: State<TemplateGeneratorSimpleListener>,
    editorListener: State<TemplateFigureBuilderListener>,
    boxListener: BoxSimpleListener,
    helpText:  State<AnnotatedString>,
    menu: State<TemplateInfo>,
    vm: State<DrawerViewModel>,
    alternative: MutableState<Boolean>,
    dropValueX: MutableState<Float>,
    dropValueY: MutableState<Float>,
    dropValueZ: MutableState<Float>,
    checkboxEditor: State<Boolean>,
    figureList: State<List<FigureInfo>>,
    selectedItem: State<List<FigureInfo>>,
    commands: State<List<HelpInfoCommand>>,
    onRotateDisplay: () -> Unit,
    onPickSelected: () -> String
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        val expanded = rememberSaveable("tortoiseEditorExpanded") { mutableStateOf(true) }
        when (tabIndex.value) {
            BoxDrawerToolBar.TAB_TORTOISE -> {
                Column(
                    Modifier.align(Alignment.TopStart).padding(8.dp)
                ) {
                    val state = rememberScrollState()
                    Box {
                        Column(
                            modifier = Modifier.padding(end = 4.dp).width(200.dp)
                                .verticalScroll(state)
                                .background(
                                    color = ThemeColors.editorBackground,
                                    shape = ThemeColors.figureListItemShape
                                ),

                            ) {
                            EditPosition(
                                expanded,
                                commands,
                                moveListener,
                                editorListener,
                                onPickSelected
                            )
                        }
                        VerticalScrollbar(
                            adapter = rememberScrollbarAdapter(state),
                            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                        )
                    }

                    if (!expanded.value) {
                        Text(
                            text = helpText.value,
                            modifier = Modifier.width(350.dp).wrapContentHeight(),
                            //          .verticalScroll(scrollState, enabled = false)
                            fontSize = 10.sp,
                            lineHeight = 12.sp,
                        )
                    }
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
            val modifier = Modifier.align(Alignment.TopEnd).width(180.dp)
            Rotate3dController(modifier, dropValueX, dropValueY, dropValueZ, onRotateDisplay)
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
