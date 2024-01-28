package com.kos.boxdrawe.widget.tabbar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.kos.boxdrawe.presentation.ToolsData
import com.kos.boxdrawe.widget.NumericUpDown
import com.kos.boxdrawe.widget.TabContentModifier

@Composable
fun ToolbarForTools(vm: ToolsData) {
    val boardWeight = remember { vm.boardWeight }
    val holeWeight = remember { vm.holeWeight }
    val holeDrop  = remember { vm.holeDrop }
    val holeDropHeight  = remember { vm.holeDropHeight }
    val holeOffset = remember { vm.holeOffset }

    Row(
        modifier = TabContentModifier
    ) {
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            Text(
                text = "Доска",
                modifier = Modifier,
                softWrap = false,
            )
            NumericUpDown("Толщина", "мм", boardWeight)
        }
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            Text(
                text = "Отверстие",
                modifier = Modifier,
                softWrap = false,
            )
            NumericUpDown("Ширина", "мм", holeWeight)
            NumericUpDown("Уменьшение длины отверстия", "мм", holeDrop)
            NumericUpDown("Уменьшение высоты отверстия", "мм", holeDropHeight)
            NumericUpDown("Отступ от края", "мм", holeOffset)
        }
    }
}