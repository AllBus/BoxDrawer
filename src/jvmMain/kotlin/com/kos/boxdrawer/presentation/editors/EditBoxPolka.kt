package com.kos.boxdrawer.presentation.editors

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.presentation.BoxSimpleListener
import com.kos.boxdrawe.themes.ThemeColors
import com.kos.boxdrawe.widget.AxisBox
import com.kos.boxdrawe.widget.ImageButton
import com.kos.boxdrawe.widget.Label
import com.kos.boxdrawe.widget.NumericTextFieldState
import com.kos.boxdrawe.widget.NumericUpDown
import com.kos.boxdrawe.widget.NumericUpDownLine
import com.kos.boxdrawe.widget.ToggleButton
import com.kos.boxdrawer.generated.resources.Res
import com.kos.boxdrawer.generated.resources.metricMM
import com.kos.boxdrawer.presentation.template.TemplateNumericBox
import com.kos.boxdrawer.template.TemplateGeneratorSimpleListener
import com.kos.boxdrawer.template.TemplateItemAngle
import org.jetbrains.compose.resources.stringResource
import vectors.Vec2

@Composable
fun EditBoxPolka(boxListener : BoxSimpleListener) {
    Column(
    ) {
        Label("Добавить полку")
        Row {

            val checkedH = remember { mutableStateOf(false) }
            val checkedE = remember { mutableStateOf(false) }
            val checkedC = remember { mutableStateOf(false) }
            val input =
                remember("p") {
                    NumericTextFieldState(
                        value = 10.0,
                        minValue = 0.0,
                    ) { v ->
                        boxListener.updateLine(
                            v,
                            checkedH.value,
                            checkedC.value,
                            checkedE.value
                        )
                    }
                }

            Row() {
                NumericUpDownLine(
                    "",
                    stringResource(Res.string.metricMM),
                    input,
                    modifier = Modifier.weight(1f)
                )
                ToggleButton(modifier = Modifier.size(45.dp, 30.dp).align(Alignment.CenterVertically),
                    "⇆", "⇅", checkedH.value) { n ->
                    checkedH.value = n
                    boxListener.updateLine(
                        input.decimal,
                        checkedH.value,
                        checkedC.value,
                        checkedE.value
                    )
                }
                ToggleButton(
                    modifier = Modifier.size(45.dp, 30.dp).align(Alignment.CenterVertically),
                    if (checkedH.value) "↧" else "↦",
                    if (checkedH.value) "↥" else "↤",
                    checkedE.value
                ) { n ->
                    checkedE.value = n
                    boxListener.updateLine(
                        input.decimal,
                        checkedH.value,
                        checkedC.value,
                        checkedE.value
                    )
                }
                ToggleButton(modifier = Modifier.size(45.dp, 30.dp).align(Alignment.CenterVertically),
                    "↸", "⇹", checkedC.value) { n ->
                    checkedC.value = n
                    boxListener.updateLine(
                        input.decimal,
                        checkedH.value,
                        checkedC.value,
                        checkedE.value
                    )
                }

                val icon = Icons.Rounded.Add
                ImageButton(
                    icon,
                    Modifier.wrapContentSize().align(Alignment.CenterVertically)
                ) {
//                checkedH.value = false
//                checkedE.value = false
//                checkedC.value = false
//                input.update(10.0)
                    boxListener.clearSelect()
                }
            }
        }
        Row{
            val inputX =
                remember("px") {
                    NumericTextFieldState(
                        value = 1.0,
                        minValue = 1.0,
                        maxValue = 100.0,
                        digits = 0,
                    ) { v ->
                        boxListener.updateGrid(
                            v.toInt(),
                            0,
                        )
                    }
                }
            val inputY =
                remember("py") {
                    NumericTextFieldState(
                        value = 1.0,
                        minValue = 1.0,
                        maxValue = 100.0,
                        digits = 0,
                    ) { v ->
                        boxListener.updateGrid(
                            inputX.decimal.toInt(),
                            v.toInt(),
                        )
                    }
                }


            NumericUpDown("", "", inputX, modifier = Modifier.weight(1f))
            NumericUpDown("", "", inputY, modifier = Modifier.weight(1f))
            AxisBox(  modifier = Modifier.size(20.dp, ThemeColors.NumericFieldHeight).padding(1.dp)
                .background(ThemeColors.inputBackgroundState(true)),
                { Vec2(inputX.decimal, inputY.decimal) }) { current, change, start ->
                    inputX.update(current.x)
                    inputY.update(current.y)
                }
        }

    }
}