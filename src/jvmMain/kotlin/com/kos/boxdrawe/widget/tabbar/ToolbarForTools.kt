package com.kos.boxdrawe.widget.tabbar

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.presentation.ToolsData
import com.kos.boxdrawe.widget.Label
import com.kos.boxdrawe.widget.NumericUpDown
import com.kos.boxdrawe.widget.RunButton
import com.kos.boxdrawe.widget.TabContentModifier
import com.kos.boxdrawe.widget.showFileChooser
import com.kos.boxdrawe.widget.showLoadFileChooser
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ToolbarForTools(vm: ToolsData) {
    val boardWeight = remember { vm.boardWeight }
    val holeWeight = remember { vm.holeWeight }
    val holeDrop  = remember { vm.holeDrop }
    val holeDropHeight  = remember { vm.holeDropHeight }
    val holeOffset = remember { vm.holeOffset }
    val algs = remember { vm.tools.figureList }
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = TabContentModifier
    ) {
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            val expanded = remember { mutableStateOf(false) }
            val settingsList = remember { vm.tools.settingsList }
            val selectedMovie = remember { vm.tools.settings }

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
                    value = selectedMovie.value.name,
                    onValueChange = {},
                    label = { Text("Шаблон") },
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
                    settingsList.value.group.forEach { item ->
                        DropdownMenuItem(
                            onClick = {
                                vm.selectSettings(item)
                                expanded.value = false
                            },
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
                        ) {
                            Text(item.name)
                        }
                    }
                }
            }
            RunButton("Открыть файл") {
                coroutineScope.launch {
                    showLoadFileChooser { f -> vm.loadDxf(f) }
                }
            }
        }
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            Label("Фигуры")
            LazyColumn(
               // verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(algs.value){ (name, a) ->
                    ListItem(
                        Modifier.height(30.dp).border(1.dp, MaterialTheme.colors.primaryVariant.copy(alpha = 0.2f))
                    ) {
                        Text(name)
                    }
                }
            }
        }

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
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            Text(
                text = "Отверстие",
                modifier = Modifier.fillMaxWidth(),
                softWrap = false,
                textAlign = TextAlign.Center
            )
            NumericUpDown("Ширина", "мм", holeWeight)
            NumericUpDown("Уменьшение длины", "мм", holeDrop)
            NumericUpDown("Уменьшение высоты", "мм", holeDropHeight)
            NumericUpDown("Отступ от края", "мм", holeOffset)
        }
    }
}