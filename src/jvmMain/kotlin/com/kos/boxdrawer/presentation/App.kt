package com.kos.boxdrawer.presentation

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.onClick
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kos.boxdrawe.presentation.DrawerViewModel
import com.kos.boxdrawe.themes.ThemeColors
import com.kos.boxdrawe.widget.BoxDrawerToolBar
import com.kos.boxdrawe.widget.TabBar
import com.kos.boxdrawer.template.TemplateInfo
import com.kos.figure.FigureEmpty
import com.kos.figure.IFigure
import kotlinx.coroutines.launch
import vectors.Vec2

@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Preview
fun App(vm: State<DrawerViewModel>) {

    val figures = vm.value.figures.collectAsState(FigureEmpty)

    val displayScale = remember { mutableFloatStateOf(2.0f) }
    var pos  = rememberSaveable("DisplayTortoiseOffset") { mutableStateOf(Offset.Zero) }

    var dropValueX by remember { mutableStateOf(0f) }
    var dropValueY by remember { mutableStateOf(0f) }
    var dropValueZ by remember { mutableStateOf(0f) }

    val tabIndex = vm.value.tabIndex.collectAsState()
    val helpText by remember { vm.value.tortoise.helpText }
    val matrix = remember { vm.value.tortoise.matrix }
    val alternative = remember { vm.value.box.alternative }
    val stateText = remember { mutableStateOf("") }
    val menu = vm.value.template.menu.collectAsState(TemplateInfo.EMPTY)
    val figureList = remember(figures) {
        derivedStateOf { IFigure.list(figures.value) }
    }

    val selectedItem = remember(figures) { mutableStateOf<IFigure>(FigureEmpty) }
    val checkboxEditor = vm.value.template.checkboxEditor.collectAsState()

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
                    Box(
                        modifier = Modifier.fillMaxSize().weight(1f)
                    ) {
                        when (tabIndex.value) {
                            BoxDrawerToolBar.TAB_TORTOISE -> {
                                Text(
                                    text = helpText,
                                    modifier = Modifier.width(350.dp).wrapContentHeight()
                                        .align(Alignment.TopStart).padding(8.dp),
                                    fontSize = 10.sp,
                                    lineHeight = 12.sp,
                                )
                            }

                            BoxDrawerToolBar.TAB_TOOLS -> {
                                Row(
                                    modifier = Modifier.Companion.align(Alignment.TopStart)
                                ) {
                                    TemplateEditorBox(
                                        menu = menu,
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
                                        dropValueX = it; vm.value.tortoise.rotate(
                                        dropValueX,
                                        dropValueY,
                                        dropValueZ
                                    )
                                    },
                                    value = dropValueX,
                                    valueRange = -360f..360f
                                )

                                Slider(
                                    modifier = Modifier.wrapContentHeight(),
                                    onValueChange = {
                                        dropValueY = it; vm.value.tortoise.rotate(
                                        dropValueX,
                                        dropValueY,
                                        dropValueZ
                                    )
                                    },
                                    value = dropValueY,
                                    valueRange = -360f..360f
                                )
                                Slider(
                                    modifier = Modifier.wrapContentHeight(),
                                    onValueChange = {
                                        dropValueZ = it; vm.value.tortoise.rotate(
                                        dropValueX,
                                        dropValueY,
                                        dropValueZ
                                    )
                                    },
                                    value = dropValueZ,
                                    valueRange = -360f..360f
                                )
                            }
                        } else {
                            if (tabIndex.value == BoxDrawerToolBar.TAB_TOOLS && !checkboxEditor.value) {
                                Box(
                                    modifier = Modifier.align(Alignment.TopEnd).width(180.dp)
                                ) {
                                    FigureListBox(figureList.value, selectedItem) { f ->
                                        selectedItem.value = f
                                    }
                                }
                            }
                        }
                    }
                    StatusBar(displayScale, pos, stateText, onHomeClick = {
                        dropValueX = 0f
                        dropValueY = 0f
                        dropValueZ = 0f
                        pos.value = Offset.Zero
                        vm.value.tortoise.rotate(
                            dropValueX,
                            dropValueY,
                            dropValueZ
                        )
                    })
                }
            }
        }
    }
}

@Composable
fun FigureListBox(figure: List<IFigure>, selectedItem: State<IFigure>, onClick: (IFigure) -> Unit) {
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()


    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        state = scrollState,
        modifier = Modifier.padding(start = 1.dp).fillMaxSize()
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { delta ->
                    coroutineScope.launch {
                        scrollState.scrollBy(-delta)
                    }
                },
            )
    ) {
        FigureItems(figures = figure, selectedItem = selectedItem, onClick = onClick)
    }

}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.FigureItems(
    figures: List<IFigure>,
    selectedItem: State<IFigure>,
    onClick: (IFigure) -> Unit
) {
    items(figures) { figure ->
        Column(
            modifier = Modifier
                .border(1.dp, ThemeColors.figureListBorder, ThemeColors.figureListItemShape)
                .background(
                    if (figure === selectedItem.value)
                        MaterialTheme.colors.primary else
                        ThemeColors.figureListBackground, ThemeColors.figureListItemShape
                )
                .width(300.dp).onClick {
                    onClick(figure)
                }
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = figure.name(),
                maxLines = 3,
                overflow = TextOverflow.Clip,
                textAlign = TextAlign.End
            )
        }
    }

//    items(figure.collection()) { f ->
//        FigureListBox(f)
//    }
}

