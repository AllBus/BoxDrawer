package com.kos.boxdrawe.presentation

import com.kos.boxdrawe.widget.BoxDrawerToolBar
import com.kos.figure.FigureEmpty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest

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
    val tabIndex = MutableStateFlow(BoxDrawerToolBar.TAB_TORTOISE)


    private val noneFigure = MutableStateFlow(FigureEmpty).asStateFlow()

    val figures = tabIndex.flatMapLatest { tab -> when (tab) {
            BoxDrawerToolBar.TAB_TORTOISE -> tortoise.figures
            BoxDrawerToolBar.TAB_SOFT -> softRez.figures
            BoxDrawerToolBar.TAB_BOX -> box.figures
            BoxDrawerToolBar.TAB_BUBLIK -> bublik.figures
            BoxDrawerToolBar.TAB_TOOLS -> tools.currentFigure
            else ->  noneFigure
        }
    }


    init {
        println("DrawerViewModel")
    }

    fun loadSettings(){
        tools.loadSettings()
        options.selectSettings(tools.ds())
    }

}

