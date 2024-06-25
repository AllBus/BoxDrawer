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
import androidx.compose.foundation.onClick
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.DropdownMenuState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kos.boxdrawe.icons.Expand
import com.kos.boxdrawe.widget.ImageButton
import com.kos.boxdrawe.widget.Label
import com.kos.boxdrawe.widget.NumericTextFieldState
import com.kos.boxdrawe.widget.NumericUpDownLine
import com.kos.boxdrawer.presentation.template.TemplateAngleBox
import com.kos.boxdrawer.presentation.template.TemplateSizeBox
import com.kos.boxdrawer.template.TemplateGeneratorSimpleListener
import com.kos.boxdrawer.template.TemplateItemAngle
import com.kos.boxdrawer.template.TemplateItemSize
import turtoise.help.HelpInfoCommand
import turtoise.help.TortoiseHelpInfo

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun EditPosition(listener: TemplateGeneratorSimpleListener, onPickSelected : () -> String) {
    val expanded = remember{ mutableStateOf(false) }
    Column(
    ) {
        Row {
            Label("Добавить перемещение", modifier = Modifier.clickable {
                expanded.value = !expanded.value
            })
            ImageButton(
                if (expanded.value) Expand.rememberExpandLess() else Expand.rememberExpandMore(),
                modifier = Modifier,
                enabled = true
            ){
                expanded.value = !expanded.value
            }
        }
        AnimatedVisibility(expanded.value) {
            Column {
                TemplateSizeBox(
                    TemplateItemSize("x y", "xy"),
                    null, "xy",
                    listener,
                )
                TemplateAngleBox(
                    TemplateItemAngle("a", "a"),
                    null, "a",
                    listener,
                )
                TemplateSizeBox(
                    TemplateItemSize("x y", "axy"),
                    null, "axy",
                    listener,
                )


                val inputPos =
                    remember("pos") {
                        NumericTextFieldState(
                            value = 0.0,
                            minValue = -1000000.0,
                        ) { v ->
                            listener.put(
                                "pos",
                                v.toString()
                            )
                        }
                    }

                Row {
                    Label("Вставить значение", modifier = Modifier.onPointerEvent(PointerEventType.Press){
                        val t=  onPickSelected()
                        inputPos.update(t)
                    })
//                    ImageButton(
//                        Icons.Rounded.Edit,
//                        modifier = Modifier.focusable(false),
//                        enabled = true
//                    ){
//                        val t=  onPickSelected()
//                        println("pick "+t)
//                        inputPos.update(t)
//                    }
                }
                Row() {
                    NumericUpDownLine("", "", inputPos, modifier = Modifier.weight(1f))
                }


                val commands = remember { TortoiseHelpInfo().commandList }
                val expandedMenuState = remember { DropdownMenuState() }

                val selectedFigure =  remember{ mutableStateOf(commands.firstOrNull()?: HelpInfoCommand("", emptyList())) }
                Text(
                    AnnotatedString(
                        selectedFigure.value.name
                    ) + AnnotatedString(" ")+
                            AnnotatedString(selectedFigure.value.data.firstOrNull()?.argument.orEmpty(),
                                SpanStyle(
                                    color = Color(0xFF26A530),
                                ) )
                    ,
                    modifier = Modifier.fillMaxWidth().clickable {
                        expandedMenuState.status = DropdownMenuState.Status.Open(Offset.Zero)
                    }.padding(4.dp)
                )
                DropdownMenu(
                    state = expandedMenuState,
                ){
                    commands.forEach { com ->
                        DropdownMenuItem(onClick = {
                            selectedFigure.value = com
                            expandedMenuState.status = DropdownMenuState.Status.Closed
                        }){
                            Text(com.name,
                                fontSize = 14.sp,)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(com.data.firstOrNull()?.description.orEmpty(),

                                fontSize = 12.sp,
                                lineHeight = 14.sp
                                )
                        }
                    }
                }
            }
        }
    }
}