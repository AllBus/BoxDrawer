package com.kos.boxdrawe.widget.tabbar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.onClick
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.presentation.ToolsData
import com.kos.boxdrawe.widget.CheckboxK
import com.kos.boxdrawe.widget.Label
import com.kos.boxdrawe.widget.NumericUpDown
import com.kos.boxdrawe.widget.RunButton
import com.kos.boxdrawe.widget.RunCheckBox
import com.kos.boxdrawe.widget.TabContentModifier
import com.kos.boxdrawe.widget.showFileChooser
import com.kos.boxdrawe.widget.showLoadFileChooser
import kotlinx.coroutines.launch
import turtoise.DrawerSettings
import turtoise.TemplateAlgorithm

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun ToolbarForTools(vm: ToolsData) {
    val boardWeight = remember { vm.boardWeight }
    val holeWeight = remember { vm.holeWeight }
    val holeDrop  = remember { vm.holeDrop }
    val zigDrop  = remember { vm.zigDrop }
    val holeDropHeight  = remember { vm.holeDropHeight }
    val holeOffset = remember { vm.holeOffset }
    val algs = remember { vm.tools.figureList }
    val checkboxEditor = vm.templateData.checkboxEditor.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    Row(
        modifier = TabContentModifier
    ) {

        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            Row {
                Label("Фигуры")
                RunCheckBox(checkboxEditor.value, "Редактор меню") { checked ->
                    vm.templateData.checkboxEditor.value = checked
                }
            }
            LazyColumn(
               // verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(algs.value){ (name, a) ->
                    ListItem(
                        Modifier
                            .height(30.dp)
                            .border(1.dp, MaterialTheme.colors.primaryVariant.copy(alpha = 0.2f))
                            .background(if (a is TemplateAlgorithm)
                                MaterialTheme.colors.secondaryVariant else
                                    MaterialTheme.colors.primaryVariant.copy(alpha = 0.2f))
                            .onClick { vm.selectFigure(a, name) }
                    ) {
                        Text(name)
                    }
                }
            }
        }
        Column(
            modifier = Modifier.weight(weight = 0.5f, fill = true)
        ) {

            RunButton("Открыть файл") {
                coroutineScope.launch {
                    showLoadFileChooser(vm.tools.chooserDir()) { f -> vm.templateData.loadDxf(f) }
                }
            }
            Spacer(Modifier.height(4.dp))
            RunButton("Нарисовать деталь") {
                coroutineScope.launch {
                    showFileChooser(vm.tools.chooserDir()) { f -> vm.templateData.save(f) }
                }
            }

            Spacer(Modifier.height(4.dp))
            RunButton("Скопировать код") {
                coroutineScope.launch {
                    clipboardManager.setText(AnnotatedString(vm.templateData.print()))
                }
            }
        }

        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            val settingsList = remember { vm.tools.settingsList }
            val selectedMovie = remember { vm.tools.settings }

            ComboBox(
                label = "Шаблон",
                selectedTitle = selectedMovie.value.name,
                items = settingsList.value.group,
                onClick = { item -> vm.selectSettings(item) }
            )
            Column(
                modifier = Modifier.weight(weight = 1f, fill = true)
            ) {
                Text(
                    text = "Доска",
                    modifier = Modifier.fillMaxWidth(),
                    softWrap = false,
                    textAlign = TextAlign.Center
                )
                NumericUpDown("Толщина", "мм", boardWeight)
            }
        }
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            Text(
                text = "Отверстие",
                modifier = Modifier.fillMaxWidth(),
                softWrap = false,
                textAlign = TextAlign.Center
            )
            NumericUpDown("Высота", "мм", holeWeight)
            NumericUpDown("Уменьшение длины", "мм", holeDrop)
            NumericUpDown("Уменьшение высоты", "мм", holeDropHeight)
            NumericUpDown("Отступ от края", "мм", holeOffset)
        }
//        Column(
//            modifier = Modifier.weight(weight = 1f, fill = true)
//        ) {
//            Text(
//                text = "Паз",
//                modifier = Modifier.fillMaxWidth(),
//                softWrap = false,
//                textAlign = TextAlign.Center
//            )
//            NumericUpDown("Уменьшение длины", "мм", zigDrop)
//        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ComboBox(
    label:String,
    selectedTitle:String,
    items : List<DrawerSettings>,
    onClick: (DrawerSettings) -> Unit,
){
    val expanded = remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded.value,
        onExpandedChange = {
            expanded.value = !expanded.value
        }
    ) {
        // textfield
        TextField(
            modifier = Modifier, // menuAnchor modifier must be passed to the text field for correctness.
            readOnly = true,
            value = selectedTitle,
            onValueChange = {},
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        // menu
        ExposedDropdownMenu(
            expanded = expanded.value,
            onDismissRequest = {
                expanded.value = false
            },
        ) {
            // menu items
            items.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        onClick(item)
                        expanded.value = false
                    },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
                ) {
                    Text(item.name)
                }
            }
        }
    }
}