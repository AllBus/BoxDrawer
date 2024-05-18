package com.kos.boxdrawe.widget

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.presentation.SaveFigure
import com.kos.boxdrawe.widget.RunButton
import com.kos.boxdrawe.widget.showFileChooser
import kotlinx.coroutines.launch
import java.awt.Toolkit

@Composable
fun SaveToFileButton(vm: SaveFigure){
    val coroutineScope = rememberCoroutineScope()
    RunButton("Нарисовать деталь") {
        coroutineScope.launch {
            showFileChooser(vm.tools.chooserDir()) { f ->
                coroutineScope.launch {
                    vm.save(f)
                }
            }
        }
    }
    Spacer(Modifier.height(4.dp))
    RunButton("Скопировать деталь") {
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