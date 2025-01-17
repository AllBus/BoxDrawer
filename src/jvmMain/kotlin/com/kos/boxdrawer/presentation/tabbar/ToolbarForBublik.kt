package com.kos.boxdrawer.presentation.tabbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.presentation.BublikData
import com.kos.boxdrawe.widget.Label
import com.kos.boxdrawe.widget.NumericUpDownLine
import com.kos.boxdrawe.widget.RunCheckBox
import com.kos.boxdrawe.widget.SaveToFileButton
import com.kos.boxdrawe.widget.SaveToFileIconButton
import com.kos.boxdrawer.generated.resources.Res
import com.kos.boxdrawer.generated.resources.metricMM
import com.kos.boxdrawer.generated.resources.torPazLocation
import com.kos.boxdrawer.generated.resources.torRadius
import com.kos.boxdrawer.generated.resources.torRadiusInner
import com.kos.boxdrawer.generated.resources.torRadiusOuter
import com.kos.boxdrawer.generated.resources.torSegmentsCount
import com.kos.boxdrawer.generated.resources.torSidesCount
import org.jetbrains.compose.resources.stringResource

@Composable
fun ToolbarForBublik(vm: BublikData) {
    var pazPositionLeftTop by remember { vm.pazPositionLeftTop }
    var pazPositionCenter by remember { vm.pazPositionCenter }
    var pazPositionLeftBottom by remember { vm.pazPositionLeftBottom }
    var pazPositionRightTop by remember { vm.pazPositionRightTop }
    var pazPositionRightBottom by remember { vm.pazPositionRightBottom }

    val radiusBublik = remember { vm.radiusBublik }
    val radius = remember { vm.radius }
    val holeRadius = remember { vm.holeRadius }
    val segmentCount = remember { vm.segmentCount }
    val sideCount = remember { vm.sideCount }

    Row(
        modifier = TabContentModifier
    ) {
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            NumericUpDownLine(stringResource(Res.string.torRadiusOuter), stringResource(Res.string.metricMM), radiusBublik)
            NumericUpDownLine(stringResource(Res.string.torRadius), stringResource(Res.string.metricMM), radius)
            NumericUpDownLine(stringResource(Res.string.torRadiusInner), stringResource(Res.string.metricMM), holeRadius)
            NumericUpDownLine(stringResource(Res.string.torSegmentsCount), stringResource(Res.string.metricMM), segmentCount)
            NumericUpDownLine(stringResource(Res.string.torSidesCount), stringResource(Res.string.metricMM), sideCount)
        }
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            Label(stringResource(Res.string.torPazLocation))
            Row(
                modifier = Modifier.size(120.dp, 52.dp)
            ) {
                Column(
                    modifier = Modifier.weight(weight = 1f, fill = true).fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    RunCheckBox(
                        checked = pazPositionLeftTop,
                        title = "",
                        onCheckedChange = remember(vm) {{ c ->
                            pazPositionLeftTop = c
                            vm.redrawBox()
                        }},
                    )
                    RunCheckBox(
                        checked = pazPositionLeftBottom,
                        title = "",
                        onCheckedChange =remember(vm) { { c ->
                            pazPositionLeftBottom = c
                            vm.redrawBox()
                        }},
                    )
                }
                Column(
                    modifier = Modifier.weight(weight = 1f, fill = true).fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ){
                    RunCheckBox(
                        checked = pazPositionCenter,
                        title = "",
                        onCheckedChange = remember(vm) {{ c ->
                            pazPositionCenter = c
                            vm.redrawBox()
                        }},
                    )
                }
                Column(
                    modifier = Modifier.weight(weight = 1f, fill = true).fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ){
                    RunCheckBox(
                        checked = pazPositionRightTop,
                        title = "",
                        onCheckedChange = remember(vm) {{ c ->
                            pazPositionRightTop = c
                            vm.redrawBox()
                        }},
                    )
                    RunCheckBox(
                        checked = pazPositionRightBottom,
                        title = "",
                        onCheckedChange = remember(vm) {{ c ->
                            pazPositionRightBottom = c
                            vm.redrawBox()
                        }},
                    )
                }
            }
        }
    }
}


@Composable
fun ToolbarActionForBublik(vm: BublikData) {
    Column(
    ) {
        SaveToFileButton(vm)
    }
}

@Composable
fun ToolbarActionIconForBublik(vm: BublikData) {
    Row(
    ) {
        SaveToFileIconButton(vm)
    }
}