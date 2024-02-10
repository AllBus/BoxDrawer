package com.kos.boxdrawe.widget.tabbar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.presentation.BezierData
import com.kos.boxdrawe.widget.Label
import com.kos.boxdrawe.widget.RunButton
import com.kos.boxdrawe.widget.SegmentButton
import com.kos.boxdrawe.widget.SegmentDoubleButton
import com.kos.boxdrawe.widget.TabContentModifier
import com.kos.boxdrawe.widget.model.ButtonDoubleData
import com.kos.boxdrawe.widget.showFileChooser
import kotlinx.coroutines.launch

@Composable
fun ToolbarForBezier(vm: BezierData) {
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    val buttons = remember { listOf(
        ButtonDoubleData(0, 20.0, "20"),
        ButtonDoubleData(1, 10.0, "10"),
        ButtonDoubleData(2, 5.0, "5"),
        ButtonDoubleData(3, 2.5, "2.5"),
        ButtonDoubleData(4, 1.0, "1"),
        ButtonDoubleData(5, 0.5, "0.5"),
        ButtonDoubleData(6, 0.1, "0.1"),
        ButtonDoubleData(7, 0.01, "0.01"),

    ) }
    val selectedId = remember { mutableIntStateOf(4) }


    val c1 = vm.c1.collectAsState()

    Row(
        modifier = TabContentModifier
    ) {
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            Label("${c1.value}")
            SegmentDoubleButton(selectedId, buttons, onClick = { index -> selectedId.value = index
                if (selectedId.value>=0){
                    vm.currentDistance.value = buttons[selectedId.value].value
                }
            })
        }

        Column(
            modifier = Modifier.weight(weight = 0.5f, fill = true)
        ) {
            RunButton("Нарисовать деталь") {
                coroutineScope.launch {
                    showFileChooser { f -> vm.save(f) }
                }
             }
            Spacer(Modifier.height(4.dp))
            RunButton("Скопировать код") {
                coroutineScope.launch {
                    clipboardManager.setText(AnnotatedString(vm.print()))
                }
            }
        }
    }
}