package com.kos.boxdrawer.presentation.tabbar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.presentation.BezierData
import com.kos.boxdrawe.widget.EditText
import com.kos.boxdrawe.widget.Label
import com.kos.boxdrawe.widget.NumericUpDown
import com.kos.boxdrawe.widget.RunButton
import com.kos.boxdrawe.widget.SaveToFileButton
import com.kos.boxdrawe.widget.SegmentDoubleButton
import com.kos.boxdrawe.widget.SimpleEditText
import com.kos.boxdrawe.widget.TabContentModifier
import com.kos.boxdrawe.widget.model.ButtonDoubleData
import com.kos.boxdrawer.generated.resources.Res
import com.kos.boxdrawer.generated.resources.bezierFigure
import com.kos.boxdrawer.generated.resources.bezierFigureCount
import com.kos.boxdrawer.generated.resources.bezierFigureDistance
import com.kos.boxdrawer.generated.resources.bezierFigureLabel
import com.kos.boxdrawer.generated.resources.bezierFigurePadding
import com.kos.boxdrawer.generated.resources.bezierNewLineButton
import com.kos.boxdrawer.generated.resources.metricMM
import com.kos.boxdrawer.generated.resources.metricPercent
import com.kos.boxdrawer.generated.resources.toolsButtonCopyCode
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun ToolbarForBezier(vm: BezierData) {
    val coroutineScope = rememberCoroutineScope()

    val buttons = remember {
        listOf(
            ButtonDoubleData(0, 20.0, "20"),
            ButtonDoubleData(1, 10.0, "10"),
            ButtonDoubleData(2, 5.0, "5"),
            ButtonDoubleData(3, 2.5, "2.5"),
            ButtonDoubleData(4, 1.0, "1"),
            ButtonDoubleData(5, 0.5, "0.5"),
            ButtonDoubleData(6, 0.1, "0.1"),
            ButtonDoubleData(7, 0.01, "0.01"),

            )
    }
    val selectedId = remember { mutableIntStateOf(4) }
    val bezierText = remember { mutableStateOf("") }

    val pathRast = remember { vm.pathRast }
    val pathOffset = remember { vm.pathOffset }
    val pathCount = remember { vm.pathCount }
    val pathFigure = remember { vm.pathFigureText }

    val c1 = vm.c1.collectAsState()

    Row(
        modifier = TabContentModifier
    ) {
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            Label("${c1.value}")
            SegmentDoubleButton(selectedId, buttons, onClick = { index ->
                selectedId.value = index
                if (selectedId.value >= 0) {
                    vm.currentDistance.value = buttons[selectedId.value].value
                }
            })
            Row(
                modifier = Modifier.weight(weight = 1f)
            ) {
                SimpleEditText(
                    title = "",
                    postfix = "",
                    modifier = Modifier,
                    value = bezierText,
                    enabled = true
                ) { t ->
                    bezierText.value = t
                }
            }
            Spacer(
                Modifier.height(40.dp)
            )
            RunButton(stringResource(Res.string.bezierNewLineButton)) {
                coroutineScope.launch {
                    val text = bezierText.value
                    if (text.isEmpty())
                        vm.newBezier()
                    else
                        vm.newBezier(text)
                }
            }
        }

        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            Label(stringResource(Res.string.bezierFigureLabel))
            NumericUpDown(stringResource(Res.string.bezierFigureDistance), stringResource(Res.string.metricPercent), pathRast)
            NumericUpDown(stringResource(Res.string.bezierFigurePadding), stringResource(Res.string.metricPercent), pathOffset)
            NumericUpDown(stringResource(Res.string.bezierFigureCount), stringResource(Res.string.metricMM), pathCount)
            EditText(title = stringResource(Res.string.bezierFigure), value = pathFigure, enabled = true) { vm.createFigure(it) }
        }


    }
}

@Composable
fun ToolbarActionForBezier(vm: BezierData) {
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    Column(
    ) {
        SaveToFileButton(vm)

        Spacer(Modifier.height(4.dp))
        RunButton(stringResource(Res.string.toolsButtonCopyCode)) {
            coroutineScope.launch {
                clipboardManager.setText(AnnotatedString(vm.print()))
            }
        }
    }
}