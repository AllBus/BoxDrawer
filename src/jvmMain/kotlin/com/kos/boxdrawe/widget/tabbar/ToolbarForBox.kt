package com.kos.boxdrawe.widget.tabbar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.presentation.BoxData
import com.kos.boxdrawe.presentation.ZigZagState
import com.kos.boxdrawe.widget.*
import com.kos.boxdrawe.widget.model.ButtonData
import com.kos.boxdrawer.detal.box.PazExt
import com.kos.boxdrawer.generated.resources.Res
import com.kos.boxdrawer.generated.resources.act_hole
import com.kos.boxdrawer.generated.resources.act_line
import com.kos.boxdrawer.generated.resources.act_outside
import com.kos.boxdrawer.generated.resources.act_paz
import com.kos.boxdrawer.generated.resources.boxBottom
import com.kos.boxdrawer.generated.resources.boxEdges
import com.kos.boxdrawer.generated.resources.boxHeight
import com.kos.boxdrawer.generated.resources.boxPolki
import com.kos.boxdrawer.generated.resources.boxPreviewStyle
import com.kos.boxdrawer.generated.resources.boxSizeInside
import com.kos.boxdrawer.generated.resources.boxSizeWithBoard
import com.kos.boxdrawer.generated.resources.boxTop
import com.kos.boxdrawer.generated.resources.boxWeight
import com.kos.boxdrawer.generated.resources.boxWidth
import com.kos.boxdrawer.generated.resources.metricMM
import io.github.windedge.table.DataTable
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ToolbarForBox(vm: BoxData) {

    val text = rememberSaveable(key = "ToolbarForBox.Text") { vm.text }


    val rowArrange = remember { mutableStateOf(1) }
    val density = LocalDensity.current

    Row(
        modifier = TabContentModifier.onSizeChanged {
            rowArrange.value = if (it.width < 600*density.density) 2 else 1
        },
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        if (rowArrange.value == 1 ) {
            ToolbarBoxRowOne(
                vm = vm,
                modifier = Modifier.weight(weight = 0.8f, fill = true)
            )
            ToolbarBoxRowTwo(vm, Modifier.weight(weight = 1.2f, fill = true))
            ToolbarBoxRowThree(vm, text, Modifier.weight(weight = 0.8f, fill = true))
        } else{
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ){
                ToolbarBoxRowOne(
                    vm = vm,
                    modifier = Modifier //.weight(weight = 1f, fill = true)
                )
                ToolbarBoxRowTwo(vm, Modifier) //.weight(weight = 1f, fill = true))
                ToolbarBoxRowThree(vm, text, Modifier) //.weight(weight = 1f, fill = true))
            }

        }
    }
}

@Composable
private fun ToolbarBoxRowThree(
    vm: BoxData,
    text: MutableState<String>,
    modifier: Modifier,
) {
    var insideChecked by remember { vm.insideChecked }
    var polkiInChecked by remember { vm.polkiInChecked }
    var alternative by remember { vm.alternative }

    Column(
        modifier = modifier
    ) {
        RunCheckBox(
            checked = insideChecked,
            title = stringResource(Res.string.boxSizeInside),
            onCheckedChange = { c ->
                insideChecked = c
                vm.redrawBox()
            },
        )
        RunCheckBox(
            checked = polkiInChecked,
            title = stringResource(Res.string.boxSizeWithBoard),
            onCheckedChange = { c ->
                polkiInChecked = c
                vm.redrawBox()
            },
        )
        RunCheckBox(
            checked = alternative,
            title = stringResource(Res.string.boxPreviewStyle),
            onCheckedChange = { c ->
                alternative = c
                vm.redrawBox()
            },
        )
        EditText(title = stringResource(Res.string.boxPolki), value = text, enabled = true) { vm.createBox(it) }
    }
}

@Composable
private fun ToolbarBoxRowOne(
    vm: BoxData,
    modifier: Modifier,
) {
    val width = remember { vm.width }
    val height = remember { vm.height }
    val weight = remember { vm.weight }
    val edgeFL = remember { vm.edgeFL }
    val edgeBL = remember { vm.edgeBL }
    val edgeBR = remember { vm.edgeBR }
    val edgeFR = remember { vm.edgeFR }

    Column(
        modifier = modifier.padding(end = 2.dp)
            //.verticalScroll(rememberScrollState())
    ) {
        Row(
        ) {
            Column(
                modifier = Modifier
                    .weight(weight = 1f, fill = true)

            ) {
                NumericUpDown(stringResource(Res.string.boxWidth), stringResource(Res.string.metricMM), width)
                NumericUpDown(stringResource(Res.string.boxWeight), stringResource(Res.string.metricMM), weight)
                NumericUpDown(stringResource(Res.string.boxHeight), stringResource(Res.string.metricMM), height)

                Label(
                    stringResource(Res.string.boxEdges),
                    singleLine = false,
                    modifier = Modifier.align(Alignment.End)
                )
                Row(
                    modifier = Modifier.align(Alignment.End)
                ) {
                    NumericUpDown("", "", edgeFL, modifier = Modifier.width(50.dp))
                    NumericUpDown("", "", edgeBL, modifier = Modifier.width(50.dp))
                    NumericUpDown("", "", edgeBR, modifier = Modifier.width(50.dp))
                    NumericUpDown("", stringResource(Res.string.metricMM), edgeFR, modifier = Modifier.width(80.dp))
                }
            }

        }
    }
}

@Composable
private fun ToolbarBoxRowTwo(vm: BoxData, modifier: Modifier) {
    Column(
        modifier = modifier
    ) {
        ZigZagBox(vm)
        BoxAdvancedProperties(
            vm,
        )
    }
}

@Composable
private fun ZigZagTable(
    vm: BoxData,
) {
    DataTable(
        modifier = Modifier.size(400.dp, 400.dp),
        columns = {
            column { Label("Пазы", singleLine = true) }
            column { Label("длина", singleLine = true) }
            column { Label("ширина", singleLine = true) }
            column { Label("высота", singleLine = true) }
            column { Label("полки", singleLine = true) }
            column { Label("дно полки", singleLine = true) }

        }
    ) {
        row {
            cell { Label("Длина паза", singleLine = true) }
            cell {
                val WwidthInput = remember { vm.widthZigState.width }
                NumericUpDown("", "", WwidthInput)
            }
            cell {
                val WwidthInput = remember { vm.weightZigState.width }
                NumericUpDown("", "", WwidthInput)
            }
            cell {
                val WwidthInput = remember { vm.heightZigState.width }
                NumericUpDown("", "", WwidthInput)
            }
            cell {
                val WwidthInput = remember { vm.polkaZigState.width }
                NumericUpDown("", "", WwidthInput)
            }
            cell {
                val WwidthInput = remember { vm.polkaPolZigState.width }
                NumericUpDown("", "", WwidthInput)
            }
        }

        row {
            cell { Label("Дельта", singleLine = true) }
            cell {
                val WwidthInput = remember { vm.widthZigState.delta }
                NumericUpDown("", "", WwidthInput)
            }
            cell {
                val WwidthInput = remember { vm.weightZigState.delta }
                NumericUpDown("", "", WwidthInput)
            }
            cell {
                val WwidthInput = remember { vm.heightZigState.delta }
                NumericUpDown("", "", WwidthInput)
            }
            cell {
                val WwidthInput = remember { vm.polkaZigState.delta }
                NumericUpDown("", "", WwidthInput)
            }
            cell {
                val WwidthInput = remember { vm.polkaPolZigState.delta }
                NumericUpDown("", "", WwidthInput)
            }
        }

        row {
            cell { Label("Толщина паза", singleLine = true) }
            cell {
                val WwidthInput = remember { vm.widthZigState.height }
                NumericUpDown("", "", WwidthInput)
            }
            cell {
                val WwidthInput = remember { vm.weightZigState.height }
                NumericUpDown("", "", WwidthInput)
            }
            cell {
                val WwidthInput = remember { vm.heightZigState.height }
                NumericUpDown("", "", WwidthInput)
            }
            cell {
                val WwidthInput = remember { vm.polkaZigState.height }
                NumericUpDown("", "", WwidthInput)
            }
            cell {
                val WwidthInput = remember { vm.polkaPolZigState.height }
                NumericUpDown("", "", WwidthInput)
            }
        }

        row {
            cell { Label("Есть", singleLine = true) }
            cell {
                val checked = remember { vm.widthZigState.enable }
                RunCheckBox(checked.value, "") { c ->
                    checked.value = c
                    vm.widthZigState.redrawBox()
                }
            }
            cell {
                val checked = remember { vm.weightZigState.enable }
                RunCheckBox(checked.value, "") { c ->
                    checked.value = c
                    vm.widthZigState.redrawBox()
                }
            }
            cell {
                val checked = remember { vm.heightZigState.enable }
                RunCheckBox(checked.value, "") { c ->
                    checked.value = c
                    vm.widthZigState.redrawBox()
                }
            }
            cell {
                val checked = remember { vm.polkaZigState.enable }
                RunCheckBox(checked.value, "") { c ->
                    checked.value = c
                    vm.widthZigState.redrawBox()
                }
            }
            cell {
                val checked = remember { vm.polkaPolZigState.enable }
                RunCheckBox(checked.value, "") { c ->
                    checked.value = c
                    vm.widthZigState.redrawBox()
                }
            }
        }
    }
}

@Composable
private fun ZigZagBox(vm: BoxData) {
    Row() {
        ZigZagLabel()
        ZigZagInput(
            modifier = Modifier.weight(weight = 1f, fill = true),
            title = "длина",
            drawNames = false,
            zigState = vm.widthZigState

        )
        ZigZagInput(
            modifier = Modifier.weight(weight = 1f, fill = true),
            title = "ширина",
            drawNames = false,
            zigState = vm.weightZigState

        )
        ZigZagInput(
            modifier = Modifier.weight(weight = 1f, fill = true),
            title = "высота",
            drawNames = false,
            zigState = vm.heightZigState

        )
        ZigZagInput(
            modifier = Modifier.weight(weight = 1f, fill = true),
            title = "полки",
            drawNames = false,
            zigState = vm.polkaZigState
        )
        ZigZagInput(
            modifier = Modifier.weight(weight = 1f, fill = true),
            title = "дно полки",
            drawNames = false,
            zigState = vm.polkaPolZigState
        )
    }
}

@Composable
private fun ColumnScope.BoxAdvancedProperties(
    vm: BoxData,
) {
    val selectZigTopId = remember { vm.selectZigTopId }
    val selectZigBottomId = remember { vm.selectZigBottomId }
    val selectZigEdgeId = remember { vm.selectZigEdgeId }
    val topOffset = remember { vm.topOffset }
    val bottomOffset = remember { vm.bottomOffset }
    val topHoleOffset = remember { vm.topHoleOffset }
    val bottomHoleOffset = remember { vm.bottomHoleOffset }
    val edgeHoleOffset = remember { vm.edgeHoleOffset }

    val bottomRadius = remember { vm.bottomRadius }
    val topRadius = remember { vm.topRadius }
    val edgeRadius = remember { vm.edgeRadius }


    val zigVariants = listOf(
        ButtonData(PazExt.PAZ_NONE, painterResource(Res.drawable.act_line)),
        ButtonData(PazExt.PAZ_HOLE, painterResource(Res.drawable.act_hole)),
        ButtonData(PazExt.PAZ_PAZ, painterResource(Res.drawable.act_paz)),
        ButtonData(PazExt.PAZ_OUT, painterResource(Res.drawable.act_outside)),
        //   ButtonData(PazExt.PAZ_PAPER, painterResource( "drawable/act_paper.png")),
        //   ButtonData(PazExt.PAZ_BACK, painterResource("drawable/act_paz_in.png")),
    )

//    val edgeZigVariants = listOf(
//        ButtonData(PazExt.PAZ_NONE, painterResource("drawable/act_line.png")),
//        ButtonData(PazExt.PAZ_HOLE, painterResource("drawable/act_hole.png")),
//        ButtonData(PazExt.PAZ_PAZ, painterResource("drawable/act_paz.png")),
//    )

    Row(
        Modifier.Companion.align(Alignment.End)
    ) {
        Column() {
            //Label("Форма крышки", Modifier.Companion.align(Alignment.CenterHorizontally))
            Row() {
                Label(stringResource(Res.string.boxTop), Modifier.width(60.dp))
                NumericUpDown(
                    "↕",
                    "",
                    topOffset,
                    modifier = Modifier.width(60.dp),
                    titleWeight = false
                )
                NumericUpDown(
                    "о",
                    "",
                    topHoleOffset,
                    modifier = Modifier.width(60.dp),
                    titleWeight = false
                )
                NumericUpDown(
                    "◵",
                    "",
                    topRadius,
                    modifier = Modifier.width(60.dp),
                    titleWeight = false
                )
            }
        }
        SegmentButton(
            selectZigTopId,
            zigVariants,
            Modifier.align(Alignment.CenterVertically).height(32.dp)
        ) { id ->
            selectZigTopId.value = id
            vm.redrawBox()
        }
    }
    Row(
        Modifier.Companion.align(Alignment.End)
    )
    {
        Column() {
         //   Label("Форма дна", Modifier.Companion.align(Alignment.CenterHorizontally))
            Row() {
                Label(stringResource(Res.string.boxBottom), Modifier.width(60.dp))
                NumericUpDown(
                    "↕",
                    "",
                    bottomOffset,
                    modifier = Modifier.width(60.dp),
                    titleWeight = false
                )
                NumericUpDown(
                    "о",
                    "",
                    bottomHoleOffset,
                    modifier = Modifier.width(60.dp),
                    titleWeight = false
                )
                NumericUpDown(
                    "◵",
                    "",
                    bottomRadius,
                    modifier = Modifier.width(60.dp),
                    titleWeight = false
                )
            }
        }
        SegmentButton(
            selectZigBottomId,
            zigVariants,
            Modifier.align(Alignment.CenterVertically).height(32.dp)
        ) { id ->
            selectZigBottomId.value = id
            vm.redrawBox()
        }
    }
//    Label("Форма соединения стенок", Modifier.Companion.align(Alignment.End))
//    Row(
//        Modifier.Companion.align(Alignment.End)
//    )
//    {
//        NumericUpDown("о", "", edgeHoleOffset, modifier = Modifier.width(60.dp), titleWeight = false)
//        NumericUpDown("◵", "", edgeRadius, modifier = Modifier.width(60.dp), titleWeight = false)
//        SegmentButton(
//            selectZigEdgeId,
//            edgeZigVariants,
//            Modifier
//        ) { id ->
//            selectZigEdgeId.value = id
//            vm.redrawBox()
//        }
//    }


}


@Composable
fun ToolbarActionForBox(vm: BoxData) {
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    Column(
    ) {
        SaveToFileButton(vm)

        Spacer(Modifier.height(4.dp))
        RunButton("Скопировать код") {
            coroutineScope.launch {
                clipboardManager.setText(AnnotatedString(vm.print()))
            }
        }
    }
}