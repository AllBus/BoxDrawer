package com.kos.boxdrawe.widget

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import com.kos.boxdrawe.icons.Icons
import com.kos.boxdrawe.presentation.PrintCode
import com.kos.boxdrawer.generated.resources.Res
import com.kos.boxdrawer.generated.resources.toolsButtonCopyCode
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun PrintCodeButton(
    vm: PrintCode
) {
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    RunButton(stringResource(Res.string.toolsButtonCopyCode)) {
        coroutineScope.launch {
            clipboardManager.setText(AnnotatedString(vm.print()))
        }
    }
}

@Composable
fun PrintCodeIconButton(
    vm: PrintCode
) {
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    ImageButton(Icons.Code_blocks) {
        coroutineScope.launch {
            clipboardManager.setText(AnnotatedString(vm.print()))
        }
    }
}
