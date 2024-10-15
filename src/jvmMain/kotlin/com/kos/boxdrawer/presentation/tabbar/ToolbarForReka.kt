package com.kos.boxdrawer.presentation.tabbar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.KeyboardArrowDown
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
import com.kos.boxdrawe.widget.CircleBox
import com.kos.boxdrawe.widget.ImageButton
import com.kos.boxdrawe.widget.Label
import com.kos.boxdrawe.widget.RunButton
import com.kos.boxdrawe.widget.SaveToFileButton
import com.kos.boxdrawe.widget.SimpleEditText
import com.kos.boxdrawer.generated.resources.Res
import com.kos.boxdrawer.generated.resources.rekaAlertClearCancel
import com.kos.boxdrawer.generated.resources.rekaAlertClearSuccess
import com.kos.boxdrawer.generated.resources.rekaAlertClearText
import com.kos.boxdrawer.generated.resources.rekaAlertClearTitle
import com.kos.boxdrawer.generated.resources.rekaGroupAngle
import com.kos.boxdrawer.generated.resources.rekaGroupShift
import com.kos.boxdrawer.generated.resources.rekaPadding
import com.kos.boxdrawer.generated.resources.rekaPoints
import com.kos.boxdrawer.generated.resources.toolsButtonCopyCode
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import turtoise.rect.Kubik.Companion.STORONA_C
import turtoise.rect.Kubik.Companion.STORONA_CL
import turtoise.rect.Kubik.Companion.STORONA_CR
import turtoise.rect.Kubik.Companion.STORONA_L
import turtoise.rect.Kubik.Companion.STORONA_R

@Composable
fun ToolbarForReka(vm: RekaToolsData) {

    val scope = rememberCoroutineScope()
    val text = remember { vm.points }
    val paddingText = remember { vm.paddingNext }

    val shiftValue = remember { vm.shiftValue }
    val angleValue = remember { vm.angleValue }

    val position = vm.current.collectAsState()

    val visibleClearAlert = remember { mutableStateOf<Boolean>(false) }
    if (visibleClearAlert.value) {
        AlertDialog(
            onDismissRequest = {
                visibleClearAlert.value = false
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        vm.clearBox()
                    }
                    visibleClearAlert.value = false
                }) {
                    stringResource(Res.string.rekaAlertClearSuccess)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    visibleClearAlert.value = false
                }) {
                    stringResource(Res.string.rekaAlertClearCancel)
                }
            },
            title = {
                Text(stringResource(Res.string.rekaAlertClearTitle))
            },
            text = {
                Text(stringResource(Res.string.rekaAlertClearText))
            }
        )
    }

    Row(
        modifier = TabContentModifier
    ) {
        Column(
            modifier = Modifier.weight(weight = 4f, fill = true).padding(end = 8.dp)
        ) {
            SimpleEditText(
                stringResource(Res.string.rekaPoints), "", text,
                fieldMaxWidth = Dp.Unspecified
            ) { t ->
                vm.setPoints(t)
            }
            SimpleEditText(
                stringResource(Res.string.rekaPadding), "", paddingText,
                fieldMaxWidth = Dp.Unspecified
            ) { t ->
                vm.setPadding(t)
            }
            Label(
                text = "${position.value.position.edge} : ${position.value.position.storona} : ${position.value.position.block}",
                modifier = Modifier.align(Alignment.End)
            )
        }
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true).padding(end = 2.dp)
        ) {
            Label(   stringResource(Res.string.rekaGroupShift))
            CircleBox(Modifier.size(160.dp)) { current, change, start ->
                vm.moveCurrentReka(change)
            }
            SimpleEditText("", "", shiftValue) {}
        }
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true).padding(end = 2.dp)
        ) {
            Label(   stringResource(Res.string.rekaGroupAngle))
            CircleBox(Modifier.size(160.dp)) { current, change, start ->
                vm.rotateCurrentReka(change)
            }
            SimpleEditText("", "", angleValue) {}
        }
        Column(
            modifier = Modifier.weight(weight = 2f, fill = true).padding(end = 2.dp)
        ) {

            Column(
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
                                scope.launch {
                                    vm.updateBox()
                                }
                            }
                        )
                        ImageButton(
                            icon = Icons.Rounded.Delete,
                            onClick = {

                                scope.launch {
                                    vm.removeBox()
                                }
                            }
                        )
                        ImageButton(
                            icon = Icons.Rounded.Clear,
                            onClick = {
                                visibleClearAlert.value = true
                            }
                        )
                    }

                }
                ImageButton(
                    icon = Icons.Rounded.KeyboardArrowUp,
                    onClick = {
                        scope.launch {
                            vm.selectPosition(DOWN_BLOCK)
                        }
                    }
                )
                Row {
                    CharButton(
                        text = "L",
                        modifier = Modifier.defaultMinSize(24.dp),
                        onClick = {
                            scope.launch {
                                vm.selectPosition(STORONA_L)
                            }
                        }

                    )
                    CharButton(
                        text = "CL",
                        modifier = Modifier.defaultMinSize(24.dp),
                        onClick = {
                            scope.launch {
                                vm.selectPosition(STORONA_CL)
                            }
                        }
                    )
                    CharButton(
                        text = "C",
                        modifier = Modifier.defaultMinSize(24.dp),
                        onClick = {
                            scope.launch {
                                vm.selectPosition(STORONA_C)
                            }
                        }
                    )
                    CharButton(
                        text = "CR",
                        modifier = Modifier.defaultMinSize(24.dp),
                        onClick = {
                            scope.launch {
                                vm.selectPosition(STORONA_CR)
                            }
                        }
                    )
                    CharButton(
                        text = "R",
                        modifier = Modifier.defaultMinSize(24.dp),
                        onClick = {
                            scope.launch {
                                vm.selectPosition(STORONA_R)
                            }
                        }
                    )
                }
                Row(
                    modifier = Modifier,
                ) {
                    ImageButton(
                        icon = Icons.AutoMirrored.Rounded.ArrowBack,
                        onClick = {
                            scope.launch {
                                vm.selectPosition(BACK_EDGE)
                            }
                        }
                    )
                    ImageButton(
                        icon = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                        onClick = {
                            scope.launch {
                                vm.selectPosition(BACK_BLOCK)
                            }
                        }
                    )

                    ImageButton(
                        icon = Icons.AutoMirrored.Rounded.KeyboardArrowRight,

                        onClick = {
                            scope.launch {
                                vm.selectPosition(NEXT_BLOCK)
                            }
                        }
                    )
                    ImageButton(
                        icon = Icons.AutoMirrored.Rounded.ArrowForward,
                        onClick = {
                            scope.launch {
                                vm.selectPosition(NEXT_EDGE)
                            }
                        }
                    )
                }

                ImageButton(
                    icon = Icons.Rounded.KeyboardArrowDown,
                    modifier = Modifier,
                    onClick = {
                        scope.launch {
                            vm.selectPosition(UP_BLOCK)
                        }
                    }
                )
                ImageButton(
                    icon = Icons.Rounded.AddCircle,
                    onClick = {
                        vm.createBox()
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
        SaveToFileButton(vm)

        Spacer(Modifier.height(4.dp))
        RunButton(stringResource(Res.string.toolsButtonCopyCode)) {
            coroutineScope.launch {
                clipboardManager.setText(AnnotatedString(vm.print()))
            }
        }
    }
}
