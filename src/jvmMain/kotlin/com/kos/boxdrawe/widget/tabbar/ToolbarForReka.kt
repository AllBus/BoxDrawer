package com.kos.boxdrawe.widget.tabbar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.presentation.RekaToolsData
import com.kos.boxdrawe.presentation.RekaToolsData.Companion.BACK_BLOCK
import com.kos.boxdrawe.presentation.RekaToolsData.Companion.BACK_EDGE
import com.kos.boxdrawe.presentation.RekaToolsData.Companion.DOWN_BLOCK
import com.kos.boxdrawe.presentation.RekaToolsData.Companion.NEXT_BLOCK
import com.kos.boxdrawe.presentation.RekaToolsData.Companion.NEXT_EDGE
import com.kos.boxdrawe.presentation.RekaToolsData.Companion.UP_BLOCK
import com.kos.boxdrawe.widget.CharButton
import com.kos.boxdrawe.widget.ImageButton
import com.kos.boxdrawe.widget.Label
import com.kos.boxdrawe.widget.RunButton
import com.kos.boxdrawe.widget.SimpleEditText
import com.kos.boxdrawe.widget.TabContentModifier
import com.kos.boxdrawe.widget.showFileChooser
import kotlinx.coroutines.launch
import turtoise.rect.Kubik.Companion.STORONA_C
import turtoise.rect.Kubik.Companion.STORONA_CL
import turtoise.rect.Kubik.Companion.STORONA_CR
import turtoise.rect.Kubik.Companion.STORONA_L
import turtoise.rect.Kubik.Companion.STORONA_R

@Composable
fun ToolbarForReka(vm: RekaToolsData) {

    val text = remember { mutableStateOf<String>("") }
    val paddingText = remember { mutableStateOf<String>("0") }
    val position = vm.current.collectAsState()

    Row(
        modifier = TabContentModifier
    ) {
        Column(
            modifier = Modifier.weight(weight = 4f, fill = true).padding(end = 8.dp)
        ) {
            SimpleEditText("Точки", "", text,
                fieldMaxWidth = Dp.Unspecified) { t ->
                text.value = t
                vm.setPoints(t) }
            SimpleEditText("Отступ", "", paddingText,
                fieldMaxWidth = Dp.Unspecified) {t ->
                paddingText.value = t
                vm.setPadding(t) }
            RunButton("Обновить текущий", modifier = Modifier.align(Alignment.End)){
                vm.updateBox()
            }
            Label(
                text = "${position.value.position.edge} : ${position.value.position.storona} : ${position.value.position.block}",
                modifier = Modifier.align(Alignment.End)
            )
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

                }
                ImageButton(
                    icon = Icons.Rounded.KeyboardArrowUp,
                    onClick = {
                        vm.selectPosition(DOWN_BLOCK)
                    }
                )
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
                        text = "C",
                        modifier = Modifier.defaultMinSize(24.dp),
                        onClick = {
                            vm.selectPosition(STORONA_C)
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

@Composable
fun ToolbarActionForReka(vm: RekaToolsData) {
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    Column(
    ) {

        RunButton("Нарисовать деталь") {
            coroutineScope.launch {
                showFileChooser(vm.tools.chooserDir()) { f ->
                    coroutineScope.launch {
                        vm.save(f)
                    }
                }
            }
        }

        Spacer(Modifier.height(4.dp))
        RunButton("Скопировать код") {
            coroutineScope.launch {
                clipboardManager.setText(AnnotatedString(vm.print()))
            }
        }
    }
}
