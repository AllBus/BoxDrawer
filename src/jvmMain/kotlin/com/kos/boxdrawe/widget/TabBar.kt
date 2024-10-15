package com.kos.boxdrawe.widget

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.icons.Expand
import com.kos.boxdrawe.presentation.DrawerViewModel
import com.kos.boxdrawe.presentation.Tools
import com.kos.boxdrawe.themes.ThemeColors
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_BEZIER
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_BOX
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_BUBLIK
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_DXF
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_GRID
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_IMAGE
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_REKA
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_SOFT
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_TOOLS
import com.kos.boxdrawe.widget.BoxDrawerToolBar.TAB_TORTOISE
import com.kos.boxdrawe.widget.tabbar.ToolbarContainer
import com.kos.boxdrawer.generated.resources.Res
import com.kos.boxdrawer.generated.resources.tabBezier
import com.kos.boxdrawer.generated.resources.tabBox
import com.kos.boxdrawer.generated.resources.tabDxf
import com.kos.boxdrawer.generated.resources.tabGrid
import com.kos.boxdrawer.generated.resources.tabImage
import com.kos.boxdrawer.generated.resources.tabReka
import com.kos.boxdrawer.generated.resources.tabSettings
import com.kos.boxdrawer.generated.resources.tabSoft
import com.kos.boxdrawer.generated.resources.tabTor
import com.kos.boxdrawer.generated.resources.tabTortoise
import com.kos.boxdrawer.presentation.tabbar.ToolbarActionForBezier
import com.kos.boxdrawer.presentation.tabbar.ToolbarActionForBox
import com.kos.boxdrawer.presentation.tabbar.ToolbarActionForDxf
import com.kos.boxdrawer.presentation.tabbar.ToolbarActionForGrid
import com.kos.boxdrawer.presentation.tabbar.ToolbarActionForReka
import com.kos.boxdrawer.presentation.tabbar.ToolbarActionForSoft
import com.kos.boxdrawer.presentation.tabbar.ToolbarActionForTools
import com.kos.boxdrawer.presentation.tabbar.ToolbarActionForTortoise
import com.kos.boxdrawer.presentation.tabbar.ToolbarForBezier
import com.kos.boxdrawer.presentation.tabbar.ToolbarForBox
import com.kos.boxdrawer.presentation.tabbar.ToolbarForDxf
import com.kos.boxdrawer.presentation.tabbar.ToolbarForGrid
import com.kos.boxdrawer.presentation.tabbar.ToolbarForReka
import com.kos.boxdrawer.presentation.tabbar.ToolbarForSoft
import com.kos.boxdrawer.presentation.tabbar.ToolbarForTools
import com.kos.boxdrawer.presentation.tabbar.ToolbarForTortoise
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter


val TabContentModifier =
    Modifier.fillMaxWidth().fillMaxHeight().padding(vertical = 4.dp, horizontal = 16.dp)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TabBar(tabs: List<TabInfo>, vm: State<DrawerViewModel>) {
    val tabIndex = vm.value.tabIndex.collectAsState()
    val expandTools = remember { mutableStateOf(true) }

    Column(
        Modifier.fillMaxWidth().wrapContentHeight().background(Color.White)
    ) {
        Row(
            modifier = Modifier
        ) {
            Box(
                modifier = Modifier.background(MaterialTheme.colors.primarySurface).height(48.dp)
            ) {
                val icon = if (expandTools.value)
                    Expand.rememberExpandLess()
                else
                    Expand.rememberExpandMore()

                ImageButton(
                    icon,
                    modifier = Modifier,
                    enabled = true
                ) {
                    expandTools.value = !expandTools.value
                }
            }
            ScrollableTabRow(
                selectedTabIndex = tabIndex.value,
                modifier = Modifier.padding(vertical = 0.dp, horizontal = 0.dp),
            ) {
                tabs.forEach { info ->
                    Tab(
                        selected = info.id == tabIndex.value,
                        onClick = {
                            vm.value.tabIndex.value = info.id
                            expandTools.value = true
                        },
                        modifier = Modifier.padding(vertical = 0.dp, horizontal = 0.dp),
                        text = { Text(stringResource(info.title)) },
                    )
                }
            }
        }
        AnimatedVisibility(expandTools.value) {
            Box(
                Modifier.fillMaxWidth().height(220.dp).background(ThemeColors.tabBackground)
            ) {
                ToolbarContainer(
                    tabIndex = tabIndex,
                    content = {
                        when (tabIndex.value) {
                            TAB_BOX -> ToolbarForBox(vm.value.box)
                            TAB_TORTOISE -> ToolbarForTortoise(vm.value.tortoise)
                            TAB_GRID -> ToolbarForGrid(vm.value.grid)
                            TAB_SOFT -> ToolbarForSoft(vm.value.softRez)
                            TAB_BEZIER -> ToolbarForBezier(vm.value.bezier)
                            TAB_BUBLIK -> ToolbarForBublik(vm.value.bublik)
                            TAB_REKA -> ToolbarForReka(vm.value.rectData)
                            TAB_TOOLS -> ToolbarForTools(vm.value.options)
                            TAB_DXF -> ToolbarForDxf(vm.value.dxfData)
                            TAB_IMAGE -> ToolbarForImage(vm.value.imageData)
                        }
                    },
                    actionsBlock = {
                        Column {
                            AnimatedContent(
                                modifier = Modifier.weight(1f),
                                targetState = tabIndex.value,
                                transitionSpec = {
                                    fadeIn() togetherWith fadeOut()
                                }) { targetIndex ->
                                when (targetIndex) {
                                    TAB_BOX -> ToolbarActionForBox(vm.value.box)
                                    TAB_TORTOISE -> ToolbarActionForTortoise(vm.value.tortoise)
                                    TAB_GRID -> ToolbarActionForGrid(vm.value.grid)
                                    TAB_SOFT -> ToolbarActionForSoft(vm.value.softRez)

                                    TAB_BEZIER -> ToolbarActionForBezier(vm.value.bezier)
                                    TAB_BUBLIK -> ToolbarActionForBublik(vm.value.bublik)
                                    TAB_REKA -> ToolbarActionForReka(vm.value.rectData)
                                    TAB_TOOLS -> ToolbarActionForTools(vm.value.options)
                                    TAB_DXF -> ToolbarActionForDxf(vm.value.dxfData)
                                    TAB_IMAGE -> ToolbarActionForImage(vm.value.imageData)
                                }
                            }
                            BoardInfoBlock(
                                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                                tools = vm.value.tools,
                                onClick = {
                                    vm.value.tabIndex.value = TAB_TOOLS
                                })
                        }
                    }
                )
            }
        }
        Box(
            Modifier.fillMaxWidth().height(1.dp).background(Color.DarkGray)
        )
    }
}

@Composable
fun BoardInfoBlock(modifier: Modifier, tools: Tools, onClick: () -> Unit) {
    val selectedMovie = remember { tools.settings }
    val boardWeight = String.format("%1$,.2f", selectedMovie.value.boardWeight)
    Text(
        "${selectedMovie.value.name} ($boardWeight)",
        modifier = modifier.clickable { onClick() }
    )
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
    val title: StringResource,
)

@Immutable
object BoxDrawerToolBar {
    const val TAB_BOX = 0
    const val TAB_TORTOISE = 1
    const val TAB_GRID = 2
    const val TAB_SOFT = 3
    const val TAB_BEZIER = 4
    const val TAB_BUBLIK = 5
    const val TAB_REKA = 6
    const val TAB_DXF = 7
    const val TAB_TOOLS = 8
    const val TAB_IMAGE = 9

    val tabs = listOf(
        TabInfo(TAB_BOX, Res.string.tabBox),
        TabInfo(TAB_TORTOISE, Res.string.tabTortoise),
        TabInfo(TAB_GRID, Res.string.tabGrid),
        TabInfo(TAB_SOFT, Res.string.tabSoft),
        TabInfo(TAB_BEZIER, Res.string.tabBezier),
        TabInfo(TAB_BUBLIK, Res.string.tabTor),
        TabInfo(TAB_REKA, Res.string.tabReka),
        TabInfo(TAB_DXF, Res.string.tabDxf),
        TabInfo(TAB_TOOLS, Res.string.tabSettings),
        TabInfo(TAB_IMAGE, Res.string.tabImage),
    )
}