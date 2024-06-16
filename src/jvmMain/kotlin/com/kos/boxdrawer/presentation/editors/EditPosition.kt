package com.kos.boxdrawer.presentation.editors

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import com.kos.boxdrawe.icons.Expand
import com.kos.boxdrawe.widget.ImageButton
import com.kos.boxdrawe.widget.Label
import com.kos.boxdrawe.widget.NumericTextFieldState
import com.kos.boxdrawe.widget.NumericUpDownLine
import com.kos.boxdrawer.presentation.template.TemplateAngleBox
import com.kos.boxdrawer.presentation.template.TemplateNumericBox
import com.kos.boxdrawer.presentation.template.TemplateSizeBox
import com.kos.boxdrawer.template.TemplateGeneratorSimpleListener
import com.kos.boxdrawer.template.TemplateItemAngle
import com.kos.boxdrawer.template.TemplateItemNumeric
import com.kos.boxdrawer.template.TemplateItemSize

@OptIn(ExperimentalComposeUiApi::class)
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
            }
        }
    }
}