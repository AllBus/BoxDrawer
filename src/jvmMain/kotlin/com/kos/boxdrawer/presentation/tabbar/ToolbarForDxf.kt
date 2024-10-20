package com.kos.boxdrawer.presentation.tabbar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.presentation.DxfToolsData
import com.kos.boxdrawe.presentation.Instruments
import com.kos.boxdrawe.presentation.Instruments.INSTRUMENT_RECTANGLE
import com.kos.boxdrawe.widget.NumericUpDown
import com.kos.boxdrawe.widget.NumericUpDownLine
import com.kos.boxdrawe.widget.RunButton
import com.kos.boxdrawe.widget.SaveToFileButton
import com.kos.boxdrawer.generated.resources.Res
import com.kos.boxdrawer.generated.resources.dxfScaleColor
import com.kos.boxdrawer.generated.resources.dxfScaleValue
import com.kos.boxdrawer.generated.resources.toolsButtonCopyCode
import com.kos.boxdrawer.generated.resources.toolsButtonOpenFile
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun ToolbarForDxf(vm: DxfToolsData) {

    val scaleValue = remember { vm.scaleEdit }
    val colorValue = remember { vm.scaleColor }
    val scaleValue2 = remember { vm.scaleEdit2 }
    val colorValue2 = remember { vm.scaleColor2 }
    val scaleValue3 = remember { vm.scaleEdit3 }
    val colorValue3 = remember { vm.scaleColor3 }

    val instrument = remember { vm.instrument }


    Row(
        modifier = TabContentModifier
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row {
                NumericUpDown(stringResource(Res.string.dxfScaleColor), "", colorValue, modifier = Modifier.weight(1f))
                NumericUpDownLine(stringResource(Res.string.dxfScaleValue), "", scaleValue, modifier = Modifier.weight(3f))
            }
            Row {
                NumericUpDown(stringResource(Res.string.dxfScaleColor), "", colorValue2, modifier = Modifier.weight(1f))
                NumericUpDownLine(stringResource(Res.string.dxfScaleValue), "", scaleValue2, modifier = Modifier.weight(3f))
            }
            Row {
                NumericUpDown(stringResource(Res.string.dxfScaleColor), "", colorValue3, modifier = Modifier.weight(1f))
                NumericUpDownLine(stringResource(Res.string.dxfScaleValue), "", scaleValue3, modifier = Modifier.weight(3f))
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Button(onClick= {
                if (instrument.value != INSTRUMENT_RECTANGLE)
                    instrument.value = INSTRUMENT_RECTANGLE
                else
                    instrument.value = Instruments.INSTRUMENT_NONE
            }) {
                Icon(
                    imageVector = Icons.Filled.AccountBox,
                    contentDescription = "Rectangle"
                )
            }
            Button(onClick= {
                if (instrument.value != Instruments.INSTRUMENT_LINE)
                    instrument.value = Instruments.INSTRUMENT_LINE
                else
                    instrument.value = Instruments.INSTRUMENT_NONE
            }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Line"
                )
            }
        }
    }
}

@Composable
fun ToolbarActionForDxf(vm: DxfToolsData) {
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
        Spacer(Modifier.height(4.dp))
        RunButton(stringResource(Res.string.toolsButtonOpenFile)) {
            coroutineScope.launch {
                showLoadFileChooser(vm.tools.chooserDir()) { f -> vm.loadDxf(f) }
            }
        }
    }
}