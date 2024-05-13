package com.kos.boxdrawe.widget

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.presentation.DrawerViewModel
import com.kos.boxdrawe.themes.ThemeColors
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_BEZIER
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_BOX
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_BUBLIK
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_GRID
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_RECA
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_SOFT
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_TOOLS
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_TORTOISE
import com.kos.boxdrawe.widget.tabbar.ToolbarForBezier
import com.kos.boxdrawe.widget.tabbar.ToolbarForBox
import com.kos.boxdrawe.widget.tabbar.ToolbarForGrid
import com.kos.boxdrawe.widget.tabbar.ToolbarForReka
import com.kos.boxdrawe.widget.tabbar.ToolbarForSoft
import com.kos.boxdrawe.widget.tabbar.ToolbarForTools
import com.kos.boxdrawe.widget.tabbar.ToolbarForTortoise
import kotlinx.coroutines.delay
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter


val TabContentModifier =
    Modifier.fillMaxWidth().fillMaxHeight().padding(vertical = 4.dp, horizontal = 16.dp)

@Composable
fun TabBar(tabs: List<TabInfo>, vm: State<DrawerViewModel>) {
    val tabIndex = vm.value.tabIndex.collectAsState()

    Column(
        Modifier.fillMaxWidth().wrapContentHeight().background(Color.White)
    ) {
        ScrollableTabRow(
            selectedTabIndex = tabIndex.value,
            modifier = Modifier,
        ) {
            tabs.forEach { info ->
                Tab(
                    selected = info.id == tabIndex.value,
                    onClick = { vm.value.tabIndex.value = info.id },
                    modifier = Modifier,
                    text = { Text(info.title) },
                )
            }
        }
        Box(
            Modifier.fillMaxWidth().height(220.dp).background(ThemeColors.tabBackground)
        ) {
            when (tabIndex.value) {
                TAB_BOX -> ToolbarForBox(vm.value.box)
                TAB_TORTOISE -> ToolbarForTortoise(vm.value.tortoise)
                TAB_GRID -> ToolbarForGrid(vm.value.grid)
                TAB_SOFT -> ToolbarForSoft(vm.value.softRez, { vm.value.tortoise.figures.value })
                TAB_BEZIER -> ToolbarForBezier(vm.value.bezier)
                TAB_BUBLIK -> ToolbarForBublik(vm.value.bublik)
                TAB_RECA -> ToolbarForReka(vm.value.rectData)
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

suspend fun showFileChooser(directory: File, action: (String) -> Unit) {
    delay(100)

//    try {
//        MemoryStack.stackPush().use { stack ->
//            val aFilterPatterns = stack.mallocPointer(1)
//            aFilterPatterns.put(stack.UTF8("*.dxf"))
//            aFilterPatterns.flip()
//
//            TinyFileDialogs.tinyfd_saveFileDialog(
//                "Сохранить фигуру как...",
//                directory.absolutePath,
//                aFilterPatterns,
//                "Autocad (*.dxf)",
//            )?.let { fileName ->
//                action(fileName)
//            }
//        }
//    }catch (e : Exception){
//        e.printStackTrace()
//    }

    JFileChooser().apply {
        this.fileFilter = FileNameExtensionFilter("Autocad (*.dxf)", "dxf")
        this.currentDirectory = directory
        if (showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            val p = if (this.selectedFile.extension.lowercase() != "dxf") {
                ".dxf"
            } else
                ""
            action(this.selectedFile.path + p)
        }
    }
}

suspend fun showLoadFileChooser(directory: File, action: (String) -> Unit) {
    delay(100)

//    MemoryStack.stackPush().use { stack ->
//        val aFilterPatterns = stack.mallocPointer(1)
//        aFilterPatterns.put(stack.UTF8("*.dxf"))
//        aFilterPatterns.flip()
//
//        TinyFileDialogs.tinyfd_openFileDialog(
//            "Открыть фигуру",
//            directory.absolutePath,
//            aFilterPatterns,
//            "Autocad (*.dxf)",
//            false,
//        )?.let { fileName ->
//            action(fileName)
//        }
//    }

    JFileChooser().apply {
        this.fileFilter = FileNameExtensionFilter("Autocad (*.dxf)", "dxf")
        this.currentDirectory = directory
        if (showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            action(this.selectedFile.path)
        }
    }
}

@Composable
@Preview
fun TabBarPreview() {
    val vm = DrawerViewModel()
    MaterialTheme {
        TabBar(BoxDrawerToolBar.tabs, mutableStateOf(DrawerViewModel()))
    }
}

class TabInfo(
    val id: Int,
    val title: String,
)

object BoxDrawerToolBar {
    const val TAB_BOX = 0
    const val TAB_TORTOISE = 1
    const val TAB_GRID = 2
    const val TAB_SOFT = 3
    const val TAB_BEZIER = 4
    const val TAB_BUBLIK = 5
    const val TAB_RECA = 6
    const val TAB_TOOLS = 7

    val tabs = listOf(
        TabInfo(TAB_BOX, "Коробка"),
        TabInfo(TAB_TORTOISE, "Фигуры"),
        TabInfo(TAB_GRID, "Сетка"),
        TabInfo(TAB_SOFT, "Мягкий рез"),
        TabInfo(TAB_BEZIER, "Безье"),
        TabInfo(TAB_BUBLIK, "Бублик"),
        TabInfo(TAB_RECA, "Река"),
        TabInfo(TAB_TOOLS, "Инструменты"),
    )
}