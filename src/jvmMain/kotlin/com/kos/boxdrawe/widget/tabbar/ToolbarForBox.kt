package com.kos.boxdrawe.widget.tabbar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.presentation.BoxData
import com.kos.boxdrawe.widget.*
import com.kos.boxdrawe.widget.model.ButtonData
import com.kos.boxdrawer.detal.box.PazExt
import kotlinx.coroutines.launch

@Composable
fun ToolbarForBox(vm: BoxData) {

    var insideChecked by remember { vm.insideChecked }
    var polkiInChecked by remember { vm.polkiInChecked }
    var alternative by remember { vm.alternative }

    val width = remember { vm.width }
    val height = remember { vm.height }
    val weight = remember { vm.weight }
    val text = rememberSaveable(key = "ToolbarForBox.Text") { vm.text }
    val coroutineScope = rememberCoroutineScope()
    val selectZigTopId = remember { vm.selectZigTopId }
    val selectZigBottomId = remember { vm.selectZigBottomId }
    val topOffset = remember { vm.topOffset }
    val bottomOffset = remember { vm.bottomOffset }

    val zigVariants = listOf(
        ButtonData(PazExt.PAZ_NONE, painterResource("drawable/act_line.png")),
        ButtonData(PazExt.PAZ_HOLE, painterResource("drawable/act_hole.png")),
        ButtonData(PazExt.PAZ_PAZ, painterResource("drawable/act_paz.png")),
        //   ButtonData(PazExt.PAZ_BACK, painterResource("drawable/act_paz_in.png")),
    )

    Row(
        modifier = TabContentModifier
    ) {
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            NumericUpDown("Длина", "мм", width)
            NumericUpDown("Ширина", "мм", weight)
            NumericUpDown("Высота", "мм", height)
            Label("Форма соединения крышки", Modifier.align(Alignment.End))
            Row(
                Modifier.align(Alignment.End)
            ) {
                NumericUpDown("", "", topOffset, modifier = Modifier.width(50.dp))
                SegmentButton(
                    selectZigTopId,
                    zigVariants,
                    Modifier
                ) { id ->
                    selectZigTopId.value = id
                    vm.redrawBox()
                }
            }
            Label("Форма соединения дна", Modifier.align(Alignment.End))
            Row(
                Modifier.align(Alignment.End)
            )
            {
                NumericUpDown("", "", bottomOffset, modifier = Modifier.width(50.dp))
                SegmentButton(
                    selectZigBottomId,
                    zigVariants,
                    Modifier
                ) { id ->
                    selectZigBottomId.value = id
                    vm.redrawBox()
                }
            }
        }
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {

            Label("Свойства пазов, по направлениям")
            Row() {
                ZigZagLabel()
                ZigZagInput(
                    modifier = Modifier.weight(weight = 1f, fill = true),
                      title = "длина",
                    drawNames = false,
                    zigState= vm.widthZigState

                )
                ZigZagInput(
                    modifier = Modifier.weight(weight = 1f, fill = true),
                    title = "ширина",
                    drawNames = false,
                    zigState= vm.weightZigState

                    )
                ZigZagInput(
                    modifier = Modifier.weight(weight = 1f, fill = true),
                    title = "высота",
                    drawNames = false,
                    zigState= vm.heightZigState

                    )
                ZigZagInput(
                    modifier = Modifier.weight(weight = 1f, fill = true),
                    title = "полки",
                    drawNames = false,
                    zigState= vm.polkaZigState
                )
                ZigZagInput(
                    modifier = Modifier.weight(weight = 1f, fill = true),
                    title = "дно полки",
                    drawNames = false,
                    zigState= vm.polkaPolZigState
                )
            }
        }
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            RunCheckBox(
                checked = insideChecked,
                title = "Размеры по внутреннему объёму",
                onCheckedChange = { c ->
                    insideChecked = c
                    vm.redrawBox()
                },
            )
            RunCheckBox(
                checked = polkiInChecked,
                title = "Учитывать толщину полки",
                onCheckedChange = { c ->
                    polkiInChecked = c
                    vm.redrawBox()
                },
            )
            RunCheckBox(
                checked = alternative,
                title = "Альтернативное расположение",
                onCheckedChange = { c ->
                    alternative = c
                    vm.redrawBox()
                },
            )
            EditText("Полки", "", text, true) { vm.createBox(it) }
        }
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            RunButton("Нарисовать коробку") {
                coroutineScope.launch {
                    showFileChooser { f -> vm.saveBox(f, text.value) }
                }
            }
        }

    }
}