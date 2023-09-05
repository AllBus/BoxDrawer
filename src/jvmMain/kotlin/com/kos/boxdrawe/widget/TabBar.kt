package com.kos.boxdrawe.widget

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.presentation.*
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_BOX
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_GRID
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_SOFT
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_TOOLS
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_TORTOISE
import figure.IFigure
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

private val TabContentModifier =  Modifier.fillMaxWidth().fillMaxHeight().padding(vertical = 4.dp, horizontal = 16.dp)

@Composable
fun TabBar(tabs: List<TabInfo>, vm: DrawerViewModel) {
    var tabIndex by remember { vm.tabIndex }

    Column(
        Modifier.fillMaxWidth().wrapContentHeight().background(Color.White)
    ){
        ScrollableTabRow(
            selectedTabIndex = tabIndex,
                    modifier = Modifier,
        ) {
            tabs.forEach { info ->
                Tab(
                    selected = info.id == tabIndex,
                    onClick = { tabIndex = info.id },
                    modifier = Modifier,
                    text = { Text(info.title) },
                )
            }
        }
        Box(
            Modifier.fillMaxWidth().height(240.dp).background(Color.LightGray)) {
            when(tabIndex) {
                TAB_BOX -> ToolbarForBox(vm.box)
                TAB_TORTOISE -> ToolbarForTortoise(vm.tortoise)
                TAB_GRID -> ToolbarForGrid(vm.grid)
                TAB_SOFT -> ToolbarForSoft(vm.softRez, { vm.tortoise.figures.value })
                TAB_TOOLS -> ToolbarForTools(vm.options)
            }
        }
        Box(
            Modifier.fillMaxWidth().height(1.dp).background(Color.DarkGray)
        )
    }
}

@Composable
fun ToolbarForSoft(vm: SoftRezData, figures: () -> IFigure) {
    var innerChecked by remember {vm.innerChecked }

    val width = remember { vm.width }
    val height = remember { vm.height }
    val cellWidthCount = remember { vm.cellWidthCount }
    val cellHeightCount = remember { vm.cellHeightCount }
    val cellWidthDistance = remember { vm.cellWidthDistance }
    val cellHeightDistance = remember { vm.cellHeightDistance }

    Row(
        modifier = TabContentModifier
    ) {
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            Text(
                text = "Область",
                modifier = Modifier,
                softWrap = false,
            )
            NumericUpDown("Длина", "мм", width)
            NumericUpDown("Высота", "мм", height)

        }
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            Text(
                text = "Количество элементов",
                modifier = Modifier,
                softWrap = false,
            )
            NumericUpDown("По длине", "шт", cellWidthCount)
            NumericUpDown("По высоте", "шт", cellHeightCount, enabled = !innerChecked)
            RunCheckBox(
                checked = innerChecked,
                title = "Сохранять пропорции",
                onCheckedChange = { c -> innerChecked = c },
            )
        }
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            Text(
                text = "Растояние между резами",
                modifier = Modifier,
                softWrap = false,
            )

            NumericUpDown("X", "мм", cellWidthDistance)
            NumericUpDown("Y", "мм", cellHeightDistance)
        }
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            RunButton("Нарисовать деталь", { showFileChooser{f -> vm.saveRez(f, figures())}})
        }
    }
}

@Composable
fun ToolbarForGrid(vm: GridData) {
    var roundChecked by remember {vm.roundChecked }
    var innerChecked by remember { vm.innerChecked }

    val widthCell = remember { vm.widthCell }
    val widthFrame = remember { vm.widthFrame }
    val radius = remember {  vm.radius }
    val cellWidthCount = remember { vm.cellWidthCount }
    val cellHeightCount = remember { vm.cellHeightCount }
    val innerWidth = remember { vm.innerWidth }
    val innerRadius = remember { vm.innerRadius }

    Row(
        modifier = TabContentModifier
    ) {
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            NumericUpDown("Ширина ячейки", "мм", widthCell)
            NumericUpDown("Ширина рамки", "мм", widthFrame)
            RunCheckBox(
                checked = roundChecked,
                title = "Скруглять углы",
                onCheckedChange = { c -> roundChecked = c },
            )
            NumericUpDown("Радиус скругления", "мм", radius, enabled = roundChecked)
        }
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            NumericUpDown("По горизонтали", "ячеек", cellWidthCount)
            NumericUpDown("По вертикали", "ячеек", cellHeightCount)
        }
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            RunCheckBox(
                checked = innerChecked,
                title = "Внутрение квадраты",
                onCheckedChange = { c -> innerChecked = c },
            )
            NumericUpDown("Сторона", "мм", innerWidth)
            NumericUpDown("Радиус", "мм", innerRadius)
        }
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            RunButton("Нарисовать деталь", {})
            RunButton("Посторить по тексту", {})
            RunButton("Получить текст", {})
        }
    }
}

@Composable
fun ToolbarForTortoise(vm: TortoiseData) {
    val text = rememberSaveable(key = "ToolbarForTortoise.Text") { vm.text }

    Row(
        modifier = TabContentModifier
    ) {
        Column(
            modifier = Modifier.weight(weight = 2f, fill = true).padding(end = 8.dp)
        ) {
            EditText("Фигуры", "", text, true ){vm.createTortoise(it)}
        }
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            RunButton("Нарисовать деталь") { showFileChooser { f -> vm.saveTortoise(f, text.value) } }
        }
    }
}

@Composable
fun ToolbarForBox(vm: BoxData) {

    var insideChecked by remember{ vm.insideChecked }

    val width = remember { vm.width }
    val height = remember { vm.height }
    val weight = remember { vm.weight }
    val text = rememberSaveable(key = "ToolbarForBox.Text") { vm.text }

    Row(
       modifier = TabContentModifier
    ) {
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            NumericUpDown("Длина", "мм", width)
            NumericUpDown("Ширина", "мм", height)
            NumericUpDown("Высота", "мм", weight)
        }
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            RunCheckBox(
                checked = insideChecked,
                title = "Размеры по внутреннему объёму",
                onCheckedChange = { c -> insideChecked = c },
            )
            EditText("Фигуры", "", text, true ){vm.createBox(it)}
        }
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            RunButton("Нарисовать коробку") { showFileChooser { f -> vm.saveBox(f, text.value) } }
        }
    }
}

@Composable
fun ToolbarForTools(vm: ToolsData) {


}

fun showFileChooser(action: (String)-> Unit) {

//    val choice = BetterFileDialog.saveFile(null, "Сохранить деталь как?", "",
//        BetterFileDialog.Filter("AutoCad", "*.dxf"),
//        BetterFileDialog.ANY_FILTER
//        )
//
//    if (choice != null){
//        action(choice)
//    }

    JFileChooser().apply {
        this.fileFilter = FileNameExtensionFilter("Autocad (*.dxf)", "dxf")
        if (showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            action(this.selectedFile.path)
        }
    }
}

@Composable
@Preview
fun TabBarPreview(vm: DrawerViewModel){
    MaterialTheme {
        TabBar(BoxDrawerToolBar.tabs, DrawerViewModel())
    }
}

class TabInfo(
    val id:Int,
    val title:String,
)

object BoxDrawerToolBar{
    const val TAB_BOX = 0
    const val TAB_TORTOISE = 1
    const val TAB_GRID = 2
    const val TAB_SOFT = 3
    const val TAB_TOOLS = 4

    val tabs = listOf(
        TabInfo(TAB_BOX, "Коробка"),
        TabInfo(TAB_TORTOISE, "Фигуры"),
        TabInfo(TAB_GRID, "Сетка"),
        TabInfo(TAB_SOFT, "Мягкий рез"),
        TabInfo(TAB_TOOLS, "Инструменты"),
    )

}

