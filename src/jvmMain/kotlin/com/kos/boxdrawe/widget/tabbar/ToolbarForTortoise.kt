package com.kos.boxdrawe.widget.tabbar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.presentation.TortoiseData
import com.kos.boxdrawe.widget.EditTextField
import com.kos.boxdrawe.widget.RunButton
import com.kos.boxdrawe.widget.TabContentModifier
import com.kos.boxdrawe.widget.showFileChooser
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ToolbarForTortoise(vm: TortoiseData) {
    val text = rememberSaveable(key = "ToolbarForTortoise.Text") { vm.text }

    Row(
        modifier = TabContentModifier
    ) {
        Column(
            modifier = Modifier.weight(weight = 2f, fill = true).padding(end = 8.dp)
        ) {
            EditTextField("Фигуры", text, true) {
                vm.findHelp(vm.text.value.text, vm.text.value.selection)
                vm.createTortoise(it.toString())
            }
        }

    }
}

@Composable
fun ToolbarActionForTortoise(vm: TortoiseData) {
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    Column(
    ) {
        RunButton("Нарисовать деталь") {
            coroutineScope.launch {
                showFileChooser(vm.tools.chooserDir()) { f -> vm.save(f) }
            }
        }
        Spacer(Modifier.height(4.dp))
        RunButton("Скопировать как программу") {
            coroutineScope.launch {
                clipboardManager.setText(AnnotatedString(vm.printCommand()))
            }
        }
    }
}

