package com.kos.boxdrawer.presentation

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import calcZoom
import com.kos.boxdrawe.presentation.DrawerViewModel
import com.kos.boxdrawe.themes.ThemeColors
import com.kos.boxdrawe.widget.BoxDrawerToolBar
import com.kos.boxdrawe.widget.TabBar
import com.kos.boxdrawe.widget.display.DisplayBezier
import com.kos.boxdrawe.widget.display.DisplayGrid
import com.kos.boxdrawe.widget.display.DisplayTortoise
import com.kos.boxdrawer.template.TemplateForm
import com.kos.boxdrawer.template.TemplateInfo
import com.kos.figure.FigureEmpty
import turtoise.TurtoiseParserStackBlock

@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Preview
fun App(vm: State<DrawerViewModel>) {

    val figures = vm.value.figures.collectAsState(FigureEmpty)

    val displayScale = remember { mutableFloatStateOf(2.0f) }

    var dropValueX by remember { mutableStateOf(0f) }
    var dropValueY by remember { mutableStateOf(0f) }
    var dropValueZ by remember { mutableStateOf(0f) }

    val tabIndex = vm.value.tabIndex.collectAsState()
    val helpText by remember { vm.value.tortoise.helpText }
    val matrix = remember { vm.value.tortoise.matrix }
    val alternative = remember { vm.value.box.alternative }
    val stateText = remember { mutableStateOf("") }
    val menu = vm.value.template.menu.collectAsState(TemplateInfo(TemplateForm("", "", emptyList()), TurtoiseParserStackBlock()))

    MaterialTheme {
        Column {
            TabBar(BoxDrawerToolBar.tabs, vm)

            Box(modifier = Modifier.fillMaxSize().background(Color(0xFF02007C))) {
                when (tabIndex.value) {
                    BoxDrawerToolBar.TAB_TORTOISE -> {
                        DisplayTortoise(displayScale, matrix, true, figures.value) { text ->
                            stateText.value = text
                        }
                        Text(
                            text = helpText,
                            modifier = Modifier.width(350.dp).wrapContentHeight()
                                .align(Alignment.TopStart).padding(8.dp),
                            fontSize = 10.sp,
                            lineHeight = 12.sp,
                        )
                    }

                    BoxDrawerToolBar.TAB_BOX -> {
                        DisplayTortoise(
                            displayScale,
                            matrix,
                            !alternative.value,
                            figures.value
                        ) { text ->
                            stateText.value = text
                        }
                    }

                    BoxDrawerToolBar.TAB_GRID -> {
                        DisplayGrid(vm.value.grid)
                    }

                    BoxDrawerToolBar.TAB_BEZIER -> DisplayBezier(displayScale, vm.value.bezier)

                    BoxDrawerToolBar.TAB_SOFT,
                    BoxDrawerToolBar.TAB_BUBLIK -> {
                        DisplayTortoise(
                            displayScale,
                            matrix,
                            false,
                            figures.value,
                        ) { text ->
                            stateText.value = text
                        }
                    }
                    BoxDrawerToolBar.TAB_TOOLS -> {
                        DisplayTortoise(
                            displayScale,
                            matrix,
                            false,
                            figures.value,
                        ) { text ->
                            stateText.value = text
                        }
                        TemplateBox(
                            modifier = Modifier.align(Alignment.TopStart).padding(8.dp).width(250.dp)
                                .verticalScroll(
                                    rememberScrollState()
                                ),
                            menu = menu,
                            templateGenerator = vm.value.template.templateGenerator,
                        )
                    }

                    else -> {

                    }
                }

                Column(
                    Modifier.align(Alignment.TopEnd),
                ) {
                    Text(
                        stateText.value,
                        color = ThemeColors.displayLabelColor,
                    )
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
                }

                Column(
                    modifier = Modifier.width(300.dp).wrapContentHeight()
                        .align(Alignment.BottomEnd),
                ) {
                    Text(
                        "%.3f".format(displayScale.value),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        color = ThemeColors.displayLabelColor
                    )
                    Slider(
                        modifier = Modifier.wrapContentHeight(),
                        onValueChange = {
                            displayScale.value = Math.pow(1.2, (it - 20).toDouble()).toFloat()
                        },
                        value = calcZoom(displayScale.value) + 20, ///log(displayScale.value.toDouble()).toFloat(),
                        valueRange = 1f..100f
                    )
                }
            }
        }
    }
}

