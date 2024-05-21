package com.kos.boxdrawe.presentation

import androidx.compose.runtime.mutableStateOf
import com.kos.boxdrawe.widget.BoxDrawerToolBar
import com.kos.figure.FigureEmpty
import com.kos.figure.IFigure
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import vectors.Vec2
import java.awt.datatransfer.Transferable

class DrawerViewModel {

    val tools = Tools()
    val tortoise = TortoiseData(tools)
    val softRez = SoftRezData(tools, tortoise)
    val box = BoxData(tools)
    val grid = GridData(tools)
    val template = TemplateData(tools)
    val options = ToolsData(tools, template)
    val bezier = BezierData(tools)
    val bublik = BublikData(tools)
    val rectData = RekaToolsData(tools)
    val tabIndex = MutableStateFlow(BoxDrawerToolBar.TAB_TORTOISE)

    private val noneFigure = MutableStateFlow(FigureEmpty).asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val figures = tabIndex.flatMapLatest { tab -> when (tab) {
            BoxDrawerToolBar.TAB_TORTOISE -> tortoise.figures
            BoxDrawerToolBar.TAB_SOFT -> softRez.figures
            BoxDrawerToolBar.TAB_BOX -> box.figures
            BoxDrawerToolBar.TAB_BUBLIK -> bublik.figures
            BoxDrawerToolBar.TAB_REKA -> rectData.figures
            BoxDrawerToolBar.TAB_TOOLS -> template.currentFigure
            else -> noneFigure
        }
    }

    suspend fun copy(): Transferable? {
        val tab = tabIndex.value
        val tf: SaveFigure? = modelAtTab(tab)
        return tf?.copy()
    }

    suspend fun save(fileName:String){
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
            else -> null
        }
        return tf
    }

    suspend fun print():String{
        val tab = tabIndex.value

        return when (tab) {
            BoxDrawerToolBar.TAB_TORTOISE ->  tortoise.printCommand()
            BoxDrawerToolBar.TAB_SOFT -> ""
            BoxDrawerToolBar.TAB_BOX -> box.print()
            BoxDrawerToolBar.TAB_BUBLIK -> ""
            BoxDrawerToolBar.TAB_REKA -> rectData.print()
            BoxDrawerToolBar.TAB_TOOLS -> template.print()
            BoxDrawerToolBar.TAB_BEZIER -> bezier.print()
            BoxDrawerToolBar.TAB_GRID -> grid.print()
            else -> ""
        }
    }

    init {
        println("DrawerViewModel")
    }

    fun loadSettings(){
        tools.loadSettings()
        options.selectSettings(tools.ds())
    }

    suspend fun onPress(point: Vec2, button: Int, scale: Float) {
        when (tabIndex.value){
            BoxDrawerToolBar.TAB_REKA -> rectData.onPress(point, button, scale)
            BoxDrawerToolBar.TAB_TOOLS -> template.onPress(point, button, scale)
        }
    }

}

