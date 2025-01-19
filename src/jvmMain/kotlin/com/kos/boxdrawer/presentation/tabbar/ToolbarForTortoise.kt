package com.kos.boxdrawer.presentation.tabbar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.icons.Icons
import com.kos.boxdrawe.presentation.TortoiseData
import com.kos.boxdrawe.widget.EditTextField
import com.kos.boxdrawe.widget.ImageButton
import com.kos.boxdrawe.widget.RunButton
import com.kos.boxdrawe.widget.SaveToFileButton
import com.kos.boxdrawe.widget.SaveToFileIconButton
import com.kos.boxdrawer.generated.resources.Res
import com.kos.boxdrawer.generated.resources.toolsButtonCopyProgram
import com.kos.boxdrawer.generated.resources.tortoiseFigureField
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ToolbarForTortoise(vm: TortoiseData) {
    val text = rememberSaveable(key = "ToolbarForTortoise.Text") { vm.text }
  //  val text2 = rememberSaveable(key = "ToolbarForTortoise.Text2") { mutableStateOf(TextFieldState("")) }

    Row(
        modifier = TabContentModifier
    ) {
        Column(
            modifier = Modifier.weight(weight = 2f, fill = true).padding(end = 8.dp)
        ) {
            EditTextField(stringResource(Res.string.tortoiseFigureField), text, true,
                onMove = remember(vm) {
                    { tv ->
                        vm.findHelp(vm.text.value.text, vm.text.value.selection)
                    }
                }, onChange = remember(vm) {
                    {
                        vm.createTortoise()
                    }
            })
//            EditTextField2(stringResource(Res.string.tortoiseFigureField), text2, true,
//                onChange = {
//                    vm.createTortoise()
//                })
        }
    }
}

@Composable
fun ToolbarActionForTortoise(vm: TortoiseData) {
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    Column(
    ) {
        SaveToFileButton(vm)
        Spacer(Modifier.height(4.dp))
        RunButton(stringResource(Res.string.toolsButtonCopyProgram)) {
            coroutineScope.launch {
                clipboardManager.setText(AnnotatedString(vm.printCommand()))
            }
        }
    }
}


@Composable
fun ToolbarActionIconForTortoise(vm: TortoiseData) {
    Row(
    ) {
        SaveToFileIconButton(vm)
    }
}

