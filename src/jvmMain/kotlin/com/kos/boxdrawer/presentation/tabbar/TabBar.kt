package com.kos.boxdrawer.presentation.tabbar

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.TabRowDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.icons.Expand
import com.kos.boxdrawe.presentation.DrawerViewModel
import com.kos.boxdrawe.themes.ThemeColors
import com.kos.boxdrawe.widget.ImageButton
import com.kos.boxdrawe.widget.tabbar.ScrollableTabRowWithHotkeys
import com.kos.boxdrawe.widget.tabbar.ToolbarContainer
import com.kos.boxdrawe.widget.tabbar.TopTab
import com.kos.boxdrawer.generated.resources.Res
import com.kos.boxdrawer.generated.resources.tabBezier
import com.kos.boxdrawer.generated.resources.tabBox
import com.kos.boxdrawer.generated.resources.tabDxf
import com.kos.boxdrawer.generated.resources.tabFormula
import com.kos.boxdrawer.generated.resources.tabGrid
import com.kos.boxdrawer.generated.resources.tabImage
import com.kos.boxdrawer.generated.resources.tabReka
import com.kos.boxdrawer.generated.resources.tabSettings
import com.kos.boxdrawer.generated.resources.tabSoft
import com.kos.boxdrawer.generated.resources.tabTor
import com.kos.boxdrawer.generated.resources.tabTortoise
import com.kos.boxdrawer.presentation.tabbar.BoxDrawerToolBar.TAB_BEZIER
import com.kos.boxdrawer.presentation.tabbar.BoxDrawerToolBar.TAB_BOX
import com.kos.boxdrawer.presentation.tabbar.BoxDrawerToolBar.TAB_BUBLIK
import com.kos.boxdrawer.presentation.tabbar.BoxDrawerToolBar.TAB_DXF
import com.kos.boxdrawer.presentation.tabbar.BoxDrawerToolBar.TAB_FORMULA
import com.kos.boxdrawer.presentation.tabbar.BoxDrawerToolBar.TAB_GRID
import com.kos.boxdrawer.presentation.tabbar.BoxDrawerToolBar.TAB_IMAGE
import com.kos.boxdrawer.presentation.tabbar.BoxDrawerToolBar.TAB_REKA
import com.kos.boxdrawer.presentation.tabbar.BoxDrawerToolBar.TAB_SOFT
import com.kos.boxdrawer.presentation.tabbar.BoxDrawerToolBar.TAB_TOOLS
import com.kos.boxdrawer.presentation.tabbar.BoxDrawerToolBar.TAB_TORTOISE
import com.kos.compose.ImmutableList
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter


val TabContentModifier =
    Modifier.fillMaxWidth().fillMaxHeight().padding(vertical = 4.dp, horizontal = 16.dp)

@Composable
fun TabBar(tabs: ImmutableList<TabInfo>, vm: State<DrawerViewModel>) {
    val tabIndex = vm.value.tabIndex.collectAsState()
    val expandTools = remember { mutableStateOf(true) }
    val pagerState = rememberPagerState(pageCount = {
        BoxDrawerToolBar.tabs.list.size
    })

    LaunchedEffect(tabIndex.value) {
        pagerState.animateScrollToPage(tabIndex.value, animationSpec = tween(500))
    }

    Column(
        Modifier.fillMaxWidth().background(ThemeColors.tabBackground)
    ) {
        Row(
            modifier = Modifier
        ) {
            Box(
                modifier = Modifier.background(TabRowDefaults.primaryContainerColor).height(32.dp)
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
            ScrollableTabRowWithHotkeys(
                selectedTabIndex = tabIndex.value,
                modifier = Modifier.padding(vertical = 0.dp, horizontal = 0.dp).weight(1f),
                onTabSelected = remember(vm.value) {
                    { id ->
                        if (id >= 0 && id < tabs.list.size) {
                            vm.value.tabIndex.value = id

                            expandTools.value = true
                        }
                    }
                }
            ) {
                ToolbarTabs(tabs, tabIndex, vm, expandTools)
            }
            AnimatedVisibility(true) {
                TabBarActions(
                    modifier = Modifier.background(TabRowDefaults.primaryContainerColor).height(32.dp),
                    tabIndex = tabIndex,
                    vm= vm,
                )
            }
        }
        AnimatedVisibility(expandTools.value) {
            ToolbarContent(tabIndex, vm)
        }
        Box(
            Modifier.fillMaxWidth().height(1.dp).background(Color.DarkGray)
        )
    }
}

@Composable
fun TabBarActions(modifier: Modifier, tabIndex: State<Int>, vm: State<DrawerViewModel>) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ){
        when (tabIndex.value) {
            TAB_BOX -> ToolbarActionIconForBox(vm.value.box)
            TAB_TORTOISE -> ToolbarActionIconForTortoise(vm.value.tortoise)
            TAB_GRID -> ToolbarActionIconForGrid(vm.value.grid)
            TAB_SOFT -> ToolbarActionIconForSoft(vm.value.softRez)
            TAB_BEZIER -> ToolbarActionIconForBezier(vm.value.bezier)
            TAB_BUBLIK -> ToolbarActionIconForBublik(vm.value.bublik)
            TAB_REKA -> ToolbarActionIconForReka(vm.value.rectData)
            TAB_TOOLS -> ToolbarActionIconForTools(vm.value.options)
            TAB_DXF -> ToolbarActionIconForDxf(vm.value.dxfData)
            TAB_IMAGE -> {}
            TAB_FORMULA -> ToolbarActionIconForFormula(vm.value.formulaData)
        }
    }
}

@Composable
private fun ToolbarTabs(
    tabs: ImmutableList<TabInfo>,
    tabIndex: State<Int>,
    vm: State<DrawerViewModel>,
    expandTools: MutableState<Boolean>
) {
    tabs.forEach { info ->
        TopTab(
            selected = info.id == tabIndex.value,
            onClick = {
                vm.value.tabIndex.value = info.id
                expandTools.value = true
            },
            modifier = Modifier.padding(vertical = 0.dp, horizontal = 0.dp)
                .wrapContentHeight(),
            text = stringResource(info.title), //{ Text(stringResource(info.title)) },
        )
    }
}

@Composable
private fun ToolbarContent(
    tabIndex: State<Int>,
    vm: State<DrawerViewModel>
) {
    Box(
        Modifier.fillMaxWidth().height(220.dp)
    ) {
        ToolbarContainer(
            pagerState = tabIndex,
            content = { index ->
                when (index) {
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
                    TAB_FORMULA -> ToolbarForFormula(vm.value.formulaData)
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
                            TAB_FORMULA -> ToolbarActionForFormula(vm.value.formulaData)
                        }
                    }
                    BoardInfoBlock(
                        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                        tools = vm.value.tools,
                        onClick = remember(vm.value) {
                            {
                                vm.value.tabIndex.value = TAB_TOOLS
                            }
                        }
                    )
                }
            }
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

@Immutable
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
    const val TAB_FORMULA = 10

    val tabs = ImmutableList(
        listOf(
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
            TabInfo(TAB_FORMULA, Res.string.tabFormula),
        )
    )
}