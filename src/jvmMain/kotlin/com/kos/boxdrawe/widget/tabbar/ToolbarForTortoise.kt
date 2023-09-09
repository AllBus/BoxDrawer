package com.kos.boxdrawe.widget.tabbar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.presentation.TortoiseData
import com.kos.boxdrawe.widget.EditText
import com.kos.boxdrawe.widget.RunButton
import com.kos.boxdrawe.widget.TabContentModifier
import com.kos.boxdrawe.widget.showFileChooser

@Composable
fun ToolbarForTortoise(vm: TortoiseData) {
    val text = rememberSaveable(key = "ToolbarForTortoise.Text") { vm.text }

    Row(
        modifier = TabContentModifier
    ) {
        Column(
            modifier = Modifier.weight(weight = 2f, fill = true).padding(end = 8.dp)
        ) {
            EditText("Фигуры", "", text, true) { vm.createTortoise(it) }
        }
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            RunButton("Нарисовать деталь") { showFileChooser { f -> vm.saveTortoise(f, text.value) } }
        }
    }
}