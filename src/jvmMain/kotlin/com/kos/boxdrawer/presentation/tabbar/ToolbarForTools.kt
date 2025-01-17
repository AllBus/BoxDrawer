package com.kos.boxdrawer.presentation.tabbar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.onClick
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.presentation.ToolsData
import com.kos.boxdrawe.widget.Label
import com.kos.boxdrawe.widget.NumericUpDown
import com.kos.boxdrawe.widget.PrintCodeButton
import com.kos.boxdrawe.widget.PrintCodeIconButton
import com.kos.boxdrawe.widget.RunButton
import com.kos.boxdrawe.widget.RunCheckBox
import com.kos.boxdrawe.widget.SaveToFileButton
import com.kos.boxdrawe.widget.SaveToFileIconButton
import com.kos.boxdrawer.generated.resources.Res
import com.kos.boxdrawer.generated.resources.metricMM
import com.kos.boxdrawer.generated.resources.toolsButtonCopyCode
import com.kos.boxdrawer.generated.resources.toolsCheckBoxTemplateEditor
import com.kos.boxdrawer.generated.resources.toolsDrawerSettingsBoardLabel
import com.kos.boxdrawer.generated.resources.toolsDrawerSettingsBoardWeight
import com.kos.boxdrawer.generated.resources.toolsDrawerSettingsHoleDropHeight
import com.kos.boxdrawer.generated.resources.toolsDrawerSettingsHoleDropWidth
import com.kos.boxdrawer.generated.resources.toolsDrawerSettingsHoleHeight
import com.kos.boxdrawer.generated.resources.toolsDrawerSettingsHoleLabel
import com.kos.boxdrawer.generated.resources.toolsDrawerSettingsHoleOffset
import com.kos.boxdrawer.generated.resources.toolsDrawerSettingsTemplateComboBox
import com.kos.boxdrawer.generated.resources.toolsFiguresList
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
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


    Row(
        modifier = TabContentModifier
    ) {

        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            Row {
                Label(stringResource(Res.string.toolsFiguresList))
                RunCheckBox(
                    checkboxEditor.value,
                    stringResource(Res.string.toolsCheckBoxTemplateEditor),
                    remember(vm) {
                        { checked ->
                            vm.templateData.checkboxEditor.value = checked
                        }
                    }
                )
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
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            val settingsList = remember { vm.tools.settingsList }
            val selectedMovie = remember { vm.tools.settings }

            ComboBox(
                label = stringResource(Res.string.toolsDrawerSettingsTemplateComboBox),
                selectedTitle = selectedMovie.value.name,
                items = settingsList.value.group,
                onClick = { item -> vm.selectSettings(item) }
            )
            Column(
                modifier = Modifier.weight(weight = 1f, fill = true)
            ) {
                Text(
                    text = stringResource(Res.string.toolsDrawerSettingsBoardLabel),
                    modifier = Modifier.fillMaxWidth(),
                    softWrap = false,
                    textAlign = TextAlign.Center
                )
                NumericUpDown(stringResource(Res.string.toolsDrawerSettingsBoardWeight), stringResource(Res.string.metricMM), boardWeight)
            }
        }
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            Text(
                text = stringResource(Res.string.toolsDrawerSettingsHoleLabel),
                modifier = Modifier.fillMaxWidth(),
                softWrap = false,
                textAlign = TextAlign.Center
            )
            NumericUpDown(stringResource(Res.string.toolsDrawerSettingsHoleHeight), stringResource(Res.string.metricMM), holeWeight)
            NumericUpDown(stringResource(Res.string.toolsDrawerSettingsHoleDropWidth), stringResource(Res.string.metricMM), holeDrop)
            NumericUpDown(stringResource(Res.string.toolsDrawerSettingsHoleDropHeight), stringResource(Res.string.metricMM), holeDropHeight)
            NumericUpDown(stringResource(Res.string.toolsDrawerSettingsHoleOffset), stringResource(Res.string.metricMM), holeOffset)
        }
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

@Composable
fun ToolbarActionForTools(vm: ToolsData) {
    Column(
    ) {
        SaveToFileButton(vm.templateData)

        Spacer(Modifier.height(4.dp))
        PrintCodeButton(vm)
    }
}

@Composable
fun ToolbarActionIconForTools(vm: ToolsData) {
    Row(
    ) {
        SaveToFileIconButton(vm.templateData)
        PrintCodeIconButton(vm)
    }
}
