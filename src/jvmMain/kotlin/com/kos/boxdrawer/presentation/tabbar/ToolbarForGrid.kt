package com.kos.boxdrawer.presentation.tabbar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.presentation.GridData
import com.kos.boxdrawe.widget.EditText
import com.kos.boxdrawe.widget.NumericUpDown
import com.kos.boxdrawe.widget.RunButton
import com.kos.boxdrawe.widget.RunCheckBox
import com.kos.boxdrawe.widget.SaveToFileButton
import com.kos.boxdrawe.widget.TabContentModifier
import com.kos.boxdrawer.generated.resources.Res
import com.kos.boxdrawer.generated.resources.gridButtonCreateFromText
import com.kos.boxdrawer.generated.resources.gridButtonTextFromGrid
import com.kos.boxdrawer.generated.resources.gridCellsInHorizontal
import com.kos.boxdrawer.generated.resources.gridCellsInVertical
import com.kos.boxdrawer.generated.resources.gridCheckInnerFrom
import com.kos.boxdrawer.generated.resources.gridCheckPreview
import com.kos.boxdrawer.generated.resources.gridCheckRoundCorners
import com.kos.boxdrawer.generated.resources.gridCornerRadius
import com.kos.boxdrawer.generated.resources.gridInnerFormCornerRadius
import com.kos.boxdrawer.generated.resources.gridInnerFormSize
import com.kos.boxdrawer.generated.resources.gridRoundEdges
import com.kos.boxdrawer.generated.resources.gridSizeCell
import com.kos.boxdrawer.generated.resources.gridSizeFrame
import com.kos.boxdrawer.generated.resources.metricCell
import com.kos.boxdrawer.generated.resources.metricMM
import org.jetbrains.compose.resources.stringResource

@Composable
fun ToolbarForGrid(vm: GridData) {
    var roundChecked by remember { vm.roundChecked }
    var innerChecked by remember { vm.innerChecked }

    val widthCell = remember { vm.widthCell }
    val widthFrame = remember { vm.widthFrame }
    val radius = remember { vm.radius }
    val cellRadius = remember { vm.cellRadius }
    val cellWidthCount = remember { vm.cellWidthCount }
    val cellHeightCount = remember { vm.cellHeightCount }
    val innerWidth = remember { vm.innerWidth }
    val innerRadius = remember { vm.innerRadius }
    val gridText = remember { vm.gridText }
    val figurePreview = vm.figurePreview.collectAsState()
    val grid3d = vm.grid3d.collectAsState()

    Row(
        modifier = TabContentModifier
    ) {
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            NumericUpDown(
                stringResource(Res.string.gridSizeCell),
                stringResource(Res.string.metricMM),
                widthCell
            )
            NumericUpDown(
                stringResource(Res.string.gridSizeFrame),
                stringResource(Res.string.metricMM),
                widthFrame
            )
            RunCheckBox(
                checked = roundChecked,
                title = stringResource(Res.string.gridCheckRoundCorners),
                onCheckedChange = { c -> roundChecked = c },
            )
            NumericUpDown(
                stringResource(Res.string.gridCornerRadius),
                stringResource(Res.string.metricMM),
                radius,
                enabled = roundChecked
            )
            NumericUpDown(
                stringResource(Res.string.gridRoundEdges),
                stringResource(Res.string.metricCell),
                cellRadius
            )
        }
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            NumericUpDown(
                stringResource(Res.string.gridCellsInHorizontal),
                stringResource(Res.string.metricCell),
                cellWidthCount
            )
            NumericUpDown(
                stringResource(Res.string.gridCellsInVertical),
                stringResource(Res.string.metricCell),
                cellHeightCount
            )
        }
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            RunCheckBox(
                checked = innerChecked,
                title = stringResource(Res.string.gridCheckInnerFrom),
                onCheckedChange = { c -> innerChecked = c },
            )
            NumericUpDown(
                stringResource(Res.string.gridInnerFormSize),
                stringResource(Res.string.metricMM),
                innerWidth
            )
            NumericUpDown(
                stringResource(Res.string.gridInnerFormCornerRadius),
                stringResource(Res.string.metricMM),
                innerRadius
            )
            RunCheckBox(
                checked = figurePreview.value,
                title = stringResource(Res.string.gridCheckPreview),
                onCheckedChange = { c -> vm.figurePreview.value = c },
            )

            RunCheckBox(
                checked = grid3d.value,
                title = "3D",
                onCheckedChange = { c -> vm.grid3d.value = c },
            )
        }
        Column(
            modifier = Modifier.weight(weight = 2f, fill = true)
        ) {
            EditText(title = "", value = gridText, enabled = true, modifier = Modifier) {

            }
        }

    }
}

@Composable
fun ToolbarActionForGrid(vm: GridData) {
    Column(
    ) {
        SaveToFileButton(vm)

        Spacer(Modifier.height(4.dp))
        RunButton(stringResource(Res.string.gridButtonCreateFromText)) {
            vm.createFromText()
        }
        Spacer(Modifier.height(4.dp))
        RunButton(stringResource(Res.string.gridButtonTextFromGrid)) {
            vm.saveToText()
        }
    }
}