package com.kos.boxdrawe.widget

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.presentation.*
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_BOX
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_GRID
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_SOFT
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_TOOLS
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_TORTOISE
import com.kos.boxdrawe.widget.tabbar.*
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

val TabContentModifier =  Modifier.fillMaxWidth().fillMaxHeight().padding(vertical = 4.dp, horizontal = 16.dp)

@Composable
fun TabBar(tabs: List<TabInfo>, vm: State<DrawerViewModel>) {
    var tabIndex by remember { vm.value.tabIndex }

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
                TAB_BOX -> ToolbarForBox(vm.value.box)
                TAB_TORTOISE -> ToolbarForTortoise(vm.value.tortoise)
                TAB_GRID -> ToolbarForGrid(vm.value.grid)
                TAB_SOFT -> ToolbarForSoft(vm.value.softRez, { vm.value.tortoise.figures.value })
                TAB_TOOLS -> ToolbarForTools(vm.value.options)
            }
        }
        Box(
            Modifier.fillMaxWidth().height(1.dp).background(Color.DarkGray)
        )
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
fun TabBarPreview(){
    val vm =  DrawerViewModel()
    MaterialTheme {
        TabBar(BoxDrawerToolBar.tabs, mutableStateOf(DrawerViewModel()))
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

