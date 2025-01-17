package com.kos.boxdrawer.presentation.tabbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.presentation.ImageToolsData
import com.kos.boxdrawe.widget.CheckboxK
import com.kos.boxdrawe.widget.EditText
import com.kos.boxdrawe.widget.FileDialog
import com.kos.boxdrawe.widget.Label
import com.kos.boxdrawe.widget.NumericUpDownLine
import com.kos.boxdrawe.widget.RunButton
import com.kos.boxdrawe.widget.SaveFileDialog
import com.kos.boxdrawer.generated.resources.Res
import com.kos.boxdrawer.generated.resources.toolsButtonOpenFile
import com.kos.boxdrawer.generated.resources.toolsButtonSaveFile
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun ToolbarForImage(data: ImageToolsData){
    val coroutineScope = rememberCoroutineScope()
    Row(
        modifier = TabContentModifier
    ) {
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            val grayScale = remember { mutableStateOf(false) }
            val bounds = remember { mutableStateOf(false) }
            NumericUpDownLine("Контраст", "", data.contrastState)
            NumericUpDownLine("Поворот", "", data.rotateState)
            NumericUpDownLine("Размытие", "", data.gaussianState)
            Row {

                NumericUpDownLine("Отверстия", "", data.holeState, modifier = Modifier.weight(1f))
                RunButton("Скопировать", onClick = { data.copyPointsAsFigure()})
            }

            Row {
                Label("Чёрнобелый")
                CheckboxK(grayScale.value, onCheckedChange = remember(data) { { c->
                    grayScale.value = c
                    data.actionGrayScale(c)
                }})
            }
            Row {
                Label("Края")
                CheckboxK(bounds.value, onCheckedChange = remember(data) { { c->
                    bounds.value = c
                    data.actionBounds(c)
                }})
            }
        }
        Column(  modifier = Modifier.weight(weight = 1f, fill = true)) {
            RunButton("Применить", onClick = remember(data) {{
                coroutineScope.launch {
                    data.applyChanges()
                }}}
                )
            NumericUpDownLine("Масштаб", "", data.imageScale, modifier = Modifier)
            EditText("Фигура", data.holeFigureText, modifier = Modifier.weight(1f)) {}
        }
    }
}

@Composable
fun ToolbarActionForImage(data:ImageToolsData){
    val coroutineScope = rememberCoroutineScope()
    Column(
    ) {

        val isDialogVisible = remember { mutableStateOf(false) }
        val isSaveDialogVisible = remember { mutableStateOf(false) }
        if (isDialogVisible.value) {
            FileDialog(title = "Выберите картинку",
                initialDirectory = data.tools.chooserDir().absolutePath,
                onCloseRequest = { f ->
                    isDialogVisible.value = false
                    coroutineScope.launch {
                        if (f != null) {
                            data.loadImage(f)
                        }
                    }
                }
            )
        }

        if (isSaveDialogVisible.value) {
            SaveFileDialog(title = "Сохранить картинку как...",
                initialDirectory = data.tools.chooserDir().absolutePath,
                onCloseRequest = { f ->
                    isSaveDialogVisible.value = false
                    coroutineScope.launch {
                        if (f != null) {
                            data.save(f)
                        }
                    }
                }
            )
        }


        RunButton(stringResource(Res.string.toolsButtonOpenFile)) {
            isDialogVisible.value = true

        }
        Spacer(Modifier.height(4.dp))
        RunButton(stringResource(Res.string.toolsButtonSaveFile)) {
            isSaveDialogVisible.value = true
        }
    }
}
