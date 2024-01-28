package com.kos.boxdrawe.widget.tabbar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.kos.boxdrawe.presentation.BoxData
import com.kos.boxdrawe.widget.*
import com.kos.boxdrawe.widget.model.ButtonData
import kotlinx.coroutines.launch

@Composable
fun ToolbarForBox(vm: BoxData) {

    var insideChecked by remember { vm.insideChecked }

    val width = remember { vm.width }
    val height = remember { vm.height }
    val weight = remember { vm.weight }
    val text = rememberSaveable(key = "ToolbarForBox.Text") { vm.text }
    val coroutineScope = rememberCoroutineScope()
    val selectZigTopId = remember{ mutableStateOf(1) }
    val selectZigBottomId = remember{ mutableStateOf(0) }

    val zigVariants = listOf(
        ButtonData(0, painterResource("drawable/act_hole.png")),
        ButtonData(1, painterResource("drawable/act_line.png")),
        ButtonData(2, painterResource("drawable/act_paz.png")),
        ButtonData(3, painterResource("drawable/act_paz_in.png")),
    )

    Row(
        modifier = TabContentModifier
    ) {
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            NumericUpDown("Длина", "мм", width)
            NumericUpDown("Ширина", "мм", height)
            NumericUpDown("Высота", "мм", weight)
            Label("Форма соединения крышки", Modifier.align(Alignment.End))
            SegmentButton(selectZigTopId, zigVariants, Modifier.align(Alignment.End)) { id -> selectZigTopId.value = id }
            Label("Форма соединения дна", Modifier.align(Alignment.End))
            SegmentButton(selectZigBottomId, zigVariants, Modifier.align(Alignment.End)) { id -> selectZigBottomId.value = id }
        }
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            RunCheckBox(
                checked = insideChecked,
                title = "Размеры по внутреннему объёму",
                onCheckedChange = { c -> insideChecked = c },
            )
            EditText("Фигуры", "", text, true) { vm.createBox(it) }
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