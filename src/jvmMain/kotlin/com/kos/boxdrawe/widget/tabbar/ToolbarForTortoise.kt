package com.kos.boxdrawe.widget.tabbar

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.presentation.TortoiseData
import com.kos.boxdrawe.widget.*
import com.kos.boxdrawe.widget.EditText
import kotlinx.coroutines.launch

@Composable
fun ToolbarForTortoise(vm: TortoiseData) {
    val text = rememberSaveable(key = "ToolbarForTortoise.Text") { mutableStateOf(TextFieldValue(vm.text.value)) }
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    Row(
        modifier = TabContentModifier
    ) {
        Column(
            modifier = Modifier.weight(weight = 2f, fill = true).padding(end = 8.dp)
        ) {
            EditTextField("Фигуры", "", text, true) {
                vm.text.value = it.text
                vm.findHelp(it.text, it.selection)
                vm.createTortoise(it.text) }
        }
        Column(
            modifier = Modifier.weight(weight = 0.5f, fill = true)
        ) {
            RunButton("Нарисовать деталь") {
                coroutineScope.launch {
                    showFileChooser { f -> vm.saveTortoise(f, text.value.text) }
                }
            }
            Spacer(Modifier.height(4.dp))
            RunButton("Скопировать как программу") {
                coroutineScope.launch {
                    clipboardManager.setText(AnnotatedString(vm.printCommand(text.value.text)))
                }
            }
        }
    }
}