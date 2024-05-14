package com.kos.boxdrawe.presentation

import com.kos.boxdrawe.widget.BoxDrawerToolBar
import com.kos.figure.FigureEmpty
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import vectors.Vec2

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
        }
    }

}

