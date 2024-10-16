package com.kos.boxdrawer.presentation.tabbar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.kos.boxdrawe.presentation.BublikData
import com.kos.boxdrawe.widget.NumericUpDownLine
import com.kos.boxdrawe.widget.RunCheckBox
import com.kos.boxdrawe.widget.SaveToFileButton

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
            NumericUpDownLine("Радиус тора", "мм", radiusBublik)
            NumericUpDownLine("Радиус", "мм", radius)
            NumericUpDownLine("Радиус отверстия", "мм", holeRadius)
            NumericUpDownLine("Число сегментов", "мм", segmentCount)
            NumericUpDownLine("Число сторон", "мм", sideCount)
        }
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            RunCheckBox(
                checked = pazPositionLeftTop,
                title = "по левому краю сверху",
                onCheckedChange = { c ->
                    pazPositionLeftTop = c
                    vm.redrawBox()
                },
            )
            RunCheckBox(
                checked = pazPositionCenter,
                title = "по центру",
                onCheckedChange = { c ->
                    pazPositionCenter = c
                    vm.redrawBox()
                },
            )

            RunCheckBox(
                checked = pazPositionLeftBottom,
                title = "по левому краю снизу",
                onCheckedChange = { c ->
                    pazPositionLeftBottom = c
                    vm.redrawBox()
                },
            )

            RunCheckBox(
                checked = pazPositionRightTop,
                title = "по правому краю сверху",
                onCheckedChange = { c ->
                    pazPositionRightTop = c
                    vm.redrawBox()
                },
            )

            RunCheckBox(
                checked = pazPositionRightBottom,
                title = "по правому краю снизу",
                onCheckedChange = { c ->
                    pazPositionRightBottom = c
                    vm.redrawBox()
                },
            )
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