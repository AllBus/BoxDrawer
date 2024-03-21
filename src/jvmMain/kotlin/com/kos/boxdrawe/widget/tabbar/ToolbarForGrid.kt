package com.kos.boxdrawe.widget.tabbar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.presentation.GridData
import com.kos.boxdrawe.widget.*
import kotlinx.coroutines.launch

@Composable
fun ToolbarForGrid(vm: GridData) {
    val coroutineScope = rememberCoroutineScope()
    var roundChecked by remember { vm.roundChecked }
    var innerChecked by remember { vm.innerChecked }

    val widthCell = remember { vm.widthCell }
    val widthFrame = remember { vm.widthFrame }
    val radius = remember { vm.radius }
    val cellRadius = remember { vm.cellRadius }
    val cellWidthCount = remember { vm.cellWidthCount }
    val cellHeightCount = remember { vm.cellHeightCount }
    val innerWidth = remember { vm.innerWidth }
    val innerRadius = remember { vm.innerRadius }
    val gridText = remember { vm.gridText }
    val figurePreview = vm.figurePreview.collectAsState()

    Row(
        modifier = TabContentModifier
    ) {
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            NumericUpDown("ячейки", "мм", widthCell)
            NumericUpDown("рамки", "мм", widthFrame)
            RunCheckBox(
                checked = roundChecked,
                title = "Скруглять углы",
                onCheckedChange = { c -> roundChecked = c },
            )
            NumericUpDown("Радиус", "мм", radius, enabled = roundChecked)
            NumericUpDown("Количество", "ячеек", cellRadius)
        }
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            NumericUpDown("горизонталь", "ячеек", cellWidthCount)
            NumericUpDown("вертикаль", "ячеек", cellHeightCount)
        }
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            RunCheckBox(
                checked = innerChecked,
                title = "Внутрение квадраты",
                onCheckedChange = { c -> innerChecked = c },
            )
            NumericUpDown("Сторона", "мм", innerWidth)
            NumericUpDown("Радиус", "мм", innerRadius)
            RunCheckBox(
                checked = figurePreview.value,
                title = "Предпросмотр",
                onCheckedChange = { c -> vm.figurePreview.value = c },
            )
        }
        Column(
            modifier = Modifier.weight(weight = 2f, fill = true)
        ){
            EditText("", "", gridText, true, Modifier){

            }
        }
        Column(
            modifier = Modifier.weight(weight = 0.5f, fill = true)
        ) {
            RunButton("Нарисовать деталь") {
                coroutineScope.launch {
                    showFileChooser(vm.tools.chooserDir()) { f -> vm.save(f) }
                }

            }
            Spacer(Modifier.height(4.dp))
            RunButton("Посторить по тексту") {
                vm.createFromText()
            }
            Spacer(Modifier.height(4.dp))
            RunButton("Получить текст") {
                vm.saveToText()
            }
        }
    }
}