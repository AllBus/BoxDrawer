package com.kos.boxdrawe.widget.tabbar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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

    Row(
        modifier = TabContentModifier
    ) {
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            NumericUpDown("Ширина ячейки", "мм", widthCell)
            NumericUpDown("Ширина рамки", "мм", widthFrame)
            RunCheckBox(
                checked = roundChecked,
                title = "Скруглять углы",
                onCheckedChange = { c -> roundChecked = c },
            )
            NumericUpDown("Радиус скругления", "мм", radius, enabled = roundChecked)
            NumericUpDown("Максимальное количество", "ячеек", cellRadius)
        }
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            NumericUpDown("По горизонтали", "ячеек", cellWidthCount)
            NumericUpDown("По вертикали", "ячеек", cellHeightCount)
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
                    showFileChooser { f -> vm.save(f) }
                }

            }
            RunButton("Посторить по тексту") {
                vm.createFromText()
            }
            RunButton("Получить текст") {
                vm.saveToText()
            }
        }
    }
}