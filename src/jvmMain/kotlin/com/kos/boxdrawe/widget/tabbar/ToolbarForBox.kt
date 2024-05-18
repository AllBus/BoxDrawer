package com.kos.boxdrawe.widget.tabbar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.presentation.BoxData
import com.kos.boxdrawe.widget.*
import com.kos.boxdrawe.widget.model.ButtonData
import com.kos.boxdrawer.detal.box.PazExt
import kotlinx.coroutines.launch

@Composable
fun ToolbarForBox(vm: BoxData) {

    var insideChecked by remember { vm.insideChecked }
    var polkiInChecked by remember { vm.polkiInChecked }
    var alternative by remember { vm.alternative }

    val width = remember { vm.width }
    val height = remember { vm.height }
    val weight = remember { vm.weight }
    val text = rememberSaveable(key = "ToolbarForBox.Text") { vm.text }


    val edgeFL = remember { vm.edgeFL }
    val edgeBL = remember { vm.edgeBL }
    val edgeBR = remember { vm.edgeBR }
    val edgeFR = remember { vm.edgeFR }

    Row(
        modifier = TabContentModifier
    ) {
        Column(
            modifier = Modifier.weight(weight = 2f, fill = true).padding(end = 2.dp).verticalScroll(rememberScrollState())
        ) {
            Row(
            ) {
                Column(
                    modifier = Modifier
                        .weight(weight = 1f, fill = true)

                ) {
                    NumericUpDown("Длина", "мм", width)
                    NumericUpDown("Ширина", "мм", weight)
                    NumericUpDown("Высота", "мм", height)


                    Row(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Label(
                            "Высоты\nуглов коробки",
                            singleLine = false,
                           // modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        NumericUpDown("", "", edgeFL, modifier = Modifier.width(50.dp))
                        NumericUpDown("", "", edgeBL, modifier = Modifier.width(50.dp))
                        NumericUpDown("", "", edgeBR, modifier = Modifier.width(50.dp))
                        NumericUpDown("", "мм", edgeFR, modifier = Modifier.width(80.dp))
                    }
                }
                Column(
                    modifier = Modifier.weight(weight = 1f, fill = true)
                ) {
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
            }

            BoxAdvancedProperties(
                vm,
            )
        }
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            RunCheckBox(
                checked = insideChecked,
                title = "Размеры по внутреннему объёму",
                onCheckedChange = { c ->
                    insideChecked = c
                    vm.redrawBox()
                },
            )
            RunCheckBox(
                checked = polkiInChecked,
                title = "Учитывать толщину полки",
                onCheckedChange = { c ->
                    polkiInChecked = c
                    vm.redrawBox()
                },
            )
            RunCheckBox(
                checked = alternative,
                title = "Альтернативное расположение",
                onCheckedChange = { c ->
                    alternative = c
                    vm.redrawBox()
                },
            )
            EditText(title = "Полки", value = text, enabled = true) { vm.createBox(it) }
        }
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
        ButtonData(PazExt.PAZ_NONE, painterResource("drawable/act_line.png")),
        ButtonData(PazExt.PAZ_HOLE, painterResource("drawable/act_hole.png")),
        ButtonData(PazExt.PAZ_PAZ, painterResource("drawable/act_paz.png")),
        ButtonData(PazExt.PAZ_OUT, painterResource("drawable/act_outside.png")),
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
                Label("Форма крышки", Modifier.width(120.dp))
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
                Label("Форма дна", Modifier.width(120.dp))
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