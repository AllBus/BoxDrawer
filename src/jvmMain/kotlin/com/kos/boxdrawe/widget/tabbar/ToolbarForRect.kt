package com.kos.boxdrawe.widget.tabbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.presentation.RectToolsData
import com.kos.boxdrawe.presentation.RectToolsData.Companion.BACK_BLOCK
import com.kos.boxdrawe.presentation.RectToolsData.Companion.BACK_EDGE
import com.kos.boxdrawe.presentation.RectToolsData.Companion.DOWN_BLOCK
import com.kos.boxdrawe.presentation.RectToolsData.Companion.NEXT_BLOCK
import com.kos.boxdrawe.presentation.RectToolsData.Companion.NEXT_EDGE
import com.kos.boxdrawe.presentation.RectToolsData.Companion.STORONA_CL
import com.kos.boxdrawe.presentation.RectToolsData.Companion.STORONA_CR
import com.kos.boxdrawe.presentation.RectToolsData.Companion.STORONA_L
import com.kos.boxdrawe.presentation.RectToolsData.Companion.STORONA_R
import com.kos.boxdrawe.presentation.RectToolsData.Companion.UP_BLOCK
import com.kos.boxdrawe.widget.CharButton
import com.kos.boxdrawe.widget.EditTextField
import com.kos.boxdrawe.widget.ImageButton
import com.kos.boxdrawe.widget.RunButton
import com.kos.boxdrawe.widget.SimpleEditText
import com.kos.boxdrawe.widget.TabContentModifier
import com.kos.figure.FigureList
import com.kos.figure.IFigure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import turtoise.Tortoise
import turtoise.rect.EStorona
import turtoise.rect.RectBlock
import turtoise.rect.RectBlockEdges
import turtoise.rect.RectBlockParent
import vectors.Vec2

@Composable
fun ToolbarForRect(vm: RectToolsData) {

    val text = remember { mutableStateOf<String>("20, 20") }

    Row(
        modifier = TabContentModifier
    ) {
        Column(
            modifier = Modifier.weight(weight = 4f, fill = true).padding(end = 8.dp)
        ) {
            SimpleEditText("Точки", "", text) {t ->
                text.value = t
                vm.setPoints(t) }
        }
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true).padding(end = 2.dp).verticalScroll(
                rememberScrollState()
            )
        ) {

        }
        Column(
            modifier = Modifier.weight(weight = 2f, fill = true).padding(end = 2.dp).verticalScroll(
                rememberScrollState()
            )
        ) {

            Column (
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier,
                ) {
                    Row {
                        ImageButton(
                            icon = Icons.Rounded.Create,
                            onClick = {
                                vm.createBox()
                            }
                        )
                        ImageButton(
                            icon = Icons.Rounded.Delete,
                            onClick = {
                                vm.removeBox()
                            }
                        )
                    }
                    ImageButton(
                        icon = Icons.Rounded.KeyboardArrowUp,
                        onClick = {
                            vm.selectPosition(DOWN_BLOCK)
                        }
                    )
                }
                Row{
                    CharButton(
                        text = "L",
                        modifier = Modifier.defaultMinSize(24.dp),
                        onClick = {
                            vm.selectPosition(STORONA_L)
                        }

                    )
                    CharButton(
                        text = "CL",
                        modifier = Modifier.defaultMinSize(24.dp),
                        onClick = {
                            vm.selectPosition(STORONA_CL)
                        }
                    )
                    CharButton(
                        text = "CR",
                        modifier = Modifier.defaultMinSize(24.dp),
                        onClick = {
                            vm.selectPosition(STORONA_CR)
                        }
                    )
                    CharButton(
                        text = "R",
                        modifier = Modifier.defaultMinSize(24.dp),
                        onClick = {
                            vm.selectPosition(STORONA_R)
                        }
                    )
                }
                Row(
                    modifier = Modifier,
                ) {
                    ImageButton(
                    icon = Icons.Rounded.ArrowBack,
                    onClick = {
                        vm.selectPosition(BACK_EDGE)
                    }
                )
                    ImageButton(
                        icon = Icons.Rounded.KeyboardArrowLeft,
                        onClick = {
                            vm.selectPosition(BACK_BLOCK)
                        }
                    )

                    ImageButton(
                        icon = Icons.Rounded.KeyboardArrowRight,

                        onClick = {
                            vm.selectPosition(NEXT_BLOCK)
                        }
                    )
                    ImageButton(
                        icon = Icons.Rounded.ArrowForward,
                        onClick = {
                            vm.selectPosition(NEXT_EDGE)
                        }
                    )
                }

                ImageButton(
                    icon = Icons.Rounded.KeyboardArrowDown,
                    modifier = Modifier,
                    onClick = {
                        vm.selectPosition( UP_BLOCK)
                    }
                )


            }
        }
    }

}


