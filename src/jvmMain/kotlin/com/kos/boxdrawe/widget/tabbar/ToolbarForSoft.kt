package com.kos.boxdrawe.widget.tabbar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.kos.boxdrawe.presentation.SoftRezData
import com.kos.boxdrawe.widget.*
import figure.IFigure

@Composable
fun ToolbarForSoft(vm: SoftRezData, figures: () -> IFigure) {
    var innerChecked by remember { vm.innerChecked }

    val width = remember { vm.width }
    val height = remember { vm.height }
    val cellWidthCount = remember { vm.cellWidthCount }
    val cellHeightCount = remember { vm.cellHeightCount }
    val cellWidthDistance = remember { vm.cellWidthDistance }
    val cellHeightDistance = remember { vm.cellHeightDistance }

    Row(
        modifier = TabContentModifier
    ) {
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            Text(
                text = "Область",
                modifier = Modifier,
                softWrap = false,
            )
            NumericUpDown("Длина", "мм", width)
            NumericUpDown("Высота", "мм", height)

        }
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            Text(
                text = "Количество элементов",
                modifier = Modifier,
                softWrap = false,
            )
            NumericUpDown("По длине", "шт", cellWidthCount)
            NumericUpDown("По высоте", "шт", cellHeightCount, enabled = !innerChecked)
            RunCheckBox(
                checked = innerChecked,
                title = "Сохранять пропорции",
                onCheckedChange = { c -> innerChecked = c },
            )
        }
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            Text(
                text = "Растояние между резами",
                modifier = Modifier,
                softWrap = false,
            )

            NumericUpDown("X", "мм", cellWidthDistance)
            NumericUpDown("Y", "мм", cellHeightDistance)
        }
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            RunButton("Нарисовать деталь", { showFileChooser { f -> vm.saveRez(f, figures()) } })
        }
    }
}