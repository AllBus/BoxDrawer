package com.kos.boxdrawe.widget.tabbar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.kos.boxdrawe.presentation.GridData
import com.kos.boxdrawe.widget.NumericUpDown
import com.kos.boxdrawe.widget.RunButton
import com.kos.boxdrawe.widget.RunCheckBox
import com.kos.boxdrawe.widget.TabContentModifier

@Composable
fun ToolbarForGrid(vm: GridData) {
    var roundChecked by remember { vm.roundChecked }
    var innerChecked by remember { vm.innerChecked }

    val widthCell = remember { vm.widthCell }
    val widthFrame = remember { vm.widthFrame }
    val radius = remember { vm.radius }
    val cellWidthCount = remember { vm.cellWidthCount }
    val cellHeightCount = remember { vm.cellHeightCount }
    val innerWidth = remember { vm.innerWidth }
    val innerRadius = remember { vm.innerRadius }

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
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            RunButton("Нарисовать деталь", {})
            RunButton("Посторить по тексту", {})
            RunButton("Получить текст", {})
        }
    }
}