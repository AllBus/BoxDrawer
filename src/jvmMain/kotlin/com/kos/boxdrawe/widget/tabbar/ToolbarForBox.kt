package com.kos.boxdrawe.widget.tabbar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.kos.boxdrawe.presentation.BoxData
import com.kos.boxdrawe.widget.*
import com.kos.boxdrawe.widget.model.ButtonData
import com.kos.boxdrawer.detal.box.PazExt
import kotlinx.coroutines.launch

@Composable
fun ToolbarForBox(vm: BoxData) {

    var insideChecked by remember { vm.insideChecked }
    var polkiInChecked by remember { vm.polkiInChecked }

    val width = remember { vm.width }
    val height = remember { vm.height }
    val weight = remember { vm.weight }
    val text = rememberSaveable(key = "ToolbarForBox.Text") { vm.text }
    val coroutineScope = rememberCoroutineScope()
    val selectZigTopId = remember { vm.selectZigTopId }
    val selectZigBottomId = remember { vm.selectZigBottomId }

    val zigVariants = listOf(
        ButtonData(PazExt.PAZ_NONE, painterResource("drawable/act_line.png")),
        ButtonData(PazExt.PAZ_HOLE, painterResource("drawable/act_hole.png")),
        ButtonData(PazExt.PAZ_PAZ, painterResource("drawable/act_paz.png")),
        //   ButtonData(PazExt.PAZ_BACK, painterResource("drawable/act_paz_in.png")),
    )

    val Wchecked = remember { vm.widthZigState.enable }
    val WwidthInput = remember { vm.widthZigState.width }
    val WdeltaInput = remember { vm.widthZigState.delta }
    val WheightInput = remember { vm.widthZigState.height }

    val Hchecked = remember { vm.heightZigState.enable }
    val HwidthInput = remember { vm.heightZigState.width }
    val HdeltaInput = remember { vm.heightZigState.delta }
    val HheightInput = remember { vm.heightZigState.height }

    val Wechecked = remember { vm.weightZigState.enable }
    val WewidthInput = remember { vm.weightZigState.width }
    val WedeltaInput = remember { vm.weightZigState.delta }
    val WeheightInput = remember { vm.weightZigState.height }

    val Pchecked = remember { vm.polkaZigState.enable }
    val PwidthInput = remember { vm.polkaZigState.width }
    val PdeltaInput = remember { vm.polkaZigState.delta }
    val PheightInput = remember { vm.polkaZigState.height }

    val Pbchecked = remember { vm.polkaPolZigState.enable }
    val PbwidthInput = remember { vm.polkaPolZigState.width }
    val PbdeltaInput = remember { vm.polkaPolZigState.delta }
    val PbheightInput = remember { vm.polkaPolZigState.height }


    Row(
        modifier = TabContentModifier
    ) {
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            NumericUpDown("Длина", "мм", width)
            NumericUpDown("Ширина", "мм", weight)
            NumericUpDown("Высота", "мм", height)
            Label("Форма соединения крышки", Modifier.align(Alignment.End))
            SegmentButton(
                selectZigTopId,
                zigVariants,
                Modifier.align(Alignment.End)
            ) { id ->
                selectZigTopId.value = id
                vm.redrawBox()
            }
            Label("Форма соединения дна", Modifier.align(Alignment.End))
            SegmentButton(
                selectZigBottomId,
                zigVariants,
                Modifier.align(Alignment.End)
            ) { id ->
                selectZigBottomId.value = id
                vm.redrawBox()
            }
        }
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {

            Label("Свойства пазов, по направлениям")
            Row() {
                ZigZagLabel()
                ZigZagInput(
                    modifier = Modifier.weight(weight = 1f, fill = true),
                      title = "длина",
                    drawNames = false,
                    checked = Wchecked,
                    widthInput= WwidthInput,
                    deltaInput= WdeltaInput,
                    heightInput = WheightInput,

                )
                ZigZagInput(
                    modifier = Modifier.weight(weight = 1f, fill = true),
                    title = "ширина",
                    drawNames = false,
                    checked = Wechecked,
                    widthInput= WewidthInput,
                    deltaInput= WedeltaInput,
                    heightInput = WeheightInput,

                    )
                ZigZagInput(
                    modifier = Modifier.weight(weight = 1f, fill = true),
                    title = "высота",
                    drawNames = false,
                    checked = Hchecked,
                    widthInput= HwidthInput,
                    deltaInput= HdeltaInput,
                    heightInput = HheightInput,

                    )
                ZigZagInput(
                    modifier = Modifier.weight(weight = 1f, fill = true),
                    title = "полки",
                    drawNames = false,
                    checked =     Pchecked,
                    widthInput=   PwidthInput,
                    deltaInput=   PdeltaInput,
                    heightInput = PheightInput,
                )
                ZigZagInput(
                    modifier = Modifier.weight(weight = 1f, fill = true),
                    title = "дно полки",
                    drawNames = false,
                    checked =     Pbchecked,
                    widthInput=   PbwidthInput,
                    deltaInput=   PbdeltaInput,
                    heightInput = PbheightInput,
                )
            }
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
            EditText("Фигуры", "", text, true) { vm.createBox(it) }
        }
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            RunButton("Нарисовать коробку") {
                coroutineScope.launch {
                    showFileChooser { f -> vm.saveBox(f, text.value) }
                }
            }
        }

    }
}