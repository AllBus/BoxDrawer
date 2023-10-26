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
import androidx.compose.ui.window.AwtWindow
import com.kos.boxdrawe.presentation.*
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_BOX
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_GRID
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_SOFT
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_TOOLS
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_TORTOISE
import com.kos.boxdrawe.widget.tabbar.ToolbarForBox
import com.kos.boxdrawe.widget.tabbar.ToolbarForGrid
import com.kos.boxdrawe.widget.tabbar.ToolbarForSoft
import com.kos.boxdrawe.widget.tabbar.ToolbarForTortoise
import java.awt.FileDialog
import java.awt.Frame
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

val TabContentModifier =  Modifier.fillMaxWidth().fillMaxHeight().padding(vertical = 4.dp, horizontal = 16.dp)

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
fun ToolbarForTools(vm: ToolsData) {
    val boardWeight = remember { vm.boardWeight}
    val holeWeight = remember { vm.holeWeight}
    val holeDrop  = remember { vm.holeDrop}
    val holeOffset = remember { vm.holeOffset}

    Row(
        modifier = TabContentModifier
    ) {
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            Text(
                text = "Доска",
                modifier = Modifier,
                softWrap = false,
            )
            NumericUpDown("Толщина", "мм", boardWeight)
        }
        Column(
            modifier = Modifier.weight(weight = 1f, fill = true)
        ) {
            Text(
                text = "Отверстие",
                modifier = Modifier,
                softWrap = false,
            )
            NumericUpDown("Ширина", "мм", holeWeight)
            NumericUpDown("Уменьшение длины", "мм", holeDrop)
            NumericUpDown("Отступ от края", "мм", holeOffset)
        }
    }
}


//@Composable
//fun showFileChooser(
//   // parent: Frame? = null,
//    onCloseRequest: (result: String) -> Unit
//) = AwtWindow(
//    create = {
//        val parent: Frame? = null
//        object : FileDialog(parent, "Choose a file", SAVE) {
//            override fun setVisible(value: Boolean) {
//                super.setVisible(value)
//                if (value) {
//                    onCloseRequest(file.orEmpty())
//                }
//            }
//        }
//    },
//    dispose = FileDialog::dispose
//)

suspend fun showFileChooser(action: (String)-> Unit) {

    JFileChooser().apply {
        this.fileFilter = FileNameExtensionFilter("Autocad (*.dxf)", "dxf")
        if (showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            val p = if (this.selectedFile.extension.lowercase() !="dxf"){
                ".dxf"
            }else
                ""
            action(this.selectedFile.path+p)
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

