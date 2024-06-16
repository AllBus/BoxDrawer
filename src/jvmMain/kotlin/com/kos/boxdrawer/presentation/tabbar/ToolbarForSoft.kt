package com.kos.boxdrawer.presentation.tabbar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.presentation.SoftRezData
import com.kos.boxdrawe.widget.NumericUpDown
import com.kos.boxdrawe.widget.NumericUpDownLine
import com.kos.boxdrawe.widget.RunButton
import com.kos.boxdrawe.widget.RunCheckBox
import com.kos.boxdrawe.widget.SaveToFileButton
import com.kos.boxdrawe.widget.TabContentModifier
import com.kos.boxdrawe.widget.showFileChooser
import com.kos.boxdrawer.generated.resources.Res
import com.kos.boxdrawer.generated.resources.metricCount
import com.kos.boxdrawer.generated.resources.metricMM
import com.kos.boxdrawer.generated.resources.rezAreaHeight
import com.kos.boxdrawer.generated.resources.rezAreaWidth
import com.kos.boxdrawer.generated.resources.rezCheckArea
import com.kos.boxdrawer.generated.resources.rezCheckFirstKine
import com.kos.boxdrawer.generated.resources.rezCheckStyle
import com.kos.boxdrawer.generated.resources.rezCountX
import com.kos.boxdrawer.generated.resources.rezCountY
import com.kos.boxdrawer.generated.resources.rezDelta
import com.kos.boxdrawer.generated.resources.rezLength
import com.kos.boxdrawer.generated.resources.rezProprzija
import com.kos.boxdrawer.generated.resources.rezSoedinenie
import com.kos.boxdrawer.generated.resources.rezTitleArea
import com.kos.boxdrawer.generated.resources.rezTitleCount
import com.kos.boxdrawer.generated.resources.rezTitleSoedinenie
import com.kos.boxdrawer.generated.resources.toolsButtonCopyCode
import com.kos.figure.IFigure
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun ToolbarForSoft(vm: SoftRezData) {
    var innerChecked by remember { vm.innerChecked }
    var isInSize by remember { vm.isInSize }
    var isDrawBox by remember { vm.isDrawBox }
    var firstSmall by remember { vm.firstSmall }

    val width = remember { vm.width }
    val height = remember { vm.height }
    val cellWidthCount = remember { vm.cellWidthCount }
    val cellHeightCount = remember { vm.cellHeightCount }
    val cellWidthDistance = remember { vm.cellWidthDistance }
    val cellHeightDistance = remember { vm.cellHeightDistance }
    val lineLength = remember { vm.lineLength }

    Row(
        modifier = TabContentModifier
    ) {
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            Text(
                text = stringResource(Res.string.rezTitleArea),
                modifier = Modifier,
                softWrap = false,
            )
            NumericUpDownLine(stringResource(Res.string.rezAreaWidth), stringResource(Res.string.metricMM), width)
            NumericUpDownLine(stringResource(Res.string.rezAreaHeight), stringResource(Res.string.metricMM), height)
            RunCheckBox(
                checked = isDrawBox,
                title = stringResource(Res.string.rezCheckArea),
                onCheckedChange = { c ->
                    isDrawBox = c
                    vm.redraw()
                },
            )
        }
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            RunCheckBox(
                checked = isInSize,
                title = stringResource(Res.string.rezCheckStyle),
                onCheckedChange = { c ->
                    isInSize = c
                    vm.redraw()
                },
            )
            if (isInSize){
                NumericUpDownLine(stringResource(Res.string.rezLength), stringResource(Res.string.metricMM), lineLength)
                RunCheckBox(
                    checked = firstSmall,
                    title = stringResource(Res.string.rezCheckFirstKine),
                    onCheckedChange = { c ->
                        firstSmall = c
                        vm.redraw()
                    },
                )
            }else {
                Text(
                    text = stringResource(Res.string.rezTitleCount),
                    modifier = Modifier,
                    softWrap = false,
                )
                NumericUpDownLine(stringResource(Res.string.rezCountX),  stringResource(Res.string.metricCount), cellWidthCount)
                NumericUpDownLine(stringResource(Res.string.rezCountY),  stringResource(Res.string.metricCount), cellHeightCount, enabled = !innerChecked)
                RunCheckBox(
                    checked = innerChecked,
                    title = stringResource(Res.string.rezProprzija),
                    onCheckedChange = { c ->
                        innerChecked = c
                        vm.redraw()
                    },
                )
            }
        }
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            Text(
                text = stringResource(Res.string.rezTitleSoedinenie),
                modifier = Modifier,
                softWrap = false,
            )
            NumericUpDownLine(stringResource(Res.string.rezSoedinenie), stringResource(Res.string.metricMM), cellWidthDistance)
            NumericUpDownLine(stringResource(Res.string.rezDelta), stringResource(Res.string.metricMM), cellHeightDistance)
        }

    }
}

@Composable
fun ToolbarActionForSoft(vm: SoftRezData){
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    Column(
    ) {
        SaveToFileButton(vm)
        Spacer(Modifier.height(4.dp))
        RunButton(stringResource(Res.string.toolsButtonCopyCode)) {
            coroutineScope.launch {
                clipboardManager.setText(AnnotatedString(vm.print()))
            }
        }
    }
}