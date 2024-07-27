package com.kos.boxdrawe.presentation

import com.kos.boxdrawe.widget.BoxDrawerToolBar
import com.kos.figure.FigureEmpty
import com.kos.figure.FigureInfo
import com.kos.figure.IFigure
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import vectors.Vec2
import java.awt.datatransfer.Transferable

class DrawerViewModel {

    private val noneFigure = MutableStateFlow(FigureEmpty).asStateFlow()
    private val _selectedItem = MutableStateFlow(emptyList<FigureInfo>())

    val tools = Tools()
    val tortoise = TortoiseData(tools)
    val softRez = SoftRezData(tools, tortoise)
    val box = BoxData(tools)
    val grid = GridData(tools)
    val template = TemplateData(tools, _selectedItem)
    val options = ToolsData(tools, template)
    val bezier = BezierData(tools)
    val bublik = BublikData(tools)
    val rectData = RekaToolsData(tools)
    val dxfData = DxfToolsData(tools)
    val tabIndex = MutableStateFlow(BoxDrawerToolBar.TAB_TORTOISE)


    @OptIn(ExperimentalCoroutinesApi::class)
    val figures = tabIndex.flatMapLatest { tab ->
        when (tab) {
            BoxDrawerToolBar.TAB_TORTOISE -> tortoise.figures
            BoxDrawerToolBar.TAB_SOFT -> softRez.figures
            BoxDrawerToolBar.TAB_BOX -> box.figures
            BoxDrawerToolBar.TAB_BUBLIK -> bublik.figures
            BoxDrawerToolBar.TAB_REKA -> rectData.figures
            BoxDrawerToolBar.TAB_TOOLS -> template.currentFigure
            BoxDrawerToolBar.TAB_DXF -> dxfData.currentFigure
            else -> noneFigure
        }
    }

    val selectedItem: StateFlow<List<FigureInfo>> get() = _selectedItem

//    @OptIn(ExperimentalCoroutinesApi::class)
//    val selectedItem: Flow<List<IFigure>> = tabIndex.flatMapLatest { tab ->
//        when (tab) {
//            //        BoxDrawerToolBar.TAB_TORTOISE ->
//            //        BoxDrawerToolBar.TAB_SOFT ->
//            //        BoxDrawerToolBar.TAB_BOX ->
//            //        BoxDrawerToolBar.TAB_BUBLIK ->
//            //        BoxDrawerToolBar.TAB_REKA ->
//            BoxDrawerToolBar.TAB_TOOLS -> template.selectedItem
//            BoxDrawerToolBar.TAB_DXF -> dxfData.selectedItem
//            else -> noneSelectedFigure
//        }
//    }

    suspend fun copy(): Transferable? {
        val tab = tabIndex.value
        val tf: SaveFigure? = modelAtTab(tab)
        return tf?.copy()
    }

    suspend fun save(fileName: String) {
        val tab = tabIndex.value
        tools.updateChooserDir(fileName)
        val tf: SaveFigure? = modelAtTab(tab)
        tf?.save(fileName)
    }

    fun modelAtTab(tab: Int): SaveFigure? {
        val tf: SaveFigure? = when (tab) {
            BoxDrawerToolBar.TAB_TORTOISE -> tortoise
            BoxDrawerToolBar.TAB_SOFT -> softRez
            BoxDrawerToolBar.TAB_BOX -> box
            BoxDrawerToolBar.TAB_BUBLIK -> bublik
            BoxDrawerToolBar.TAB_REKA -> rectData
            BoxDrawerToolBar.TAB_TOOLS -> template
            BoxDrawerToolBar.TAB_BEZIER -> bezier
            BoxDrawerToolBar.TAB_GRID -> grid
            BoxDrawerToolBar.TAB_DXF -> dxfData
            else -> null
        }
        return tf
    }

    suspend fun print(): String {
        val tab = tabIndex.value

        return when (tab) {
            BoxDrawerToolBar.TAB_TORTOISE -> tortoise.printCommand()
            BoxDrawerToolBar.TAB_SOFT -> ""
            BoxDrawerToolBar.TAB_BOX -> box.print()
            BoxDrawerToolBar.TAB_BUBLIK -> ""
            BoxDrawerToolBar.TAB_REKA -> rectData.print()
            BoxDrawerToolBar.TAB_TOOLS -> template.print()
            BoxDrawerToolBar.TAB_BEZIER -> bezier.print()
            BoxDrawerToolBar.TAB_GRID -> grid.print()
            BoxDrawerToolBar.TAB_DXF -> dxfData.print()
            else -> ""
        }
    }

    init {
        println("DrawerViewModel")
    }

    fun loadSettings() {
        tools.loadState()
        options.selectSettings(tools.ds())
        tortoise.loadState()
     }

    fun saveState(){
        tortoise.saveState()
        tools.saveState()
    }

    suspend fun onPress(point: Vec2, button: Int, scale: Float) {
        when (tabIndex.value) {
            BoxDrawerToolBar.TAB_REKA -> rectData.onPress(point, button, scale)
            BoxDrawerToolBar.TAB_TOOLS -> template.onPress(point, button, scale)
            BoxDrawerToolBar.TAB_DXF -> dxfData.onPress(point, button, scale, _selectedItem)
        }
    }

    fun setSelected(figures: List<FigureInfo>) {
        _selectedItem.value = figures
    }



}

