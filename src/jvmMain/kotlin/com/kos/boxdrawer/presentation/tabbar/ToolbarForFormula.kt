package com.kos.boxdrawer.presentation.tabbar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import com.kos.boxdrawe.presentation.FormulaData
import com.kos.boxdrawe.widget.InputText
import com.kos.boxdrawe.widget.Label
import com.kos.boxdrawe.widget.LabelLight
import com.kos.boxdrawe.widget.NumericTextFieldState
import com.kos.boxdrawe.widget.NumericUpDown
import com.kos.boxdrawe.widget.NumericUpDownLine
import com.kos.boxdrawe.widget.RunCheckBox
import com.kos.boxdrawe.widget.RunComboBox
import com.kos.boxdrawe.widget.SaveToFileButton
import com.kos.boxdrawe.widget.SaveToFileIconButton
import com.kos.boxdrawe.widget.model.ComboBoxItem
import com.kos.boxdrawer.generated.resources.Res
import com.kos.boxdrawer.generated.resources.formulaCustom
import com.kos.boxdrawer.generated.resources.formulaCylinder
import com.kos.boxdrawer.generated.resources.formulaEuclide
import com.kos.boxdrawer.generated.resources.formulaSferic
import com.kos.boxdrawer.generated.resources.formulaSystem
import kotlinx.coroutines.flow.mapLatest
import org.jetbrains.compose.resources.stringResource
import vectors.Vec2

@Composable
fun ToolbarForFormula(data: FormulaData) {
    val coroutineScope = rememberCoroutineScope()
    Row(
        modifier = TabContentModifier
    ) {
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {

            val settingsList = remember { listOf(
                ComboBoxItem(0, Res.string.formulaEuclide),
                ComboBoxItem(1, Res.string.formulaCylinder),
                ComboBoxItem(2, Res.string.formulaSferic),
                  //      ComboBoxItem(3, Res.string.formulaCustom),
            ) }
            val selectedMovie = data.selectSystem.mapLatest { s ->  settingsList.find { it.id ==  s}?: settingsList.first() }.collectAsState(settingsList.first())

            RunComboBox(
                label = stringResource(Res.string.formulaSystem),
                selected = selectedMovie.value,
                items = settingsList,
                onClick = { item ->
                    data.setSystem(item.id)
                }
            )
        }
        Column(
            modifier = Modifier.weight(weight = 2f, fill = true)
        ) {
            val xFormula = remember() {
                mutableStateOf(
                    TextFieldValue("")
                )
            }

            val yFormula = remember() {
                mutableStateOf(
                    TextFieldValue("")
                )
            }

            val zFormula = remember() {
                mutableStateOf(
                    TextFieldValue("")
                )
            }

            val variables = remember() {
                mutableStateOf(
                    TextFieldValue("")
                )
            }

            var lineX by remember { data.lineX }
            var lineY by remember { data.lineY }
            var lineZ by remember { data.lineZ }

            Row {
                InputText(
                    value = xFormula.value,
                    onValueChange = remember(data) {
                        {
                            xFormula.value = it
                            data.recalculate(
                                xFormula.value.text,
                                yFormula.value.text,
                                zFormula.value.text,
                                variables.value.text
                            )
                        }},
                    label = "x = ",
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.wrapContentHeight().fillMaxWidth(),
                    enabled = true,
                )
//                RunCheckBox(
//                    checked = lineX,
//                    title = "",
//                    onCheckedChange = { c ->
//                        lineX = c
//                        data.redrawBox()
//                    },
//                )
            }

            Row {
                InputText(
                    value = yFormula.value,
                    onValueChange = {
                        yFormula.value = it
                        data.recalculate(
                            xFormula.value.text,
                            yFormula.value.text,
                            zFormula.value.text,
                            variables.value.text
                        )
                    },
                    label = "y = ",
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.wrapContentHeight().fillMaxWidth(),
                    enabled = true,
                )
//                RunCheckBox(
//                    checked = lineY,
//                    title = "",
//                    onCheckedChange = { c ->
//                        lineY = c
//                        data.redrawBox()
//                    },
//                )
            }

//            Row {
//                InputText(
//                    value = zFormula.value,
//                    onValueChange = {
//                        zFormula.value = it
//                        data.recalculate(
//                            xFormula.value.text,
//                            yFormula.value.text,
//                            zFormula.value.text,
//                            variables.value.text
//                        )
//                    },
//                    label = "z = ",
//                    singleLine = true,
//                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
//                    modifier = Modifier.wrapContentHeight().fillMaxWidth(),
//                    enabled = true,
//                )
////                RunCheckBox(
////                    checked = lineZ,
////                    title = "",
////                    onCheckedChange = { c ->
////                        lineZ = c
////                        data.redrawBox()
////                    },
////                )
//            }
            Row{
                InputText(
                    value = variables.value,
                    onValueChange = {
                        variables.value = it
                        data.recalculate(
                            xFormula.value.text,
                            yFormula.value.text,
                            zFormula.value.text,
                            variables.value.text
                        )
                    },
                    label = "variables",
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.wrapContentHeight().fillMaxWidth(),
                    enabled = true,
                )
            }


            Row {
                val input1 = remember() {
                   data.inputXCount
                }
                val input2 = remember() {
                    data.inputYCount
                }
                val input3 = remember() {
                    data.inputZCount
                }

                Label(
                    "точек в линии",
                    singleLine = true,
                    modifier = Modifier.align(alignment = Alignment.CenterVertically)
                )
                NumericUpDownLine("", "", input1, modifier = Modifier.weight(1f))
                NumericUpDownLine("Начало", "", input2, modifier = Modifier.weight(1f))
                NumericUpDownLine("Конец", "", input3, modifier = Modifier.weight(1f))
            }
        }
    }

}

@Composable
fun ToolbarActionForFormula(data: FormulaData){
    Column(
    ) {
        SaveToFileButton(data)
    }
}

@Composable
fun ToolbarActionIconForFormula(data: FormulaData){
    Row(
    ) {
        SaveToFileIconButton(data)
    }
}

