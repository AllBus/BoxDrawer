package com.kos.boxdrawe.widget

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.presentation.SaveFigure
import com.kos.boxdrawer.generated.resources.Res
import com.kos.boxdrawer.generated.resources.copyFileButton
import com.kos.boxdrawer.generated.resources.createFileButton
import com.kos.boxdrawer.presentation.tabbar.showFileChooser
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import java.awt.Toolkit
import com.kos.boxdrawe.icons.Icons

@Composable
fun SaveToFileButton(vm: SaveFigure) {
    val coroutineScope = rememberCoroutineScope()
    RunButton(stringResource(Res.string.createFileButton)) {
        coroutineScope.launch {
            showFileChooser(vm.tools.chooserDir()) { f ->
                coroutineScope.launch {
                    vm.save(f)
                }
            }
        }
    }
    Spacer(Modifier.height(4.dp))
    RunButton(stringResource(Res.string.copyFileButton)) {
        coroutineScope.launch {
            val dxf = vm.copy()
            Toolkit.getDefaultToolkit().systemClipboard.setContents(dxf, null)
//                val fileList =
//                    Toolkit.getDefaultToolkit().systemClipboard.availableDataFlavors //a(DataFlavor.javaFileListFlavor) as List<File>
//                fileList.forEach {
//                    println(it)
//                }
        }
    }
}

@Composable
fun SaveToFileIconButton(vm: SaveFigure) {
    val coroutineScope = rememberCoroutineScope()
    ImageButton(Icons.File_save) {
        coroutineScope.launch {
            showFileChooser(vm.tools.chooserDir()) { f ->
                coroutineScope.launch {
                    vm.save(f)
                }
            }
        }
    }

    ImageButton(Icons.File_copy) {
        coroutineScope.launch {
            val dxf = vm.copy()
            Toolkit.getDefaultToolkit().systemClipboard.setContents(dxf, null)
//                val fileList =
//                    Toolkit.getDefaultToolkit().systemClipboard.availableDataFlavors //a(DataFlavor.javaFileListFlavor) as List<File>
//                fileList.forEach {
//                    println(it)
//                }
        }
    }
}

