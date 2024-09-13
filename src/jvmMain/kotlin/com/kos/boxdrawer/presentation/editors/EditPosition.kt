package com.kos.boxdrawer.presentation.editors

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.DropdownMenuState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Create
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kos.boxdrawe.icons.Expand
import com.kos.boxdrawe.icons.IconCopy
import com.kos.boxdrawe.widget.ImageButton
import com.kos.boxdrawe.widget.Label
import com.kos.boxdrawe.widget.LabelLight
import com.kos.boxdrawe.widget.NumericTextFieldState
import com.kos.boxdrawe.widget.NumericUpDownLine
import com.kos.boxdrawe.widget.RunButton
import com.kos.boxdrawe.widget.verticalScrollbar
import com.kos.boxdrawer.presentation.template.TemplateAngleBox
import com.kos.boxdrawer.presentation.template.TemplateBox
import com.kos.boxdrawer.presentation.template.TemplateSimpleItemBox
import com.kos.boxdrawer.presentation.template.TemplateSizeBox
import com.kos.boxdrawer.template.TemplateCreator
import com.kos.boxdrawer.template.TemplateFigureBuilderListener
import com.kos.boxdrawer.template.TemplateForm
import com.kos.boxdrawer.template.TemplateGeneratorListener
import com.kos.boxdrawer.template.TemplateGeneratorSimpleListener
import com.kos.boxdrawer.template.TemplateInfo
import com.kos.boxdrawer.template.TemplateItemAngle
import com.kos.boxdrawer.template.TemplateItemSize
import kotlinx.coroutines.launch
import turtoise.help.HelpData
import turtoise.help.HelpInfoCommand
import turtoise.help.TortoiseHelpInfo
import turtoise.parser.TPArg
import turtoise.parser.TortoiseParserStackBlock

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun EditPosition(
    expanded: MutableState<Boolean>,
    commands: State<List<HelpInfoCommand>>,
    moveListener: TemplateGeneratorSimpleListener,
    editorListener: TemplateFigureBuilderListener,
    onPickSelected: () -> String
) {
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    Column(
    ) {
        Row {
            LabelLight("Добавить перемещение", modifier = Modifier.clickable {
                expanded.value = !expanded.value
            })
            ImageButton(
                if (expanded.value) Expand.rememberExpandLess() else Expand.rememberExpandMore(),
                modifier = Modifier,
                enabled = true
            ) {
                expanded.value = !expanded.value
            }
        }
        AnimatedVisibility(expanded.value) {
            Column(modifier = Modifier) {
                TemplateSizeBox(
                    TemplateItemSize("x y", "xy"),
                    null, "xy",
                    moveListener,
                )
                TemplateAngleBox(
                    TemplateItemAngle("a", "a"),
                    null, "a",
                    moveListener,
                )

                val inputPos =
                    remember("pos") {
                        NumericTextFieldState(
                            value = 0.0,
                            minValue = -1000000.0,
                        ) { v ->
                            moveListener.put(
                                "pos",
                                v.toString()
                            )
                        }
                    }

                Row {
                    LabelLight(
                        "Вставить значение",
                        modifier = Modifier.onPointerEvent(PointerEventType.Press) {
                            val t = onPickSelected()
                            inputPos.update(t)
                        })
                }
                Row() {
                    NumericUpDownLine("", "", inputPos, modifier = Modifier.weight(1f))
                }

                val expandedMenuState = remember { DropdownMenuState() }
                val subMenuExpand = remember { DropdownMenuState() }

                val selectedFigure = remember {
                    mutableStateOf(
                        commands.value.firstOrNull() ?: HelpInfoCommand(
                            "",
                            emptyList()
                        )
                    )
                }
                val selectedCommandData = remember { mutableStateOf(HelpData("", "")) }
                Text(
                    AnnotatedString(
                        selectedFigure.value.name
                    ) + AnnotatedString(" ") +
                            AnnotatedString(
                                selectedCommandData.value.argument,
                                SpanStyle(
                                    color = Color(0xFF26A530),
                                )
                            ),
                    modifier = Modifier.fillMaxWidth().clickable {
                        expandedMenuState.status = DropdownMenuState.Status.Open(Offset.Zero)
                    }.padding(4.dp)
                )

                CommandMenu(expandedMenuState, commands.value) { com ->
                    selectedFigure.value = com
                    selectedCommandData.value = com.data.firstOrNull() ?: HelpData("", "")
                    if (selectedFigure.value.data.size > 1) {
                        subMenuExpand.status = DropdownMenuState.Status.Open(Offset.Zero)
                    }
                    editorListener.setFigure(selectedFigure.value, selectedCommandData.value)
                }
                Text(
                    AnnotatedString(selectedCommandData.value.description),
                    modifier = Modifier.fillMaxWidth().clickable {
                        subMenuExpand.status = DropdownMenuState.Status.Open(Offset.Zero)
                    }.padding(4.dp),
                    fontSize = 10.sp,
                    lineHeight = 12.sp,
                    color = MaterialTheme.colors.onPrimary
                )

                DropDownMenuCommandData(selectedFigure.value, subMenuExpand) { f, d ->
                    selectedCommandData.value = d
                    editorListener.setFigure(selectedFigure.value, selectedCommandData.value)
                }

                val form = remember(selectedFigure.value, selectedCommandData.value) {
                    editorListener.getForm()
                }
                CommandModel(form, editorListener)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RunButton("Готово") {
                        editorListener.insertFigure()
                    }
                    ImageButton(
                        icon = IconCopy.rememberContentCopy(),
                        onClick = {
                            coroutineScope.launch {
                                clipboardManager.setText(AnnotatedString(editorListener.currentText.value))
                            }
                        }
                    )
                }
                Text(editorListener.currentText.value)
            }
        }
    }
}

@Composable
fun CommandModel(
    form: TemplateForm,
    listener: TemplateGeneratorListener
) {

    val menu = remember(form) {
        mutableStateOf(
            TemplateInfo(
                form = form,
                values = TortoiseParserStackBlock(),
                edit = false,
            )
        )
    }
    Column {
        TemplateBox(
            modifier = Modifier,
            menu,
            listener
        )
    }
}

@Composable
private fun CommandMenu(
    expandedMenuState: DropdownMenuState,
    commands: List<HelpInfoCommand>,
    onSelect: (HelpInfoCommand) -> Unit
) {
    DropdownMenu(
        state = expandedMenuState,
    ) {
        commands.forEach { com ->
            DropdownMenuItem(onClick = {
                expandedMenuState.status = DropdownMenuState.Status.Closed
                onSelect(com)
            }) {
                Text(
                    com.name,
                    modifier = Modifier.width(32.dp),
                    fontSize = 14.sp,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    com.description.takeIf { it.isNotEmpty() }
                        ?: com.data.firstOrNull()?.description.orEmpty(),

                    fontSize = 12.sp,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@Composable
fun DropDownMenuCommandData(
    info: HelpInfoCommand,
    expandedMenuState: DropdownMenuState,
    onSelect: (
        HelpInfoCommand, HelpData
    ) -> Unit
) {
    DropdownMenu(
        state = expandedMenuState,
    ) {
        info.data.forEach { com ->
            DropdownMenuItem(onClick = {
                expandedMenuState.status = DropdownMenuState.Status.Closed
                onSelect(info, com)
            }) {
                Column {
                    Text(
                        com.argument,
                        fontSize = 14.sp,
                    )
                    Text(
                        com.description,
                        fontSize = 12.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}